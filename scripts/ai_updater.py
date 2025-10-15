"""AI-assisted automation for applying issue-driven updates to the repo."""

import os
import re

import google.generativeai as genai
from github import Github, GithubException


# Mapping of primary files to any related files that must stay in sync
RAW_RELATED_FILES = {
    "src/test/resources/features/login.feature": [
        "src/test/java/com/example/steps/LoginSteps.java",
    ],
}


def normalize_repo_path(path: str) -> str:
    """Convert user-supplied paths to normalized repo paths."""
    normalized = path.strip().replace("\\", "/")
    while normalized.startswith("./"):
        normalized = normalized[2:]
    while "//" in normalized:
        normalized = normalized.replace("//", "/")
    return normalized


def build_related_lookup(raw_mapping: dict[str, list[str]]) -> dict[str, list[str]]:
    """Normalize the related-files mapping and make it bi-directional."""
    related: dict[str, set[str]] = {}
    for primary, related_list in raw_mapping.items():
        primary_norm = normalize_repo_path(primary)
        rel_norm = [normalize_repo_path(item) for item in related_list]

        related.setdefault(primary_norm, set()).update(rel_norm)
        for rel in rel_norm:
            related.setdefault(rel, set()).add(primary_norm)

    return {path: sorted(peers) for path, peers in related.items()}


RELATED_FILES = build_related_lookup(RAW_RELATED_FILES)


def main() -> None:
    """Main function to orchestrate the AI-powered code update process."""
    # --- 1. Get required variables from GitHub Actions environment ---
    try:
        github_token = os.environ["GITHUB_TOKEN"]
        repo_name = os.environ["GITHUB_REPOSITORY"]
        issue_number = int(os.environ["ISSUE_NUMBER"])
        gemini_api_key = os.environ["GEMINI_API_KEY"]
    except KeyError as e:
        print(f"Error: Missing environment variable {e}. Make sure you have set up secrets correctly.")
        return

    # --- 2. Configure APIs ---
    g = Github(github_token)
    repo = g.get_repo(repo_name)

    genai.configure(api_key=gemini_api_key)
    model = genai.GenerativeModel("gemini-2.5-flash")

    # --- 3. Get Issue Details ---
    issue = repo.get_issue(number=issue_number)
    issue_title = issue.title
    issue_body = issue.body or ""

    try:
        raw_file_path = [line.split("File:", 1)[1].strip() for line in issue_body.split("\n") if "File:" in line][0]
        file_to_update = normalize_repo_path(raw_file_path)
    except IndexError:
        print("Error: Could not find 'File: <path>' in the issue body.")
        issue.create_comment(
            "AI Agent: I couldn't find the `File: <path>` in your issue description. Please specify the file to update."
        )
        return

    # --- 4. Gather the content for all files that must be updated together ---
    files_to_update = [file_to_update] + RELATED_FILES.get(file_to_update, [])
    files_to_update = list(dict.fromkeys(files_to_update))
    file_objects = {}
    original_contents = {}

    for path in files_to_update:
        try:
            file_obj = repo.get_contents(path)
            file_objects[path] = file_obj
            original_contents[path] = file_obj.decoded_content.decode("utf-8")
        except GithubException:
            print(f"Error: Could not find the file '{path}' in the repository.")
            issue.create_comment(
                f"AI Agent: I couldn't find the file `{path}` in the repository. Please check the path."
            )
            return

    # --- 5. Build the Prompt for the AI ---
    file_sections = []
    for path, content in original_contents.items():
        if path.endswith(".py"):
            fence = "python"
        elif path.endswith(".feature"):
            fence = "gherkin"
        elif path.endswith(".java"):
            fence = "java"
        else:
            fence = "text"
        file_sections.append(f"**Original File (`{path}`):**\n```{fence}\n{content}\n```")

    file_sections_text = "\n".join(file_sections)
    prompt = (
        "You are an expert software developer specializing in test automation. Your task is to update the provided files so they stay consistent with the user's request.\n"
        "**User's Request:**\n"
        f"{issue_title}\n{issue_body}\n"
        f"{file_sections_text}\n"
        "**Your Instructions:**\n"
        "1. Carefully review every file and update them to satisfy the request.\n"
        "2. Keep the feature files and their step definitions in sync when scenarios change.\n"
        "3. Respond using this exact format for each file (include all files listed above even if unchanged):\n"
        "<<<<<FILE:relative/path>>>>>>\n"
        "<entire updated file content>\n"
        "<<<<<END FILE>>>>>\n"
        "Repeat the file block for every file you are returning.\n"
        "4. Do not include any additional commentary, explanations, or markdown beyond the required file blocks."
    )

    # --- 6. Call the Gemini AI API ---
    print("Calling Gemini API...")
    try:
        response = model.generate_content(prompt)
        updated_code = response.text.strip()
        if not updated_code or "Traceback" in updated_code:
            raise ValueError("AI returned an empty or error response.")
    except Exception as e:  # noqa: BLE001
        print(f"Error calling Gemini API: {e}")
        issue.create_comment(
            f"AI Agent: I encountered an error with the AI model. It said: `{e}`. Please try again or rephrase your request."
        )
        return

    # --- 7. Parse the AI response into individual file updates ---
    file_blocks = re.findall(r"<<<<<FILE:(.*?)>>>>>>\n(.*?)\n<<<<<END FILE>>>>>", updated_code, re.DOTALL)
    if not file_blocks:
        issue.create_comment(
            "AI Agent: I could not parse the AI response. Please ensure the instructions are followed and try again."
        )
        print("Error: AI response missing formatted file blocks.")
        return

    updated_files = {normalize_repo_path(path): content for path, content in file_blocks}

    missing_files = [path for path in files_to_update if path not in updated_files]
    if missing_files:
        issue.create_comment(
            "AI Agent: The AI response did not include updates for the following files: " + ", ".join(missing_files)
        )
        print(f"Error: Missing files in AI response: {missing_files}")
        return

    # --- 8. Create a new branch and commit the changes ---
    new_branch_name = f"ai-update-issue-{issue_number}"
    source_branch = repo.get_branch(repo.default_branch)

    try:
        repo.create_git_ref(ref=f"refs/heads/{new_branch_name}", sha=source_branch.commit.sha)
    except GithubException as e:
        if e.status == 422:
            print(f"Branch {new_branch_name} already exists. Skipping branch creation.")
        else:
            raise

    commit_message = f"feat: AI updates files based on issue #{issue_number}"
    for idx, path in enumerate(files_to_update):
        new_content = updated_files[path]
        file_obj = file_objects[path]
        repo.update_file(
            path=path,
            message=commit_message if idx == 0 else f"{commit_message} ({path})",
            content=new_content,
            sha=file_obj.sha,
            branch=new_branch_name,
        )

    print(f"Changes committed to branch {new_branch_name}.")

    # --- 9. Create a Pull Request ---
    pr_title = f"AI Update for Issue #{issue_number}: {issue_title}"
    pr_body = (
        "This PR was automatically generated by an AI agent in response to issue "
        f"#{issue_number}.\n**Request:**\n> {issue_body}\nPlease review the changes before merging."
    )

    try:
        pr = repo.create_pull(
            title=pr_title,
            body=pr_body,
            head=new_branch_name,
            base=repo.default_branch,
        )
        print(f"Pull Request created: {pr.html_url}")
        issue.create_comment(
            f"AI Agent: I have created a Pull Request with the requested changes. You can review it here: {pr.html_url}"
        )
    except GithubException as e:
        print(f"Could not create PR. It might already exist. Error: {e}")
        issue.create_comment(
            f"AI Agent: I've pushed the changes to the `{new_branch_name}` branch, but I couldn't create a Pull Request. It might already exist."
        )


if __name__ == "__main__":
    main()

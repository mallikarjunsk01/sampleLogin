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


FILE_DIRECTIVE_PATTERN = re.compile(r"^\s*(?:[-*]\s*)?File\s*:\s*(.+)$", re.IGNORECASE)


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


def extract_step_texts(feature_content: str) -> list[str]:
    """Return the list of step texts (without the Given/When/Then keyword)."""
    prefixes = ("Given", "When", "Then", "And", "But")
    step_texts: list[str] = []

    for line in feature_content.splitlines():
        stripped = line.strip()
        if not stripped or stripped.startswith("#"):
            continue

        for prefix in prefixes:
            token = f"{prefix} "
            if stripped.startswith(token):
                text = stripped[len(token) :]
                if text:
                    step_texts.append(text)
                break

    return step_texts


def ensure_steps_have_definitions(step_texts: list[str], java_content: str) -> list[str]:
    """Return any step texts that are missing corresponding Java annotations."""
    if not step_texts:
        return []

    normalized_java = java_content.replace(r"\"", '"')
    missing: list[str] = []

    for step in step_texts:
        escaped = step.replace('"', r'\"')
        raw_pattern = f'("{step}")'
        escaped_pattern = f'("{escaped}")'
        if raw_pattern in normalized_java or escaped_pattern in java_content:
            continue
        missing.append(step)

    return missing


def resolve_file_in_repo(repo, path: str):
    """Resolve a repo file path, tolerating leading directory aliases."""
    normalized = normalize_repo_path(path)
    segments = normalized.split("/")
    candidates = [normalized]
    for index in range(1, len(segments)):
        candidate = "/".join(segments[index:])
        if candidate:
            candidates.append(candidate)

    last_error: GithubException | None = None
    for candidate in candidates:
        try:
            file_obj = repo.get_contents(candidate)
            return candidate, file_obj
        except GithubException as exc:  # store and keep trying shorter paths
            last_error = exc

    if last_error is not None:
        raise last_error
    raise GithubException(status=404, data={"message": f"File '{path}' not found."}, headers={})


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

    raw_file_path: str | None = None
    for line in issue_body.split("\n"):
        match = FILE_DIRECTIVE_PATTERN.match(line)
        if match:
            raw_file_path = match.group(1).strip()
            break

    try:
        if raw_file_path is None:
            raise IndexError
        file_to_update = normalize_repo_path(raw_file_path)
    except IndexError:
        print("Error: Could not find 'File: <path>' in the issue body.")
        issue.create_comment(
            "AI Agent: I couldn't find the `File: <path>` in your issue description. Please specify the file to update."
        )
        return

    # --- 4. Gather the content for all files that must be updated together ---
    try:
        resolved_primary_path, primary_file_obj = resolve_file_in_repo(repo, file_to_update)
    except GithubException:
        print(f"Error: Could not find the file '{file_to_update}' in the repository.")
        issue.create_comment(
            f"AI Agent: I couldn't find the file `{file_to_update}` in the repository. Please check the path."
        )
        return

    files_to_update = [resolved_primary_path] + RELATED_FILES.get(resolved_primary_path, [])
    file_objects = {}
    original_contents = {}
    resolved_paths: list[str] = []

    for path in files_to_update:
        try:
            resolved_path, file_obj = resolve_file_in_repo(repo, path)
        except GithubException:
            print(f"Error: Could not find the file '{path}' in the repository.")
            issue.create_comment(
                f"AI Agent: I couldn't find the file `{path}` in the repository. Please check the path."
            )
            return

        if resolved_path not in file_objects:
            file_objects[resolved_path] = file_obj
            original_contents[resolved_path] = file_obj.decoded_content.decode("utf-8")
        resolved_paths.append(resolved_path)

    files_to_update = list(dict.fromkeys(resolved_paths))

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

    # --- 7b. Validate step definitions cover all feature steps ---
    validation_errors: list[str] = []
    for feature_path in [path for path in files_to_update if path.endswith(".feature")]:
        feature_content = updated_files.get(feature_path)
        if not feature_content:
            continue

        step_texts = extract_step_texts(feature_content)
        if not step_texts:
            continue

        for related_path in RELATED_FILES.get(feature_path, []):
            if not related_path.endswith(".java"):
                continue
            java_content = updated_files.get(related_path)
            if java_content is None:
                continue
            missing_steps = ensure_steps_have_definitions(step_texts, java_content)
            if missing_steps:
                formatted = "; ".join(f'"{step}"' for step in missing_steps)
                validation_errors.append(
                    f"Missing step definitions in `{related_path}` for: {formatted}"
                )

    if validation_errors:
        message = "\n".join(validation_errors)
        issue.create_comment(
            "AI Agent: I could not complete the update because some feature steps are missing matching Java step definitions.\n"
            + message
        )
        print(f"Error: Step definition validation failed. {message}")
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
# # scripts/ai_updater.py
# import os
# import google.generativeai as genai
# from github import Github, GithubException
# def main():
#    """
#    Main function to orchestrate the AI-powered code update process.
#    """
#    # --- 1. Get required variables from GitHub Actions environment ---
#    try:
#        github_token = os.environ["GITHUB_TOKEN"]
#        repo_name = os.environ["GITHUB_REPOSITORY"]
#        issue_number = int(os.environ["ISSUE_NUMBER"])
#        gemini_api_key = os.environ["GEMINI_API_KEY"]
#    except KeyError as e:
#        print(f"Error: Missing environment variable {e}. Make sure you have set up secrets correctly.")
#        return
#    # --- 2. Configure APIs ---
#    # Authenticate with GitHub
#    g = Github(github_token)
#    repo = g.get_repo(repo_name)
#    # Configure Gemini AI
#    genai.configure(api_key=gemini_api_key)
#    model = genai.GenerativeModel('gemini-2.5-flash') # Using a fast and capable model
#    # --- 3. Get Issue Details ---
#    issue = repo.get_issue(number=issue_number)
#    issue_title = issue.title
#    issue_body = issue.body
#    # We need to parse the file to be updated from the issue body
#    # Let's assume the user specifies it like: "File: path/to/your/script.py"
#    try:
#        file_to_update = [line.split("File:")[1].strip() for line in issue_body.split('\n') if 'File:' in line][0]
#    except IndexError:
#        print("Error: Could not find 'File: <path>' in the issue body.")
#        issue.create_comment("AI Agent: I couldn't find the `File: <path>` in your issue description. Please specify the file to update.")
#        return
#    # --- 4. Get the content of the file to be updated ---
#    try:
#        file_content_obj = repo.get_contents(file_to_update)
#        original_file_content = file_content_obj.decoded_content.decode("utf-8")
#    except GithubException:
#        print(f"Error: Could not find the file '{file_to_update}' in the repository.")
#        issue.create_comment(f"AI Agent: I couldn't find the file `{file_to_update}` in the repository. Please check the path.")
#        return
#    # --- 5. Build the Prompt for the AI ---
#    prompt = f"""
#    You are an expert software developer specializing in automation scripts. Your task is to update a Python script based on a user's request.
#    **User's Request:**
#    {issue_title}
#    {issue_body}
#    **Original Python Script (`{file_to_update}`):**
#    ```python
#    {original_file_content}
#    ```
#    **Your Instructions:**
#    1. Carefully read the user's request and the original script.
#    2. Modify the script to implement the requested changes.
#    3. IMPORTANT: Your response must ONLY contain the full, updated Python code for the script. Do not include any explanations, greetings, or markdown formatting like ```python. Just the raw code.
#    """
#    # --- 6. Call the Gemini AI API ---
#    print("Calling Gemini API...")
#    try:
#        response = model.generate_content(prompt)
#        updated_code = response.text.strip()
#        # A simple check to ensure the AI didn't return an empty response
#        if not updated_code or "Traceback" in updated_code:
#            raise Exception("AI returned an empty or error response.")
#    except Exception as e:
#        print(f"Error calling Gemini API: {e}")
#        issue.create_comment(f"AI Agent: I encountered an error with the AI model. It said: `{e}`. Please try again or rephrase your request.")
#        return
#    # --- 7. Create a new branch and commit the changes ---
#    new_branch_name = f"ai-update-issue-{issue_number}"
#    source_branch = repo.get_branch(repo.default_branch)
#    # Create new branch from default branch
#    try:
#        repo.create_git_ref(ref=f"refs/heads/{new_branch_name}", sha=source_branch.commit.sha)
#    except GithubException as e:
#        if e.status == 422: # Branch already exists
#            print(f"Branch {new_branch_name} already exists. Skipping branch creation.")
#        else:
#            raise e
#    # Commit the updated file to the new branch
#    commit_message = f"feat: AI updates script based on issue #{issue_number}"
#    repo.update_file(
#        path=file_to_update,
#        message=commit_message,
#        content=updated_code,
#        sha=file_content_obj.sha,
#        branch=new_branch_name
#    )
#    print(f"Changes committed to branch {new_branch_name}.")
#    # --- 8. Create a Pull Request ---
#    pr_title = f"AI Update for Issue #{issue_number}: {issue_title}"
#    pr_body = f"""
#    This PR was automatically generated by an AI agent in response to issue #{issue_number}.
#    **Request:**
# > {issue_body}
#    Please review the changes before merging.
#    """
#    try:
#        pr = repo.create_pull(
#            title=pr_title,
#            body=pr_body,
#            head=new_branch_name,
#            base=repo.default_branch
#        )
#        print(f"Pull Request created: {pr.html_url}")
#        issue.create_comment(f"AI Agent: I have created a Pull Request with the requested changes. You can review it here: {pr.html_url}")
#    except GithubException as e:
#         print(f"Could not create PR. It might already exist. Error: {e}")
#         issue.create_comment(f"AI Agent: I've pushed the changes to the `{new_branch_name}` branch, but I couldn't create a Pull Request. It might already exist.")

# if __name__ == "__main__":
#    main()

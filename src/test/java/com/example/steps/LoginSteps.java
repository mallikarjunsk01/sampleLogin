
package com.example.steps;

import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.Assert;

public class LoginSteps {
    WebDriver driver;

    // Updated HTML content to include elements for profile and logout functionality
    private static final String HTML_CONTENT = "data:text/html,<html><body>"
        + "<div id='login_form_container'>"
        + "<form><input id='username'><input id='password'><button id='login'>Login</button></form>"
        + "<div id='error' style='display:none'>Invalid credentials</div>"
        + "</div>"
        + "<div id='logged_in_container' style='display:none'>"
        + "<div id='home'>Welcome, user!</div>"
        + "<a id='profile_menu_link' href='#'>Profile</a>"
        + "<button id='logout'>Logout</button>"
        + "</div>"
        + "<script>"
        + "function showLoginForm() {"
        + "  document.getElementById('login_form_container').style.display='block';"
        + "  document.getElementById('logged_in_container').style.display='none';"
        + "  document.getElementById('error').style.display='none';"
        + "  document.getElementById('username').value = '';"
        + "  document.getElementById('password').value = '';"
        + "}"
        + "function showLoggedInState() {"
        + "  document.getElementById('login_form_container').style.display='none';"
        + "  document.getElementById('logged_in_container').style.display='block';"
        + "}"
        + "document.getElementById('login').onclick=function(){"
        + "var u=document.getElementById('username').value;"
        + "var p=document.getElementById('password').value;"
        + "if(u==='demo'&&p==='demo123'){showLoggedInState();}"
        + "else{document.getElementById('error').style.display='block';}"
        + "return false;};"
        + "document.getElementById('logout').onclick=function(){"
        + "  showLoginForm();"
        + "  return false;"
        + "};"
        + "showLoginForm(); // Initial state when page loads"
        + "</script></body></html>";


    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        driver.get(HTML_CONTENT);
    }

    @When("I enter valid credentials")
    public void i_enter_valid_credentials() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
    }

    @Then("I should see the home page")
    public void i_should_see_the_home_page() {
        // With updated HTML, the 'home' div is inside 'logged_in_container'
        WebElement loggedInContainer = driver.findElement(By.id("logged_in_container"));
        Assert.assertTrue(loggedInContainer.isDisplayed());
        // Optionally, assert that the specific 'home' message is also displayed
        WebElement homeMessage = driver.findElement(By.id("home"));
        Assert.assertTrue(homeMessage.isDisplayed());
        driver.quit();
    }
//sdvbsdbv
	/*
	 * @When("I enter invalid credentials") public void
	 * i_enter_invalid_credentials() {
	 * driver.findElement(By.id("username")).sendKeys("wrong");
	 * driver.findElement(By.id("password")).sendKeys("wrong");
	 * driver.findElement(By.id("login")).click(); }
	 * 
	 * @Then("I should see an error message") public void
	 * i_should_see_an_error_message() { WebElement error =
	 * driver.findElement(By.id("error")); Assert.assertTrue(error.isDisplayed());
	 * driver.quit(); }
	 */

    @Given("I am on the login page ui were valid credetntials required")
    public void i_am_on_the_login_page_ui_were_valid_credentials_required() {
        // Reusing the same setup as the initial login page for consistency
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.get(HTML_CONTENT);
    }

    @When("I enter valid credentials log in will be success")
    public void i_enter_valid_credentials_login_will_be_success() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
        // Assert that the logged-in container is displayed after successful login
        WebElement loggedInContainer = driver.findElement(By.id("logged_in_container"));
        Assert.assertTrue(loggedInContainer.isDisplayed());
    }

    @And("I open my profile menu and check if im login successfully or not")
    public void i_open_my_profile_menu_and_check_if_im_login_successfully_or_not() {
        // Assert that the profile menu link is visible and click it
        WebElement profileLink = driver.findElement(By.id("profile_menu_link"));
        Assert.assertTrue(profileLink.isDisplayed());
        profileLink.click();
        // For this simple demo, clicking the profile link does not change the page.
        // We can re-assert that we are still in a logged-in state.
        WebElement loggedInContainer = driver.findElement(By.id("logged_in_container"));
        Assert.assertTrue(loggedInContainer.isDisplayed());
    }

    @Then("I should be able to log out successfully from login page ui successfully")
    public void i_should_be_able_to_log_out_successfully_from_login_page_ui_successfully() {
        // Click the logout button
        driver.findElement(By.id("logout")).click();
        // Assert that the login form container is now displayed, indicating successful logout
        WebElement loginFormContainer = driver.findElement(By.id("login_form_container"));
        Assert.assertTrue(loginFormContainer.isDisplayed());
        // Optionally, assert that the logged-in container is no longer displayed
        WebElement loggedInContainer = driver.findElement(By.id("logged_in_container"));
        Assert.assertFalse(loggedInContainer.isDisplayed());
        driver.quit();
    }
}
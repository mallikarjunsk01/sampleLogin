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

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        driver.get("data:text/html,<html><body>"
            + "<div id='loginForm'>"
            + "<form><input id='username'><input id='password'><button id='login'>Login</button></form>"
            + "<div id='error' style='display:none'>Invalid credentials</div>"
            + "</div>" // end loginForm
            + "<div id='home' style='display:none'>Welcome"
            + "<a id='profileMenuLink' href='#'>Profile</a>"
            + "<div id='profileDropdown' style='display:none'>"
            + "<button id='logout'>Logout</button>"
            + "</div>" // end profileDropdown
            + "</div>" // end home
            + "<div id='logoutSuccess' style='display:none'>You have been logged out.</div>"
            + "<script>"
            + "document.getElementById('login').onclick=function(){"
            + "var u=document.getElementById('username').value;"
            + "var p=document.getElementById('password').value;"
            + "if(u==='demo'&&p==='demo123'){"
            + "document.getElementById('loginForm').style.display='none';"
            + "document.getElementById('home').style.display='block';"
            + "document.getElementById('error').style.display='none';" // Hide error if it was shown
            + "}else{"
            + "document.getElementById('error').style.display='block';"
            + "}"
            + "return false;};"
            + "document.getElementById('profileMenuLink').onclick=function(){"
            + "var dropdown = document.getElementById('profileDropdown');"
            + "dropdown.style.display = (dropdown.style.display === 'none') ? 'block' : 'none';"
            + "return false;};"
            + "document.getElementById('logout').onclick=function(){"
            + "document.getElementById('home').style.display='none';"
            + "document.getElementById('profileDropdown').style.display='none';"
            + "document.getElementById('logoutSuccess').style.display='block';"
            + "document.getElementById('loginForm').style.display='block';" // Simulate return to login context
            + "document.getElementById('username').value = '';" // Clear fields
            + "document.getElementById('password').value = '';"
            + "return false;};"
            + "</script></body></html>");
    }

    @When("I enter valid credentials")
    public void i_enter_valid_credentials() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
    }

    @Then("I should see the home page")
    public void i_should_see_the_home_page() {
        WebElement home = driver.findElement(By.id("home"));
        Assert.assertTrue(home.isDisplayed());
        driver.quit();
    }

    @When("I enter valid credentials and log in")
    public void i_enter_valid_credentials_and_log_in() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
        WebElement home = driver.findElement(By.id("home"));
        Assert.assertTrue(home.isDisplayed()); // Ensure login was successful
    }

    @And("I open my profile menu")
    public void i_open_my_profile_menu() {
        WebElement profileMenuLink = driver.findElement(By.id("profileMenuLink"));
        profileMenuLink.click();
        WebElement profileDropdown = driver.findElement(By.id("profileDropdown"));
        Assert.assertTrue(profileDropdown.isDisplayed());
    }

    @Then("I should be able to log out successfully")
    public void i_should_be_able_to_log_out_successfully() {
        WebElement logoutButton = driver.findElement(By.id("logout"));
        logoutButton.click();
        WebElement logoutSuccessMessage = driver.findElement(By.id("logoutSuccess"));
        Assert.assertTrue(logoutSuccessMessage.isDisplayed());
        WebElement loginForm = driver.findElement(By.id("loginForm"));
        Assert.assertTrue(loginForm.isDisplayed()); // Should return to login state
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
}
package com.example.steps;

import io.cucumber.java.After;
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
            + "<div id='login-area' style='display:block;'>" // Initially visible
            + "<form><input id='username'><input id='password'><button id='login'>Login</button></form>"
            + "<div id='error' style='display:none'>Invalid credentials</div>"
            + "</div>"
            + "<div id='home-area' style='display:none;'>" // Initially hidden
            + "<div id='welcome-message'>Welcome</div>"
            + "<button id='profile-menu'>Profile</button>"
            + "<div id='profile-dropdown' style='display:none;'>" // Initially hidden
            + "<button id='logout'>Logout</button>"
            + "</div>"
            + "</div>"
            + "<div id='logout-success-message' style='display:none;'>You have been logged out.</div>" // Initially hidden
            + "<script>"
            // Login logic
            + "document.getElementById('login').onclick=function(){"
            + "var u=document.getElementById('username').value;"
            + "var p=document.getElementById('password').value;"
            + "if(u==='demo'&&p==='demo123'){document.getElementById('login-area').style.display='none';"
            + "document.getElementById('home-area').style.display='block';"
            + "document.getElementById('error').style.display='none';"
            + "document.getElementById('logout-success-message').style.display='none';}"
            + "else{document.getElementById('error').style.display='block';}"
            + "return false;};"
            // Profile menu logic
            + "document.getElementById('profile-menu').onclick=function(){"
            + "var dropdown = document.getElementById('profile-dropdown');"
            + "dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';"
            + "};"
            // Logout logic
            + "document.getElementById('logout').onclick=function(){"
            + "document.getElementById('home-area').style.display='none';"
            + "document.getElementById('profile-dropdown').style.display='none';"
            + "document.getElementById('login-area').style.display='block';"
            + "document.getElementById('logout-success-message').style.display='block';"
            + "document.getElementById('username').value = '';" // Clear input
            + "document.getElementById('password').value = '';" // Clear input
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
        WebElement homeArea = driver.findElement(By.id("home-area"));
        Assert.assertTrue(homeArea.isDisplayed());
    }

    @When("I enter valid credentials and log in")
    public void i_enter_valid_credentials_and_log_in() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
        WebElement homeArea = driver.findElement(By.id("home-area"));
        Assert.assertTrue(homeArea.isDisplayed()); // Assert successful login
    }

    @And("I open my profile menu")
    public void i_open_my_profile_menu() {
        driver.findElement(By.id("profile-menu")).click();
        WebElement profileDropdown = driver.findElement(By.id("profile-dropdown"));
        Assert.assertTrue(profileDropdown.isDisplayed());
    }

    @Then("I should be able to log out successfully")
    public void i_should_be_able_to_log_out_successfully() {
        driver.findElement(By.id("logout")).click();
        WebElement logoutSuccessMessage = driver.findElement(By.id("logout-success-message"));
        Assert.assertTrue(logoutSuccessMessage.isDisplayed());
        WebElement loginArea = driver.findElement(By.id("login-area"));
        Assert.assertTrue(loginArea.isDisplayed()); // Should return to login state
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
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
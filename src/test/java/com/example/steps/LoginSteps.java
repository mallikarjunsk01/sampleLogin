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

    // Helper method to get the base HTML content, updated to include new elements and logic
    private String getBaseHtmlContent() {
        return "data:text/html,<html><body>"
            + "<form><input id='username'><input id='password'><button id='login'>Login</button></form>"
            + "<div id='error' style='display:none'>Invalid credentials</div>"
            + "<div id='home' style='display:none'>"
            + "Welcome"
            + "<button id='profileMenu'>Profile</button>"
            + "<button id='cart'>Cart</button>"
            + "<button id='logout'>Logout</button>"
            + "</div>"
            + "<div id='profileDetails' style='display:none'>Logged in as demo</div>"
            + "<div id='cartPage' style='display:none'>Your Cart</div>"
            + "<div id='logoutSuccess' style='display:none'>You have been logged out.</div>"
            + "<script>"
            + "document.getElementById('login').onclick=function(){"
            + "var u=document.getElementById('username').value;"
            + "var p=document.getElementById('password').value;"
            + "if(u==='demo'&&p==='demo123'){document.getElementById('home').style.display='block';"
            + "document.getElementById('error').style.display='none';"
            + "document.getElementById('username').style.backgroundColor = 'lightgreen';"
            + "document.getElementById('password').style.backgroundColor = 'lightgreen';"
            + "}else{document.getElementById('home').style.display='none';"
            + "document.getElementById('error').style.display='block';"
            + "document.getElementById('username').style.backgroundColor = '';"
            + "document.getElementById('password').style.backgroundColor = '';"
            + "}"
            + "return false;};"
            + "document.getElementById('profileMenu').onclick=function(){"
            + "document.getElementById('profileDetails').style.display='block';"
            + "document.getElementById('cartPage').style.display='none';"
            + "document.getElementById('logoutSuccess').style.display='none';"
            + "};"
            + "document.getElementById('cart').onclick=function(){"
            + "document.getElementById('cartPage').style.display='block';"
            + "document.getElementById('profileDetails').style.display='none';"
            + "document.getElementById('logoutSuccess').style.display='none';"
            + "};"
            + "document.getElementById('logout').onclick=function(){"
            + "document.getElementById('home').style.display='none';"
            + "document.getElementById('profileDetails').style.display='none';"
            + "document.getElementById('cartPage').style.display='none';"
            + "document.getElementById('logoutSuccess').style.display='block';"
            + "document.getElementById('username').value = '';"
            + "document.getElementById('password').value = '';"
            + "document.getElementById('username').style.backgroundColor = '';"
            + "document.getElementById('password').style.backgroundColor = '';"
            + "};"
            + "</script></body></html>";
    }

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        driver.get(getBaseHtmlContent());
    }

    // Updated step definition for "Successful login" scenario's When step
    @When("Green colour should come while I enter valid credentials.")
    public void green_colour_should_come_while_i_enter_valid_credentials() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
        // Assert color change
        WebElement usernameInput = driver.findElement(By.id("username"));
        String backgroundColor = usernameInput.getCssValue("background-color");
        // Verify background color is lightgreen (rgb(144, 238, 144) or rgba with alpha)
        Assert.assertTrue("Username input should have a green background color after entering valid credentials",
                          backgroundColor.contains("rgb(144, 238, 144)") || backgroundColor.contains("rgba(144, 238, 144"));
    }

    @Then("I should see the home page")
    public void i_should_see_the_home_page() {
        WebElement home = driver.findElement(By.id("home"));
        Assert.assertTrue(home.isDisplayed());
        driver.quit();
    }

    // New step definitions for "Cart on page" scenario

    @Given("I am on the login page ui")
    public void i_am_on_the_login_page_ui() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        driver.get(getBaseHtmlContent());
    }

    @When("I enter valid credentials and log in will be on page")
    public void i_enter_valid_credentials_and_log_in_will_be_on_page() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
        WebElement home = driver.findElement(By.id("home"));
        Assert.assertTrue("Home page should be displayed after valid login", home.isDisplayed());
    }

    @And("I open my profile menu and check if im login not")
    public void i_open_my_profile_menu_and_check_if_im_login_not() {
        WebElement profileMenuButton = driver.findElement(By.id("profileMenu"));
        Assert.assertTrue("Profile menu button should be displayed on home page", profileMenuButton.isDisplayed());
        profileMenuButton.click();
        WebElement profileDetails = driver.findElement(By.id("profileDetails"));
        Assert.assertTrue("Profile details should be displayed after clicking profile menu", profileDetails.isDisplayed());
        // "check if im login not" is interpreted as verifying some logged-in state, which profile details confirm.
    }

    @Then("im clicking on cart option")
    public void im_clicking_on_cart_option() {
        WebElement cartButton = driver.findElement(By.id("cart"));
        Assert.assertTrue("Cart button should be displayed on home page", cartButton.isDisplayed());
        cartButton.click();
        WebElement cartPage = driver.findElement(By.id("cartPage"));
        Assert.assertTrue("Cart page should be displayed after clicking cart option", cartPage.isDisplayed());
    }

    @Then("I should be able to log out successfully from login page ui")
    public void i_should_be_able_to_log_out_successfully_from_login_page_ui() {
        WebElement logoutButton = driver.findElement(By.id("logout"));
        Assert.assertTrue("Logout button should be displayed on current page", logoutButton.isDisplayed());
        logoutButton.click();
        WebElement logoutSuccessMessage = driver.findElement(By.id("logoutSuccess"));
        Assert.assertTrue("Logout success message should be displayed", logoutSuccessMessage.isDisplayed());
        // Verify that the login form elements are visible again, indicating a successful logout
        WebElement usernameInput = driver.findElement(By.id("username"));
        Assert.assertTrue("Username input should be visible after logout", usernameInput.isDisplayed());
        driver.quit();
    }
}

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
            + "<form><input id='username'><input id='password'><button id='login'>Login</button></form>"
            + "<div id='error' style='display:none'>Invalid credentials</div>"
            + "<div id='home' style='display:none'>Welcome</div>"
            + "<script>"
            + "document.getElementById('login').onclick=function(){"
            + "var u=document.getElementById('username').value;"
            + "var p=document.getElementById('password').value;"
            + "if(u==='demo'&&p==='demo123'){document.getElementById('home').style.display='block';}"
            + "else{document.getElementById('error').style.display='block';}"
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

    @Given("I am on the login page ui")
    public void i_am_on_the_login_page_ui() {
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        driver.get("data:text/html,<html><body>"
            + "<div id='login-container'>"
            + "<form><input id='username'><input id='password'><button id='login'>Login</button></form>"
            + "<div id='error' style='display:none'>Invalid credentials</div>"
            + "</div>"
            + "<div id='home-page' style='display:none'>"
            + "<h1>Welcome Home!</h1>"
            + "<button id='profile-menu-btn'>My Profile</button>"
            + "<div id='profile-dropdown' style='display:none;'>"
            + "<p>Logged in as: demo</p>"
            + "<button id='cart-in-option'>Add to Cart</button>"
            + "</div>"
            + "<div id='cart-confirmation' style='display:none;'>Item added to cart successfully!</div>"
            + "</div>"
            + "<script>"
            + "document.getElementById('login').onclick=function(){"
            + "var u=document.getElementById('username').value;"
            + "var p=document.getElementById('password').value;"
            + "if(u==='demo'&&p==='demo123'){"
            + "document.getElementById('login-container').style.display='none';"
            + "document.getElementById('home-page').style.display='block';"
            + "}else{"
            + "document.getElementById('error').style.display='block';"
            + "}"
            + "return false;"
            + "};"
            + "document.getElementById('profile-menu-btn').onclick=function(){"
            + "var dropdown = document.getElementById('profile-dropdown');"
            + "dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';"
            + "};"
            + "document.getElementById('cart-in-option').onclick=function(){"
            + "document.getElementById('cart-confirmation').style.display='block';"
            + "};"
            + "</script></body></html>");
    }

    @When("I enter valid credentials and log in will be on page")
    public void i_enter_valid_credentials_and_log_in_will_be_on_page() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
        // Verify that the home page is displayed after login
        WebElement homePage = driver.findElement(By.id("home-page"));
        Assert.assertTrue("Home page should be displayed after successful login", homePage.isDisplayed());
    }

    @And("I open my profile menu and check if im login not")
    public void i_open_my_profile_menu_and_check_if_im_login_not() {
        driver.findElement(By.id("profile-menu-btn")).click();
        WebElement profileDropdown = driver.findElement(By.id("profile-dropdown"));
        Assert.assertTrue("Profile dropdown should be displayed after clicking profile menu", profileDropdown.isDisplayed());
        // Additional check for "login not" can be a simple assertion that 'demo' user info is visible, implying logged in
        WebElement loggedInUserInfo = driver.findElement(By.xpath("//div[@id='profile-dropdown']/p[contains(text(), 'Logged in as: demo')]"));
        Assert.assertTrue("User info should confirm 'demo' is logged in", loggedInUserInfo.isDisplayed());
    }

    @Then("im clicking on cartIn option")
    public void im_clicking_on_cartin_option() {
        driver.findElement(By.id("cart-in-option")).click();
    }

    @Then("I should be able to add the iteam to cart successfully")
    public void i_should_be_able_to_add_the_iteam_to_cart_successfully() {
        WebElement cartConfirmation = driver.findElement(By.id("cart-confirmation"));
        Assert.assertTrue("Cart confirmation message should be displayed", cartConfirmation.isDisplayed());
        driver.quit(); // Quit driver after the scenario completes
    }
}
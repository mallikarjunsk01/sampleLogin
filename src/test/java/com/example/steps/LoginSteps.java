
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
        // options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        driver.get("data:text/html,<html><body>"
            + "<form><input id='username_ui'><input id='password_ui'><button id='login_ui'>Login</button></form>"
            + "<div id='error_ui' style='display:none'>Invalid credentials</div>"
            + "<div id='home_ui' style='display:none'>Welcome, User! <button id='profileMenu'>Profile</button><button id='cartIn'>Cart (0)</button></div>"
            + "<div id='profileLoggedInStatus' style='display:none'>You are logged in.</div>"
            + "<div id='addItemSuccess' style='display:none'>Item added successfully!</div>"
            + "<script>"
            + "document.getElementById('login_ui').onclick=function(){"
            + "var u=document.getElementById('username_ui').value;"
            + "var p=document.getElementById('password_ui').value;"
            + "if(u==='user'&&p==='pass'){document.getElementById('home_ui').style.display='block';}"
            + "else{document.getElementById('error_ui').style.display='block';}"
            + "return false;};"
            + "document.getElementById('profileMenu').onclick=function(){"
            + "document.getElementById('profileLoggedInStatus').style.display='block';"
            + "};"
            + "document.getElementById('cartIn').onclick=function(){"
            + "document.getElementById('addItemSuccess').style.display='block';"
            + "};"
            + "</script></body></html>");
    }

    @When("I enter valid credentials and log in will be on page")
    public void i_enter_valid_credentials_and_log_in_will_be_on_page() {
        driver.findElement(By.id("username_ui")).sendKeys("user");
        driver.findElement(By.id("password_ui")).sendKeys("pass");
        driver.findElement(By.id("login_ui")).click();
        WebElement homePage = driver.findElement(By.id("home_ui"));
        Assert.assertTrue(homePage.isDisplayed()); // Verify successful login and presence on the home page
    }

    @And("I open my profile menu and check if im login not")
    public void i_open_my_profile_menu_and_check_if_im_login_not() {
        driver.findElement(By.id("profileMenu")).click();
        WebElement loginStatus = driver.findElement(By.id("profileLoggedInStatus"));
        Assert.assertTrue(loginStatus.isDisplayed()); // Check if the "You are logged in" status is displayed
    }

    @Then("im clicking on cartIn option")
    public void im_clicking_on_cartin_option() {
        driver.findElement(By.id("cartIn")).click();
    }

    @Then("I should be able add items successfully from login page ui")
    public void i_should_be_able_add_items_successfully_from_login_page_ui() {
        WebElement addItemSuccess = driver.findElement(By.id("addItemSuccess"));
        Assert.assertTrue(addItemSuccess.isDisplayed()); // Verify "Item added successfully!" message
        driver.quit(); // Clean up driver after scenario completion
    }
}
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
        // options.addArguments("--headless=new"); // Uncomment for headless execution
        driver = new ChromeDriver(options);
        driver.get("data:text/html,<html><body>"
            + "<form><input id='username'><input id='password'><button id='login'>Login</button></form>"
            + "<div id='error' style='display:none; color: red;'>Invalid credentials</div>"
            + "<div id='home' style='display:none; padding: 20px; border: 1px solid blue; margin-top: 10px;'>"
            + "Welcome, User!<br>"
            + "<button id='profileMenu'>Profile</button>"
            + "<div id='profileDropdown' style='display:none; border: 1px solid grey; padding: 10px; margin-top: 5px;'>"
            + "<span id='loggedInStatus'>Logged In: No</span><br>"
            + "<button id='cartOption'>My Cart</button><br>"
            + "<button id='logoutButton'>Logout</button>"
            + "</div>"
            + "</div>"
            + "<div id='loggedOutPage' style='display:none; color: green;'>You are logged out.</div>"
            + "<script>"
            + "document.getElementById('login').onclick=function(){"
            + "var u=document.getElementById('username').value;"
            + "var p=document.getElementById('password').value;"
            + "if(u==='demo'&&p==='demo123'){"
            + "document.getElementById('home').style.display='block';"
            + "document.getElementById('error').style.display='none';"
            + "document.getElementById('loggedInStatus').textContent = 'Logged In: Yes';"
            + "document.getElementById('username').value = '';"
            + "document.getElementById('password').value = '';"
            + "}"
            + "else{"
            + "document.getElementById('error').style.display='block';"
            + "document.getElementById('home').style.display='none';"
            + "}"
            + "return false;};"
            + "document.getElementById('profileMenu').onclick=function(){"
            + "var dropdown = document.getElementById('profileDropdown');"
            + "dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';"
            + "};"
            + "document.getElementById('logoutButton').onclick=function(){"
            + "document.getElementById('home').style.display='none';"
            + "document.getElementById('loggedOutPage').style.display='block';"
            + "document.getElementById('loggedInStatus').textContent = 'Logged In: No';"
            + "document.getElementById('profileDropdown').style.display='none';"
            + "};"
            + "</script></body></html>");
    }

    @When("I enter valid credentials and log in will be on page")
    public void i_enter_valid_credentials_and_log_in_will_be_on_page() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
        // Assert that home page is displayed after login
        WebElement home = driver.findElement(By.id("home"));
        Assert.assertTrue("Home page should be displayed after login", home.isDisplayed());
    }

    @And("I open my profile menu and check if im login not")
    public void i_open_my_profile_menu_and_check_if_im_login_not() {
        driver.findElement(By.id("profileMenu")).click();
        WebElement loggedInStatus = driver.findElement(By.id("loggedInStatus"));
        Assert.assertEquals("Logged In: Yes", loggedInStatus.getText());
        Assert.assertTrue("Profile dropdown should be displayed", driver.findElement(By.id("profileDropdown")).isDisplayed());
    }

    @Then("im clicking on cart option")
    public void im_clicking_on_cart_option() {
        WebElement cartOption = driver.findElement(By.id("cartOption"));
        Assert.assertTrue("Cart option should be present and clickable", cartOption.isDisplayed() && cartOption.isEnabled());
        cartOption.click();
        // In a real scenario, you'd assert on the outcome of clicking the cart option,
        // e.g., navigation to a cart page or an item being added to cart.
    }

    @Then("I should be able to log out successfully from login page ui")
    public void i_should_be_able_to_log_out_successfully_from_login_page_ui() {
        driver.findElement(By.id("logoutButton")).click();
        WebElement loggedOutPage = driver.findElement(By.id("loggedOutPage"));
        Assert.assertTrue("Logged out message should be displayed", loggedOutPage.isDisplayed());
        Assert.assertEquals("You are logged out.", loggedOutPage.getText());
        if (driver != null) {
            driver.quit();
        }
    }
}
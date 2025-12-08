package com.example.steps;

import io.cucumber.java.en.*;
import io.cucumber.java.After;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.Assert;

public class LoginSteps {
    WebDriver driver;

    // Common HTML for both scenarios to simplify setup
    private static final String COMMON_HTML =
        "<html><body>"
        + "<div id='login-section'>"
        + "<form><input id='username'><input id='password'><button id='login'>Login</button></form>"
        + "<div id='error' style='display:none'>Invalid credentials</div>"
        + "</div>"
        + "<div id='logged-in-section' style='display:none'>"
        + "<div id='home'>Welcome, User!</div>"
        + "<button id='profileMenuBtn'>Profile</button>"
        + "<div id='profileMenu' style='display:none'>"
        + "<a href='#' id='cartInOption'>CartIn</a>"
        + "</div>"
        + "<div id='cartStatus' style='display:none'>Item added to cart!</div>"
        + "</div>"
        + "<script>"
        + "document.getElementById('login').onclick = function() {"
        + "var u = document.getElementById('username').value;"
        + "var p = document.getElementById('password').value;"
        + "if (u === 'demo' && p === 'demo123') {"
        + "document.getElementById('login-section').style.display = 'none';"
        + "document.getElementById('logged-in-section').style.display = 'block';"
        + "} else {"
        + "document.getElementById('error').style.display = 'block';"
        + "}"
        + "return false;"
        + "};"
        + "document.getElementById('profileMenuBtn').onclick = function() {"
        + "var menu = document.getElementById('profileMenu');"
        + "menu.style.display = menu.style.display === 'block' ? 'none' : 'block';"
        + "};"
        + "document.getElementById('cartInOption').onclick = function() {"
        + "document.getElementById('cartStatus').style.display = 'block';"
        + "return false;"
        + "};"
        + "</script></body></html>";

    private void setupDriverAndPage(String htmlContent) {
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new"); // Uncomment for headless execution
        driver = new ChromeDriver(options);
        driver.get("data:text/html," + htmlContent);
    }

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        setupDriverAndPage(COMMON_HTML);
    }

    @Given("I am on the login page ui")
    public void i_am_on_the_login_page_ui() {
        setupDriverAndPage(COMMON_HTML);
    }

    @When("I enter valid credentials")
    public void i_enter_valid_credentials() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
    }

    @When("I enter valid credentials and log in will be on page")
    public void i_enter_valid_credentials_and_log_in_will_be_on_page() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
        WebElement home = driver.findElement(By.id("home"));
        Assert.assertTrue("Home page should be displayed after login", home.isDisplayed());
    }

    @And("I open my profile menu and check if im login not")
    public void i_open_my_profile_menu_and_check_if_im_login_not() {
        driver.findElement(By.id("profileMenuBtn")).click();
        WebElement profileMenu = driver.findElement(By.id("profileMenu"));
        Assert.assertTrue("Profile menu should be displayed", profileMenu.isDisplayed());
    }

    @Then("im clicking on cartIn option")
    public void im_clicking_on_cartin_option() {
        driver.findElement(By.id("cartInOption")).click();
    }

    @Then("I should be able to add the iteam to cart successfully")
    public void i_should_be_able_to_add_the_iteam_to_cart_successfully() {
        WebElement cartStatus = driver.findElement(By.id("cartStatus"));
        Assert.assertTrue("Cart status message should be displayed", cartStatus.isDisplayed());
        Assert.assertEquals("Expected cart status message", "Item added to cart!", cartStatus.getText());
    }

    @Then("I should see the home page")
    public void i_should_see_the_home_page() {
        WebElement home = driver.findElement(By.id("home"));
        Assert.assertTrue(home.isDisplayed());
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
	 * }
	 */
}
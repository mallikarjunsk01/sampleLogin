
package com.example.steps;

import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.Assert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginSteps {
    WebDriver driver;

    // Common HTML for all scenarios starting from login to support extended features
    private static final String APP_HTML = "data:text/html,<html><body>"
            + "<form id='login-form'>"
            + "<input id='username' value='demo'><input id='password' value='demo123'><button id='login'>Login</button>"
            + "</form>"
            + "<div id='error' style='display:none'>Invalid credentials</div>"
            + "<div id='home' style='display:none'>"
            + "<h1>Welcome Home!</h1>"
            + "<button id='profile-menu'>Profile</button>"
            + "<div id='profile-dropdown' style='display:none'>"
            + "<span>Logged in as: demo</span>"
            + "<button id='cart-option'>Cart Out</button>" // Changed to 'Cart Out' as per request
            + "<button id='logout'>Logout</button>"
            + "</div>"
            + "</div>"
            + "<div id='cart-page' style='display:none'>"
            + "<h2>Shopping Cart</h2>"
            + "<ul id='cart-items'>"
            + "<li id='item-1'>Item 1 <button class='delete-item' data-item-id='1'>Delete</button></li>"
            + "</ul>"
            + "<div id='cart-empty-message' style='display:none'>Your cart is empty.</div>"
            + "<button id='back-to-home'>Back to Home</button>" // Added a back button
            + "</div>"
            + "<script>"
            + "document.getElementById('login').onclick=function(){"
            + "var u=document.getElementById('username').value;"
            + "var p=document.getElementById('password').value;"
            + "if(u==='demo'&&p==='demo123'){document.getElementById('login-form').style.display='none';"
            + "document.getElementById('error').style.display='none';"
            + "document.getElementById('home').style.display='block';}"
            + "else{document.getElementById('error').style.display='block';}"
            + "return false;};"
            + "document.getElementById('profile-menu').onclick=function(){"
            + "var profileDropdown = document.getElementById('profile-dropdown');"
            + "if(profileDropdown.style.display === 'none'){profileDropdown.style.display='block';}"
            + "else{profileDropdown.style.display='none';}"
            + "};"
            + "document.getElementById('cart-option').onclick=function(){"
            + "document.getElementById('home').style.display='none';"
            + "document.getElementById('profile-dropdown').style.display='none';" // Hide dropdown when navigating
            + "document.getElementById('cart-page').style.display='block';"
            + "};"
            + "document.querySelectorAll('.delete-item').forEach(button => {"
            + "button.onclick=function(){"
            + "var itemId = this.getAttribute('data-item-id');"
            + "var itemElement = document.getElementById('item-' + itemId);"
            + "if(itemElement){itemElement.remove();}"
            + "if(document.getElementById('cart-items').children.length === 0){"
            + "document.getElementById('cart-empty-message').style.display='block';"
            + "}"
            + "};"
            + "});"
            + "document.getElementById('back-to-home').onclick=function(){"
            + "document.getElementById('cart-page').style.display='none';"
            + "document.getElementById('home').style.display='block';"
            + "};"
            + "</script></body></html>";

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless=new"); // Uncomment for headless execution
        driver = new ChromeDriver(options);
        driver.get(APP_HTML);
    }

    @Given("I am on the login page ui")
    public void i_am_on_the_login_page_ui() {
        i_am_on_the_login_page(); // Reusing the setup method
    }

    @When("I enter valid credentials")
    public void i_enter_valid_credentials() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
    }

    @When("I enter valid credentials and log in will be on page")
    public void i_enter_valid_credentials_and_log_in_will_be_on_page() {
        i_enter_valid_credentials();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("home")));
        Assert.assertTrue(driver.findElement(By.id("home")).isDisplayed());
    }

    @And("I open my profile menu and check if im login not")
    public void i_open_my_profile_menu_and_check_if_im_login_not() {
        driver.findElement(By.id("profile-menu")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement profileDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("profile-dropdown")));
        Assert.assertTrue(profileDropdown.isDisplayed());
        Assert.assertTrue(profileDropdown.getText().contains("Logged in as: demo"));
    }

    @Then("im clicking on cart out option")
    public void im_clicking_on_cart_out_option() {
        driver.findElement(By.id("cart-option")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cart-page")));
        Assert.assertTrue(driver.findElement(By.id("cart-page")).isDisplayed());
    }

    @Then("I should be able to delete the iteam from cart successfully")
    public void i_should_be_able_to_delete_the_iteam_from_cart_successfully() {
        WebElement deleteButton = driver.findElement(By.cssSelector("#item-1 .delete-item"));
        deleteButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cart-empty-message")));
        Assert.assertFalse(driver.findElements(By.id("item-1")).size() > 0); // Assert item is gone
        Assert.assertTrue(driver.findElement(By.id("cart-empty-message")).isDisplayed()); // Assert empty message is shown
        driver.quit(); // Clean up after the scenario
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
}
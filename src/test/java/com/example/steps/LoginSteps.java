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

    // Helper method to provide the extended HTML content for new scenarios
    private String getExtendedHtmlContent() {
        return "data:text/html,<html><body>"
            + "<form id='login-form'><input id='username'><input id='password'><button id='login'>Login</button></form>"
            + "<div id='error' style='display:none'>Invalid credentials</div>"
            + "<div id='home' style='display:none'>Welcome!"
            + "   <div id='profile-menu-container'>"
            + "       <button id='profile-menu-button'>Profile</button>"
            + "       <div id='profile-menu-dropdown' style='display:none; border:1px solid black; padding:5px; margin-top:5px;'><span>Logged In User: demo</span></div>"
            + "   </div>"
            + "   <button id='cart-out-option'>Go to Cart / Manage Cart</button>"
            + "   <div id='cart-items-container'>"
            + "       <h3>Your Cart</h3>"
            + "       <div id='item1' style='border:1px solid grey; margin:5px; padding:5px;'>Item 1 <button id='delete-item1'>Delete</button></div>"
            + "       <div id='item2' style='border:1px solid grey; margin:5px; padding:5px;'>Item 2 <button id='delete-item2'>Delete</button></div>"
            + "   </div>"
            + "</div>"
            + "<script>"
            + "document.getElementById('login').onclick=function(){"
            + "var u=document.getElementById('username').value;"
            + "var p=document.getElementById('password').value;"
            + "if(u==='demo'&&p==='demo123'){"
            + "document.getElementById('login-form').style.display='none';" // Hide login form
            + "document.getElementById('home').style.display='block';"
            + "}else{document.getElementById('error').style.display='block';}"
            + "return false;};"
            + "document.getElementById('profile-menu-button').onclick=function(){"
            + "var dropdown = document.getElementById('profile-menu-dropdown');"
            + "dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';"
            + "};"
            + "document.getElementById('delete-item1').onclick=function(){"
            + "var item = document.getElementById('item1');"
            + "if (item) { item.remove(); }"
            + "};"
            + "document.getElementById('delete-item2').onclick=function(){"
            + "var item = document.getElementById('item2');"
            + "if (item) { item.remove(); }"
            + "};"
            + "</script></body></html>";
    }

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        // Original HTML for the existing scenario
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

    @Given("I am on the login page ui")
    public void i_am_on_the_login_page_ui() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.get(getExtendedHtmlContent()); // Use the extended HTML for the new scenario
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
        // Assert that we are on the home page after login
        WebElement home = driver.findElement(By.id("home"));
        Assert.assertTrue("Home page should be displayed after successful login", home.isDisplayed());
    }

    @And("I open my profile menu and check if im login not")
    public void i_open_my_profile_menu_and_check_if_im_login_not() {
        // Ensure the profile menu button is visible (it's part of the home page, which is displayed after login)
        WebElement profileMenuButton = driver.findElement(By.id("profile-menu-button"));
        Assert.assertTrue("Profile menu button should be displayed", profileMenuButton.isDisplayed());
        profileMenuButton.click();

        // Check if the dropdown is displayed and contains logged-in user info
        WebElement profileMenuDropdown = driver.findElement(By.id("profile-menu-dropdown"));
        Assert.assertTrue("Profile menu dropdown should be displayed after clicking button", profileMenuDropdown.isDisplayed());
        Assert.assertTrue("Profile menu dropdown should contain login confirmation text", profileMenuDropdown.getText().contains("Logged In User: demo"));
    }

    @Then("im clicking on cart out option")
    public void im_clicking_on_cart_out_option() {
        // Assuming "cart out option" is a button/link that leads to cart management
        WebElement cartOutOption = driver.findElement(By.id("cart-out-option"));
        Assert.assertTrue("Cart out option button should be displayed", cartOutOption.isDisplayed());
        cartOutOption.click(); 
        // In this simple HTML, clicking might not change the page but makes it interactable.
        // The cart items are immediately visible on the 'home' page after login.
    }

    @Then("I should be able to delete the iteam from cart successfully")
    public void i_should_be_able_to_delete_the_iteam_from_cart_successfully() {
        // Check if item1 is present, click its delete button, then verify it's gone
        WebElement item1 = driver.findElement(By.id("item1"));
        Assert.assertTrue("Item 1 should be initially present in the cart", item1.isDisplayed());

        driver.findElement(By.id("delete-item1")).click();

        // Verify item1 is no longer present by checking if the element can be found
        // findElements returns an empty list if no elements are found, avoiding NoSuchElementException
        Assert.assertTrue("Item 1 should be successfully deleted and not present", driver.findElements(By.id("item1")).isEmpty());
        driver.quit(); // Quitting driver as it's the last step of this scenario
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
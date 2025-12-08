
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
            + "<div id='login-section'>"
            + "<form><input id='username'><input id='password'><button id='login'>Login</button></form>"
            + "<div id='error' style='display:none'>Invalid credentials</div>"
            + "</div>"

            + "<div id='home-section' style='display:none'>Welcome</div>"

            + "<div id='items-section' style='display:none'>"
            + "<h2>Available Items</h2>"
            + "<button id='add-item-button'>Add Item 1 to Cart</button>"
            + "</div>"

            + "<div id='cart-section' style='display:none'>"
            + "<h2>Your Cart</h2>"
            + "<ul id='cart-items'></ul>"
            + "</div>"

            + "<script>"
            + "document.getElementById('login').onclick=function(){"
            + "var u=document.getElementById('username').value;"
            + "var p=document.getElementById('password').value;"
            + "if(u==='demo'&&p==='demo123'){"
            + "document.getElementById('login-section').style.display='none';"
            + "document.getElementById('home-section').style.display='block';"
            + "document.getElementById('items-section').style.display='block';" // Immediately show items page after login
            + "}else{"
            + "document.getElementById('error').style.display='block';"
            + "}"
            + "return false;"
            + "};"

            + "document.getElementById('add-item-button').onclick=function(){"
            + "var cartItems = document.getElementById('cart-items');"
            + "var li = document.createElement('li');"
            + "li.textContent = 'Item 1 Added';"
            + "li.id = 'cart-item-1';" // Assign an ID to the added item for easier assertion
            + "cartItems.appendChild(li);"
            + "document.getElementById('cart-section').style.display='block';" // Show cart when item is added
            + "};"
            + "</script></body></html>");
    }

    @When("I enter valid credentials and log in will be on page")
    public void i_enter_valid_credentials_and_log_in_will_be_on_page() {
        driver.findElement(By.id("username")).sendKeys("demo");
        driver.findElement(By.id("password")).sendKeys("demo123");
        driver.findElement(By.id("login")).click();
        // The JavaScript already handles displaying the items-section after successful login
        WebElement itemsSection = driver.findElement(By.id("items-section"));
        Assert.assertTrue(itemsSection.isDisplayed()); // Verify we are on the items page
    }

    @And("im clicking on item which will add item")
    public void im_clicking_on_item_which_will_add_item() {
        driver.findElement(By.id("add-item-button")).click();
    }

    @Then("I should be able to see that item added in cart")
    public void i_should_be_able_to_see_that_item_added_in_cart() {
        WebElement cartSection = driver.findElement(By.id("cart-section"));
        Assert.assertTrue(cartSection.isDisplayed());
        WebElement cartItem = driver.findElement(By.id("cart-item-1"));
        Assert.assertTrue(cartItem.isDisplayed());
        Assert.assertEquals("Item 1 Added", cartItem.getText());
        driver.quit();
    }
}
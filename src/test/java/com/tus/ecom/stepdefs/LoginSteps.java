package com.tus.ecom.stepdefs;

import com.tus.ecom.pages.LoginPage;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginSteps {

    WebDriver driver;
    LoginPage loginPage;

    @Before
    public void setUp() {
        driver = new ChromeDriver();
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


    @Given("user is on login page")
    public void user_on_login_page() {
        driver.get("http://localhost:8080/login");
        loginPage = new LoginPage(driver);
    }

    @When("user enters username {string} and password {string}")
    public void enter_credentials(String user, String pass) {

        loginPage.enterUsername(user);
        loginPage.enterPassword(pass);
    }

    @And("clicks login")
    public void clicks_login() {
        loginPage.clickLogin();
    }

    @Then("user should see homepage")
    public void verify_dashboard() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("http://localhost:8080/"));

        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("http://localhost:8080/"));
        driver.quit();
    }

}
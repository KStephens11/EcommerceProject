package com.tus.ecom.stepdefs;

import com.tus.ecom.pages.LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginSteps {

    private final BaseSteps base;
    LoginPage loginPage;

    public LoginSteps(BaseSteps base) {
        this.base = base;
    }

    @Given("user is on login page")
    public void user_on_login_page() {
        base.driver.get("http://localhost:8080/login");
        loginPage = new LoginPage(base.driver);
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
    public void verify_homepage() {
        WebDriverWait wait = new WebDriverWait(base.driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/"));
        assertEquals("http://localhost:8080/", base.driver.getCurrentUrl());
    }

    @Then("the page should contain {string}")
    public void page_should_contain(String text) {
        WebDriverWait wait = new WebDriverWait(base.driver, Duration.ofSeconds(10));
        wait.until(d -> Objects.requireNonNull(d.getPageSource()).contains(text));
        assertTrue(Objects.requireNonNull(base.driver.getPageSource()).contains(text),
                "Expected page to contain: " + text);
    }

    @Then("user should remain on login page")
    public void user_remains_on_login_page() {
        WebDriverWait wait = new WebDriverWait(base.driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(Objects.requireNonNull(base.driver.getCurrentUrl()).contains("/login"));
    }
}

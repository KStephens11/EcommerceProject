package com.tus.ecom.stepdefs;

import com.tus.ecom.pages.RegisterPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterSteps {

    private final BaseSteps base;
    RegisterPage registerPage;

    public RegisterSteps(BaseSteps base) {
        this.base = base;
    }

    @Given("user is on register page")
    public void user_on_register_page() {
        base.driver.get("http://localhost:8080/register");
        registerPage = new RegisterPage(base.driver);
    }

    @When("user fills in username {string} and password {string}")
    public void fill_register_form(String user, String pass) {
        registerPage.enterUsername(user);
        registerPage.enterPassword(pass);
    }

    @And("clicks register")
    public void clicks_register() {
        registerPage.clickRegister();
    }

    @Then("user should be redirected to login page")
    public void verify_redirected_to_login() {
        WebDriverWait wait = new WebDriverWait(base.driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(base.driver.getCurrentUrl().contains("/login"),
                "Expected redirect to login page after registration");
    }

    @Then("user should see a registration warning")
    public void verify_registration_warning() {
        assertTrue(registerPage.isWarningVisible(),
                "Expected warning box to be visible for duplicate username");
    }
}

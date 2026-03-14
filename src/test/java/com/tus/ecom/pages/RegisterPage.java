package com.tus.ecom.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {

    WebDriver driver;
    WebDriverWait wait;

    By usernameField  = By.id("regUsername");
    By passwordField  = By.id("regPassword");
    By registerButton = By.cssSelector("#registerForm button[type='submit']");
    By warningBox     = By.id("warningBox");

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterUsername(String user) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        driver.findElement(usernameField).clear();
        driver.findElement(usernameField).sendKeys(user);
    }

    public void enterPassword(String pass) {
        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(pass);
    }

    public void clickRegister() {
        driver.findElement(registerButton).click();
    }

    public boolean isWarningVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(warningBox));
            return driver.findElement(warningBox).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

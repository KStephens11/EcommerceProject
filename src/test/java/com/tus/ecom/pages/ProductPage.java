package com.tus.ecom.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ProductPage {

    WebDriver driver;
    WebDriverWait wait;

    By productCards          = By.cssSelector(".product-card");
    By searchInput           = By.id("searchInput");
    By searchButton          = By.id("searchButton");
    By productManagementBtn  = By.id("productManagementBtn");
    By managementSection     = By.id("management-section");
    By managementTable       = By.id("managementProductTable");

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean areProductsVisible() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(productCards));
            return !driver.findElements(productCards).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void searchFor(String term) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        driver.findElement(searchInput).clear();
        driver.findElement(searchInput).sendKeys(term);
        driver.findElement(searchButton).click();
    }

    public boolean isProductManagementButtonVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(productManagementBtn));
            return driver.findElement(productManagementBtn).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickProductManagement() {
        wait.until(ExpectedConditions.elementToBeClickable(productManagementBtn));
        driver.findElement(productManagementBtn).click();
    }

    public boolean isManagementTableVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(managementSection));
            return driver.findElement(managementSection).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

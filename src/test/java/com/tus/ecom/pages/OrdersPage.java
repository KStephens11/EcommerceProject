package com.tus.ecom.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OrdersPage {

    WebDriver driver;
    WebDriverWait wait;

    By ordersBtn      = By.id("ordersBtn");
    By ordersSection  = By.id("orders-section");
    By statsView      = By.id("statsView");
    By ordersTab      = By.id("ordersTab");
    By ordersView     = By.id("ordersView");
    By ordersTable    = By.id("ordersTableBody");
    By totalRevenue   = By.id("totalRevenue");

    public OrdersPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickOrders() {
        wait.until(ExpectedConditions.elementToBeClickable(ordersBtn));
        driver.findElement(ordersBtn).click();
    }

    public boolean isOrdersSectionVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(ordersSection));
            return driver.findElement(ordersSection).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickOrdersListTab() {
        wait.until(ExpectedConditions.elementToBeClickable(ordersTab));
        driver.findElement(ordersTab).click();
    }

    public boolean isOrdersTableVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(ordersView));
            return driver.findElement(ordersView).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isStatsViewVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(statsView));
            return driver.findElement(statsView).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTotalRevenueDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(totalRevenue));
            String text = driver.findElement(totalRevenue).getText();
            return !text.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}

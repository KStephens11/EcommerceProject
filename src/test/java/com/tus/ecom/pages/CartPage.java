package com.tus.ecom.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CartPage {

    WebDriver driver;
    WebDriverWait wait;

    // Navbar cart button
    By cartBtn        = By.id("cartBtn");
    By cartCount      = By.id("cartCount");

    // Cart offcanvas
    By cartOffcanvas  = By.id("cartOffcanvas");
    By cartItems      = By.id("cartItems");
    By checkoutBtn    = By.id("checkoutBtn");

    // Product grid - first card
    By firstProduct   = By.cssSelector(".product-card");

    // Product detail modal
    By productModal   = By.id("productModal");
    By addToCartBtn   = By.id("addToCartBtn");
    By modalCloseBtn  = By.cssSelector("#productModal .btn-secondary[data-bs-dismiss='modal']");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickFirstProduct() {
        wait.until(ExpectedConditions.elementToBeClickable(firstProduct));
        driver.findElements(firstProduct).getFirst().click();
    }

    public void clickAddToCart() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(productModal));
        wait.until(ExpectedConditions.elementToBeClickable(addToCartBtn));
        driver.findElement(addToCartBtn).click();
    }

    public void closeProductModal() {
        driver.findElement(modalCloseBtn).click();
        // Wait for modal to fully hide before continuing
        wait.until(ExpectedConditions.invisibilityOfElementLocated(productModal));
    }

    public void openCart() {
        wait.until(ExpectedConditions.elementToBeClickable(cartBtn));
        driver.findElement(cartBtn).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartOffcanvas));
    }

    public void clickCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(checkoutBtn));
        driver.findElement(checkoutBtn).click();
    }

    public boolean isCartEmpty() {
        try {
            // After checkout cart empties — cartCount returns to 0
            wait.until(d -> {
                String count = d.findElement(cartCount).getText();
                return "0".equals(count);
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getCartCount() {
        try {
            return Integer.parseInt(driver.findElement(cartCount).getText().trim());
        } catch (Exception e) {
            return 0;
        }
    }
}

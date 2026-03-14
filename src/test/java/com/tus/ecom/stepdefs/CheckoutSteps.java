package com.tus.ecom.stepdefs;

import com.tus.ecom.pages.CartPage;
import com.tus.ecom.pages.OrdersPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckoutSteps {

    private final BaseSteps base;
    CartPage   cartPage;
    OrdersPage ordersPage;

    public CheckoutSteps(BaseSteps base) {
        this.base       = base;
        this.cartPage   = new CartPage(base.driver);
        this.ordersPage = new OrdersPage(base.driver);
    }

    @When("the user clicks the first product card")
    public void click_first_product() {
        // Wait for products to load from the API before clicking
        WebDriverWait wait = new WebDriverWait(base.driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".product-card")));
        cartPage.clickFirstProduct();
    }

    @And("the user clicks Add to Cart in the modal")
    public void click_add_to_cart() {
        cartPage.clickAddToCart();
    }

    @And("the user closes the product modal")
    public void close_product_modal() {
        cartPage.closeProductModal();
    }

    @And("the user opens the cart")
    public void open_cart() {
        cartPage.openCart();
    }

    @And("the user clicks Checkout")
    public void click_checkout() {
        cartPage.clickCheckout();
    }

    @Then("the cart should be empty after checkout")
    public void cart_empty_after_checkout() {
        assertTrue(cartPage.isCartEmpty(),
                "Expected cart to be empty after checkout but count was: "
                + cartPage.getCartCount());
    }

    @Then("a new order should be visible in the orders table")
    public void new_order_visible() {
        // Orders table body should have at least one row that is not the "no orders" placeholder
        WebDriverWait wait = new WebDriverWait(base.driver, Duration.ofSeconds(10));

        By orderRow = By.cssSelector("#ordersTableBody tr td:first-child");

        wait.until(ExpectedConditions.presenceOfElementLocated(orderRow));

        String firstCell = base.driver.findElement(orderRow).getText();

        assertTrue(firstCell.startsWith("#"),
                "Expected order row with ID like '#1' but found: " + firstCell);
    }
}

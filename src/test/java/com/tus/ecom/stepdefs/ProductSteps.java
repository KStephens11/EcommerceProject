package com.tus.ecom.stepdefs;

import com.tus.ecom.pages.ProductPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductSteps {

    private final BaseSteps base;
    ProductPage productPage;

    public ProductSteps(BaseSteps base) {
        this.base = base;
        this.productPage = new ProductPage(base.driver);
    }

    @Then("products should be visible on the page")
    public void products_visible() {
        assertTrue(productPage.areProductsVisible(),
                "Expected product cards to be visible on the page");
    }

    @When("user searches for {string}")
    public void user_searches_for(String term) {
        productPage.searchFor(term);
    }

    @Then("search results should be displayed")
    public void search_results_displayed() {
        // After search, product cards should still render (even if empty message)
        WebDriverWait wait = new WebDriverWait(base.driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
        assertTrue(base.driver.getPageSource().contains("product-card")
                || base.driver.getPageSource().contains("No products found"),
                "Expected search results or empty message");
    }

    @Then("the product management button should be visible")
    public void product_management_button_visible() {
        assertTrue(productPage.isProductManagementButtonVisible(),
                "Expected Product Management button to be visible for admin");
    }

    @When("admin clicks Product Management")
    public void admin_clicks_product_management() {
        productPage.clickProductManagement();
    }

    @Then("the product management table should be visible")
    public void management_table_visible() {
        assertTrue(productPage.isManagementTableVisible(),
                "Expected product management section to be visible");
    }
}

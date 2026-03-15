package com.tus.ecom.stepdefs;

import com.tus.ecom.pages.OrdersPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrdersSteps {

    OrdersPage ordersPage;

    public OrdersSteps(BaseSteps base) {
        this.ordersPage = new OrdersPage(base.driver);
    }

    @When("admin clicks Orders")
    public void admin_clicks_orders() {
        ordersPage.clickOrders();
    }

    @Then("the orders section should be visible")
    public void orders_section_visible() {
        assertTrue(ordersPage.isOrdersSectionVisible(),
                "Expected orders section to be visible");
    }

    @And("admin clicks the Orders List tab")
    public void admin_clicks_orders_list_tab() {
        ordersPage.clickOrdersListTab();
    }

    @Then("the orders table should be visible")
    public void orders_table_visible() {
        assertTrue(ordersPage.isOrdersTableVisible(),
                "Expected orders list view to be visible");
    }

    @Then("the statistics view should be visible")
    public void stats_view_visible() {
        assertTrue(ordersPage.isStatsViewVisible(),
                "Expected statistics view to be visible");
    }

    @And("the total revenue should be displayed")
    public void total_revenue_displayed() {
        assertTrue(ordersPage.isTotalRevenueDisplayed(),
                "Expected total revenue element to be displayed");
    }
}

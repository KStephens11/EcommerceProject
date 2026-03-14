Feature: Orders and Statistics

  Background:
    Given user is logged in as "admin" with password "admin"

  Scenario: Admin can navigate to Orders panel
    When admin clicks Orders
    Then the orders section should be visible

  Scenario: Orders list is displayed
    When admin clicks Orders
    And admin clicks the Orders List tab
    Then the orders table should be visible

  Scenario: Statistics panel is displayed with revenue
    When admin clicks Orders
    Then the statistics view should be visible
    And the total revenue should be displayed

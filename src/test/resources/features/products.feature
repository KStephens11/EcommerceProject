Feature: Products

  Background:
    Given user is logged in as "admin" with password "admin"

  Scenario: Products are displayed on the homepage
    Then the page should contain "Ecommerce Products"
    And products should be visible on the page

  Scenario: User can search for a product
    When user searches for "laptop"
    Then search results should be displayed

  Scenario: Admin can see Product Management button
    Then the product management button should be visible

  Scenario: Admin can navigate to Product Management
    When admin clicks Product Management
    Then the product management table should be visible

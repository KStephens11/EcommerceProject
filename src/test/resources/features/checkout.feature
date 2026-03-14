Feature: Checkout and Order Verification

  Scenario: Customer adds product to cart, checks out, and admin verifies the order
    Given user is logged in as "admin" with password "admin"
    When the user clicks the first product card
    And the user clicks Add to Cart in the modal
    And the user closes the product modal
    And the user opens the cart
    And the user clicks Checkout
    Then the cart should be empty after checkout
    And admin clicks Orders
    And admin clicks the Orders List tab
    Then a new order should be visible in the orders table

Feature: Login

  Scenario: Successful login with valid credentials
    Given user is on login page
    When user enters username "admin" and password "admin"
    And clicks login
    Then user should see homepage
    And the page should contain "Ecommerce Products"

  Scenario: Failed login with invalid credentials
    Given user is on login page
    When user enters username "wronguser" and password "wrongpass"
    And clicks login
    Then user should remain on login page

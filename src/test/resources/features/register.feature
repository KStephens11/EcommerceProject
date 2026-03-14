Feature: Register

  Scenario: Successful registration with new credentials
    Given user is on register page
    When user fills in username "testuser123" and password "Password123"
    And clicks register
    Then user should be redirected to login page

  Scenario: Registration fails with duplicate username
    Given user is on register page
    When user fills in username "admin" and password "Password123"
    And clicks register
    Then user should see a registration warning

  Scenario: Registration fails when password has no capital letter
    Given user is on register page
    When user fills in username "testuser123" and password "password123"
    And clicks register
    Then user should see a registration warning

  Scenario: Registration fails when password is less than 8 characters
    Given user is on register page
    When user fills in username "testuser123" and password "Pass1"
    And clicks register
    Then user should see a registration warning

  Scenario: Registration fails when password has no number
    Given user is on register page
    When user fills in username "testuser123" and password "PasswordNoNumber"
    And clicks register
    Then user should see a registration warning

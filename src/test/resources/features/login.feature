Feature: Login Feature

  Scenario: User logs in successfully
    Given user is on login page
    When user enters username "admin" and password "admin"
    And clicks login
    Then user should see homepage
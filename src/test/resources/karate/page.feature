Feature: Page Controller Integration Test

  Background:
    * url baseUrl
    * configure followRedirects = false

  Scenario: Should load index page

    # Login first to obtain JWT cookie
    Given path '/api/auth/login'
    And request
    """
    {
        "username": "admin",
        "password": "admin"
    }
    """
    When method post
    Then status 200

    * def jwtCookie = responseCookies.jwt

    Given path '/'
    And cookie jwt = jwtCookie.value

    When method get
    Then status 200

    # in navbar
    And match response contains 'Ecommerce'


  Scenario: Should load login page

    Given path '/login'
    When method get
    Then status 200

    And match response contains 'Sign In'


  Scenario: Should load register page

    Given path '/register'
    When method get
    Then status 200

    And match response contains 'Register'
Feature: Auth Login API Integration Test

  Background:
    * url baseUrl
    * configure followRedirects = false

  Scenario: Successful login should return 200 and set JWT cookie

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

    And match response == ''

    And match responseHeaders['Set-Cookie'][0] contains 'jwt='


  Scenario: Logout should clear JWT cookie

    Given path '/api/auth/logout'
    When method post

    Then status 302

    And match responseCookies.jwt == '#notpresent'

  Scenario: Login with wrong credentials should return 302

    Given path '/api/auth/login'
    And request
    """
    {
        "username": "wrong",
        "password": "wrong"
    }
    """

    When method post
    Then status 302
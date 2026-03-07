Feature: User Controller Integration Test

  Background:
    * url baseUrl
    * configure followRedirects = false

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
    * cookie jwt = jwtCookie.value


  Scenario: Get all users

    Given path '/api/users'
    When method get
    Then status 200
    And match response == '#array'


  Scenario: Get user by ID

    Given path '/api/users/register'
    And request
    """
    {
        "username": "test_user",
        "password": "Password123"
    }
    """
    When method post
    Then status 201

    * def userId = response.id

    Given path '/api/users/', userId
    When method get
    Then status 200
    And match response.username == 'test_user'


  Scenario: Register user with invalid data should return BAD REQUEST

    Given path '/api/users/register'
    And request
    """
    {
        "username": "",
        "password": "123"
    }
    """
    When method post
    Then status 400
    And match response.message != null


  Scenario: Get logged in user profile

    Given path '/api/users/me'
    When method get
    Then status 200
    And match response.username == 'admin'
    And match response.roles == '#array'
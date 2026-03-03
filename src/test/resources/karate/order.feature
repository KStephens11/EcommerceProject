Feature: Order API Integration Test

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


  Scenario: Create order should return 201 and order response

    Given path '/api/orders'

    And cookie jwt = jwtCookie.value

    And request
    """
    {
        "items": [
            {
                "productId": 1,
                "quantity": 1
            }
        ]
    }
    """

    When method post

    Then status 201
    And match response.id != null
    And match response.items[0].productId == 1


  Scenario: Get all orders

    Given path '/api/orders'

    And cookie jwt = jwtCookie.value

    When method get

    Then status 200
    And match response != null
    And match response[0].username == 'admin'


  Scenario: Get sales statistics by category

    Given path '/api/orders/stats/sales-by-category'

    And cookie jwt = jwtCookie.value

    When method get

    Then status 200
    And match response == '#array'
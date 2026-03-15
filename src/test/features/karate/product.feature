Feature: Product Controller Integration Test

  Background:
    * url baseUrl
    * configure followRedirects = false

    # Login and capture JWT cookie
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


  Scenario: Add product

    Given path '/api/products'

    And request
      """
      {
        "name": "Test Product",
        "brand": 'Test Brand',
        "category": "Test Category",
        "price": 100,
        "quantity": 10
      }
      """

    When method post

    Then status 201
    And match response.name == 'Test Product'
    And match response.quantity == 10


  Scenario: Get products list

    Given path '/api/products'
    And param page = 0
    And param size = 10

    When method get

    Then status 200
    And match response.content == '#array'


  Scenario: Get product by ID

    Given path '/api/products'

    And request
      """
      {
        "name": "Test Product X",
        "brand": 'Test Brand',
        "category": "Test Category",
        "price": 100,
        "quantity": 10
      }
      """

    When method post
    Then status 201

    * def productId = response.id


  Scenario: Update product

    * def productId = 1

    Given path '/api/products/', productId

    And request
      """
      {
        "name": "Updated Product",
        "brand": 'Updated Brand',
        "category": "Updated Category",
        "price": 150,
        "quantity": 20
      }
      """

    When method put

    Then status 200
    And match response.name == 'Updated Product'


  Scenario: Delete product

    Given path '/api/products'
    And request
      """
      {
        "name": "Delete Test Product",
        "brand": 'Delete Test Brand',
        "category": "Delete Test Category",
        "price": 50,
        "quantity": 5
      }
      """
    When method post
    Then status 201

    * def productId = response.id

    Given path '/api/products', productId
    When method delete

    Then status 200


  Scenario: Search product by name

    Given path '/api/products/name'
    And param name = 'Test'

    When method get

    Then status 200
    And match response.content == '#array'


  Scenario: Get low stock products

    Given path '/api/products/low-stock'
    And param threshold = 5

    When method get

    Then status 200
    And match response == '#array'

  Scenario: Upload product image

    Given path '/api/products/upload-image'
    And multipart file file = { read: 'classpath:test-image.png', filename: 'test-image.png', contentType: 'image/png' }

    When method post

    Then status 200
    And match response contains '/uploads/products/'

  Scenario: Get product by invalid ID should return 404

    Given path '/api/products/999999'
    When method get

    Then status 404

  Scenario: Get product by invalid ID should return 404

    Given path '/api/products/999999'
    When method get

    Then status 404

  Scenario: Search products with blank name should return list

    Given path '/api/products/name'
    And param name = ' '

    When method get

    Then status 200
    And match response.content == '#array'

  Scenario: Add product with missing name should return 400

    Given path '/api/products'
    And request
    """
    {
      "brand": "Test Brand",
      "category": "Test Category",
      "price": 100,
      "quantity": 10
    }
    """
    When method post
    Then status 400
    And match response.message == 'Product name is required.'


  Scenario: Add product with missing brand should return 400

    Given path '/api/products'
    And request
    """
    {
      "name": "Test Product",
      "category": "Test Category",
      "price": 100,
      "quantity": 10
    }
    """
    When method post
    Then status 400
    And match response.message == 'Brand is required.'


  Scenario: Add product with missing category should return 400

    Given path '/api/products'
    And request
    """
    {
      "name": "Test Product",
      "brand": "Test Brand",
      "price": 100,
      "quantity": 10
    }
    """
    When method post
    Then status 400
    And match response.message == 'Category is required.'


  Scenario: Add product with negative price should return 400

    Given path '/api/products'
    And request
    """
    {
      "name": "Test Product",
      "brand": "Test Brand",
      "category": "Test Category",
      "price": -10,
      "quantity": 10
    }
    """
    When method post
    Then status 400
    And match response.message == 'Price must be a positive value.'


  Scenario: Add product with negative quantity should return 400

    Given path '/api/products'
    And request
    """
    {
      "name": "Test Product",
      "brand": "Test Brand",
      "category": "Test Category",
      "price": 100,
      "quantity": -1
    }
    """
    When method post
    Then status 400
    And match response.message == 'Stock quantity must be a positive value.'


  Scenario: Update product with missing name should return 400

    * def productId = 1

    Given path '/api/products/', productId
    And request
    """
    {
      "brand": "Updated Brand",
      "category": "Updated Category",
      "price": 150,
      "quantity": 20
    }
    """
    When method put
    Then status 400
    And match response.message == 'Product name is required.'
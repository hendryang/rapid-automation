Feature: Test Common API GET, POST, PUT, DELETE - 2

  Scenario: GET
    When I GET "todos/{id}" with
      | id | 1 |
    Then I should get status "200"
    And response should be
    """
    {
      "title": "Test1",
      "completed": false,
      "id": 1
    }
    """

  Scenario: POST
    When I POST "todos" with unique title "Todo_<autogendatetime>"
    """
    {
      "title": "Todo_<autogendatetime>",
      "completed": false
    }
    """
    Then I should get status "201"
    And response should be
    """
    {
      "title": "Todo_<autogendatetime>",
      "completed": false,
      "id": "SKIPCHECK"
    }
    """

  Scenario: PUT
    When I PUT existing "todos/{id}" with new title "NewTodo_<autogendatetime>"
    """
    {
      "title": "NewTodo_<autogendatetime>",
      "completed": true
    }
    """
    Then I should get status "200"
    And response should be
    """
    {
      "title": "NewTodo_<autogendatetime>",
      "completed": true,
      "id": "SKIPCHECK"
    }
    """

  Scenario: DELETE
    When I DELETE existing "todos/{id}"
    Then I should get status "200"
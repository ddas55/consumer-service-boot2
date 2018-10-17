Feature: the health can be retrieved
  Scenario: client makes call to GET /healthz
    When the client calls /healthz
    Then the client receives response status code of 200
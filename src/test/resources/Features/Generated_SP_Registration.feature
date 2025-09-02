Feature: Service Provider Registration

  Allows an operations user to register a new Service Provider in the system.

  Scenario: Fill and submit the service provider registration form
    When I fill the 'Service Provider ID' with a valid value
    When I fill the 'Company name' with a valid value
    When I fill the 'Address' with a valid value
    When I fill the 'SP users' with a valid value
    When I fill the 'Resources' with a valid value
    And I click the 'Submit' button
    Then I should see a success message

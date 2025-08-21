Feature: Service Provider Registration

  Background:
    Given I navigate to the login page
    Then the login form should be visible
    When I enter valid <username> and <password>
    And I click the login button
    Then I should be redirected to the dashboard
    Then I navigate to the service provider registration page
    Then the registration form should be visible
  # Service Provider ID

  @positive
  Scenario: Register with valid Service Provider ID
    When I enter "12345678" in the Service Provider ID field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see a success message

  @negative @validation
  Scenario: Register with missing Service Provider ID
    When I leave the Service Provider ID field empty
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Service provider Id required"

  @negative @validation
  Scenario: Register with duplicate Service Provider ID
    Given a service provider with ID "12345678" already exists
    When I enter "12345678" in the Service Provider ID field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Service Provider Id already exists"

  @negative @validation
  Scenario: Register with invalid Service Provider ID format
    When I enter "1234" in the Service Provider ID field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Please enter 8 digit number for Service Provider Id."
  # Company Name

  @positive
  Scenario: Register with valid Company Name
    When I enter "Valid Company" in the Company Name field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see a success message

  @negative @validation
  Scenario: Register with missing Company Name
    When I leave the Company Name field empty
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Company Name required"

  @negative @validation
  Scenario: Register with Company Name exceeding max length
    When I enter "A very long company name that exceeds fifty characters in length" in the Company Name field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Company Name should be less than 50 characters"

  @negative @validation
  Scenario: Register with duplicate Company Name
    Given a company named "Valid Company" already exists
    When I enter "Valid Company" in the Company Name field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Service Provider already exists"
  # Address

  @positive
  Scenario: Register with valid Address
    When I enter "123 Main Street" in the Address field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see a success message

  @negative @validation
  Scenario: Register with Address exceeding max length
    When I enter a string longer than 255 characters in the Address field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Address should be less than 255 characters"
  # Description

  @positive
  Scenario: Register with valid Description
    When I enter "A valid description" in the Description field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see a success message

  @negative @validation
  Scenario: Register with Description exceeding max length
    When I enter a string longer than 255 characters in the Description field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Description should be less than 255 characters"
  # White Listed Users

  @positive
  Scenario: Register with valid White Listed Users
    When I enter "12345678,123456789012345" in the White Listed Users field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see a success message

  @negative @validation
  Scenario: Register with invalid White Listed Users format
    When I enter "1234,12345678901234567890" in the White Listed Users field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Should be comma-separated phone numbers having 15 digits as maximum and 8 digits as minimum"

  @negative @validation
  Scenario: Register with White Listed Users overlapping Black Listed Users
    Given "12345678" is in both White Listed and Black Listed Users
    When I enter "12345678" in both fields
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "You can not have a White-listed user in the Blacklisted user list."

  @negative @validation
  Scenario: Register with duplicate White Listed Users
    When I enter "12345678,12345678" in the White Listed Users field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Msisdn are Duplicate in White listed Msisdn."
  # Black Listed Users

  @positive
  Scenario: Register with valid Black Listed Users
    When I enter "87654321,123456789012345" in the Black Listed Users field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see a success message
  # Dedicated Alias

  @positive
  Scenario: Register with valid Dedicated Alias
    When I enter "alias1,alias2" in the Dedicated Alias field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see a success message

  @negative @validation
  Scenario: Register with duplicate Dedicated Alias
    Given "alias1" is already in use
    When I enter "alias1" in the Dedicated Alias field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Dedicated alias/es - [Alias Name] or a wildcard format is already in use"

  @negative @validation
  Scenario: Register with invalid Dedicated Alias format
    When I enter "invalid@alias" in the Dedicated Alias field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Invalid regular expression/s {Dedicated Alias} please enter valid regular expression."
  # SP Users

  @positive
  Scenario: Register with valid SP Users
    When I select "acrsp1" in the SP users field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see a success message

  @negative @validation
  Scenario: Register with no SP Users selected
    When I leave the SP users field empty
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Select atleast one User Name."
  # Marketing Users

  @positive
  Scenario: Register with valid Marketing Users
    When I select "ThiostMktg" in the Marketing Users field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see a success message

  @negative @validation
  Scenario: Register with no Marketing Users selected
    When I leave the Marketing Users field empty
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Allowed Marketing Users required."
  # Resources

  @positive
  Scenario: Register with valid Resources
    When I select "SMS" in the Resources field
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see a success message

  @negative @validation
  Scenario: Register with no Resources selected
    When I leave the Resources field empty
    And I fill other required fields with valid data
    And I submit the registration form
    Then I should see error "Select atleast one resource."

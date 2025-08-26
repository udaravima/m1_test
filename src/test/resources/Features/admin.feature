Feature: Admin Module Functionality

  Background:
    Given I am logged in as an "Admin"
    And I navigate to the admin section

  # User Management
  @smoke @positive @Admin @UserManagement
  Scenario: Create a new user with valid data
    When I navigate to the "Users" management page
    And I click the "Add User" button
    And I fill the user creation form with valid data
    And I click the "Save" button
    Then a new user should be created successfully

  @negative @validation @Admin @UserManagement
  Scenario: Create a new user with missing mandatory fields
    When I navigate to the "Users" management page
    And I click the "Add User" button
    And I leave the "User Name" field empty
    And I click the "Save" button
    Then I should see an error message "User Name is required"

  @positive @Admin @UserManagement
  Scenario: Update an existing user
    When I navigate to the "Users" management page
    And I select a user to update
    And I update the user's "Email Address"
    And I click the "Save" button
    Then the user's information should be updated

  @positive @Admin @UserManagement
  Scenario: Delete an existing user
    When I navigate to the "Users" management page
    And I select a user to delete
    And I confirm the deletion
    Then the user should be deleted successfully

  # Group Management
  @smoke @positive @Admin @GroupManagement
  Scenario: Create a new group with valid data
    When I navigate to the "Groups" management page
    And I click the "Add Group" button
    And I fill the group creation form with valid data
    And I click the "Save" button
    Then a new group should be created successfully

  @negative @validation @Admin @GroupManagement
  Scenario: Create a new group with a missing group name
    When I navigate to the "Groups" management page
    And I click the "Add Group" button
    And I leave the "Group Name" field empty
    And I click the "Save" button
    Then I should see an error message "Group Name is required"

  # Role and Permission Management
  @smoke @positive @Admin @RoleManagement
  Scenario: Create a new role
    When I navigate to the "Roles" management page
    And I click the "Add Role" button
    And I enter a unique role name
    And I click the "Save" button
    Then a new role should be created successfully

  @positive @Admin @PermissionManagement
  Scenario: Assign permissions to a role
    When I navigate to the "Roles" management page
    And I select a role
    And I assign the "MANAGE_USERS" permission to the role
    And I click the "Save" button
    Then the role should have the "MANAGE_USERS" permission

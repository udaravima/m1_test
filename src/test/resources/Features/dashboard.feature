Feature: Dashboard Functionality

  Background:
    Given I am logged in successfully
    And I am on the dashboard page

  @smoke
  Scenario: Dashboard loads successfully
    Then the dashboard should be visible
    And the page title should be "Dashboard"

  @navigation
  Scenario: Menu navigation works
    When I click the "Profile" menu
    Then the profile page should be displayed

    When I click the "Settings" menu
    Then the settings page should be displayed

  @widgets
  Scenario: Widgets are displayed and populated
    Then all dashboard widgets should be visible
    And each widget should have data

  @logout
  Scenario: Logout redirects to login page
    When I click the logout button
    Then I should be redirected to the login page

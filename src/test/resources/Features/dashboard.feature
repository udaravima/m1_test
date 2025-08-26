Feature: Dashboard Functionality

  Background:
    Given I am logged in  as admin successfully
    And I am on the dashboard page

  @smoke @positive @Dashboard
  Scenario: Dashboard displays key performance indicators
    Then I should see the "TPS" KPI
    And I should see the "TPD" KPI
    And I should see the "SLA Violations" KPI
    And I should see the "System Alerts" KPI

  @positive @Dashboard
  Scenario: Dashboard widgets are customizable
    When I drag the "TPS" widget to a new position
    Then the "TPS" widget should be at the new position
    When I resize the "TPD" widget
    Then the "TPD" widget should be resized

  @positive @Dashboard
  Scenario: Dashboard updates in near real-time
    Given the system generates new data
    When I wait for the dashboard to refresh
    Then the dashboard data should be updated

  @positive @Dashboard
  Scenario: Export graphs and charts as images
    When I click the "Export" button on the "TPS" graph
    Then an image of the "TPS" graph should be downloaded

  @positive @security @Dashboard
  Scenario: Operations user has full dashboard access
    Given I am logged in as an "Operations-User"
    Then I should see all service provider applications on the dashboard

  @positive @security @Dashboard
  Scenario: Service provider user has restricted dashboard access
    Given I am logged in as an "SP-User"
    Then I should only see my applications on the dashboard

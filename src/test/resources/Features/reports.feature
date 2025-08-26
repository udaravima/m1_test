Feature: Reports Functionality

  Background:
    Given I am logged in successfully
    And I navigate to the reports section

  @smoke @positive @Reports
  Scenario: Generate an ad-hoc report
    When I select the "System Utilization Report"
    And I specify the report parameters
    And I click the "Generate" button
    Then the report should be generated successfully

  @positive @Reports
  Scenario Outline: Download a report in different formats
    When I generate a "Traffic Summary Report"
    And I select the "<format>" format
    And I click the "Download" button
    Then the report should be downloaded in "<format>" format

    Examples:
      | format |
      | PDF    |
      | CSV    |
      | XLS    |

  @positive @Reports
  Scenario: Drill-down functionality in reports
    When I am viewing a "Service Provider Report"
    And I click on a data point to drill down
    Then I should see a more detailed view of the data

  @positive @Reports
  Scenario: CDR Query integration
    When I am on the "CDR Query" page
    And I search for specific CDRs
    And I select the option to generate a report from the results
    Then a "CDR Usage Report" should be generated with the queried CDRs

  @positive @security @Reports
  Scenario: Unauthorized user cannot access reports
    Given I am logged in as a user without report permissions
    When I try to navigate to the reports section
    Then I should be denied access

Feature: SLA Configuration

  Background:
    Given I am logged in successfully
    And I navigate to the SLA configuration page for a service provider

  @smoke @positive @SLA @SMS
  Scenario: Configure SMS SLA with valid data
    When I select "SMS" as the NCS
    And I enter "500" in the "SMS_MO_MaximumMessagesPerSecond" field
    And I enter "10000" in the "SMS_MO_MaximumMessagesPerDay" field
    And I enter "500" in the "SMS_MT_MaximumRecipientsPerSecond" field
    And I enter "10000" in the "SMS_MT_MaximumRecipientsPerDay" field
    And I click the "Save" button
    Then I should see a success message "SMS SLA for Service Provider configured successfully"

  @negative @validation @SLA @SMS
  Scenario Outline: Configure SMS SLA with invalid data
    When I select "SMS" as the NCS
    And I enter "<mo_mps>" in the "SMS_MO_MaximumMessagesPerSecond" field
    And I enter "<mo_mpd>" in the "SMS_MO_MaximumMessagesPerDay" field
    And I enter "<mt_rps>" in the "SMS_MT_MaximumRecipientsPerSecond" field
    And I enter "<mt_rpd>" in the "SMS_MT_MaximumRecipientsPerDay" field
    And I click the "Save" button
    Then I should see an error message "<error_message>"

    Examples:
      | mo_mps | mo_mpd  | mt_rps | mt_rpd  | error_message                                                     |
      |        | 10000   | 500    | 10000   | "Maximum messages per second required."                             |
      | 500    |         | 500    | 10000   | "Maximum messages per day required."                                |
      | 1500   | 10000   | 500    | 10000   | "Max message per Second should not be greater than 1000."           |
      | 500    | 60000000| 500    | 10000   | "Max message per day should not be greater than 50000000."          |
      | 500    | 400     | 500    | 10000   | "Max message per day should be greater than message per Second."    |

  @smoke @positive @SLA @MMS
  Scenario: Configure MMS SLA with valid data
    When I select "MMS" as the NCS
    And I enter "50" in the "MMS_MO_MaximumMessagesPerSecond" field
    And I enter "5000" in the "MMS_MO_MaximumMessagesPerDay" field
    And I enter "50" in the "MMS_MT_MaximumRecipientsPerSecond" field
    And I enter "5000" in the "MMS_MT_MaximumRecipientsPerDay" field
    And I click the "Save" button
    Then I should see a success message "MMS SLA for Service Provider configured successfully"

  @negative @validation @SLA @MMS
  Scenario Outline: Configure MMS SLA with invalid data
    When I select "MMS" as the NCS
    And I enter "<mo_mps>" in the "Maximum Messages Per Second" field for MMS MO
    And I enter "<mo_mpd>" in the "Maximum Messages Per Day" field for MMS MO
    And I enter "<mt_rps>" in the "Maximum recipients Per Second" field for MMS MT
    And I enter "<mt_rpd>" in the "Maximum recipients Per Day" field for MMS MT
    And I click the "Save" button
    Then I should see an error message "<error_message>"

    Examples:
      | mo_mps | mo_mpd | mt_rps | mt_rpd | error_message                                                          |
      |        | 5000   | 50     | 5000   | "Maximum messages per second required."                                  |
      | 50     |        | 50     | 5000   | "Maximum messages per day required."                                     |
      | 150    | 5000   | 50     | 5000   | "Max message per Second should not be greater than 100 message per second." |
      | 50     | 60000000| 50     | 5000   | "Max message per day should not be greater than 50000000 message per day."|
      | 50     | 40     | 50     | 5000   | "Maximum messages per day should be greater than maximum messages per second." |

  @smoke @positive @SLA @USSD
  Scenario: Configure USSD SLA with valid data
    When I select "USSD" as the NCS
    And I enter "500" in the "USSD_MO_MaximumMessagesPerSecond" field
    And I enter "10000" in the "USSD_MO_MaximumMessagesPerDay" field
    And I enter "500" in the "USSD_MT_MaximumRecipientsPerSecond" field
    And I enter "10000" in the "USSD_MT_MaximumRecipientsPerDay" field
    And I click the "Save" button
    Then I should see a success message "USSD SLA for Service Provider configured successfully"

  @negative @validation @SLA @USSD
  Scenario Outline: Configure USSD SLA with invalid data
    When I select "USSD" as the NCS
    And I enter "<mo_mps>" in the "Maximum Messages Per Second" field for USSD MO
    And I enter "<mo_mpd>" in the "Maximum Messages Per Day" field for USSD MO
    And I enter "<mt_rps>" in the "Maximum recipients Per Second" field for USSD MT
    And I enter "<mt_rpd>" in the "Maximum recipients Per Day" field for USSD MT
    And I click the "Save" button
    Then I should see an error message "<error_message>"

    Examples:
      | mo_mps | mo_mpd  | mt_rps | mt_rpd  | error_message                                                     |
      |        | 10000   | 500    | 10000   | "Maximum messages per second required."                             |
      | 500    |         | 500    | 10000   | "Maximum messages per day required."                                |
      | 1500   | 10000   | 500    | 10000   | "Max message per Second should not be greater than 1000 message per second."|
      | 500    | 60000000| 500    | 10000   | "Max message per day should not be greater than 50000000 message per day."|
      | 500    | 400     | 500    | 10000   | "Max message per day should be greater than message per Second."    |

  @smoke @positive @SLA @WAPPUSH
  Scenario: Configure WAP PUSH SLA with valid data
    When I select "WAPPUSH" as the NCS
    And I enter "50" in the "WAPPUSH_MaximumRecipientsPerSecond" field
    And I enter "5000" in the "WAPPUSH_MaximumRecipientsPerDay" field
    And I click the "Save" button
    Then I should see a success message "WAP-PUSH SLA for Service Provider configured successfully"

  @negative @validation @SLA @WAPPUSH
  Scenario Outline: Configure WAP PUSH SLA with invalid data
    When I select "WAPPUSH" as the NCS
    And I enter "<rps>" in the "Maximum Recipients Per Second" field for WAP PUSH
    And I enter "<rpd>" in the "Maximum Recipients Per Day" field for WAP PUSH
    And I click the "Save" button
    Then I should see an error message "<error_message>"

    Examples:
      | rps  | rpd    | error_message                                                                  |
      |      | 5000   | "Max Recipients per Second required."                                          |
      | 50   |        | "Max Recipients per Day required."                                             |
      | 150  | 5000   | "Max recipients per second should not be greater than 100 recipients per second." |
      | 50   | 600000000| "Max recipients per day should not be greater than 500000000 recipients per day."|
      | 50   | 40     | "Max recipients per day should be greater than Max recipients per second."     |

  @smoke @positive @SLA @BAS
  Scenario: Configure BAS SLA with valid data
    When I select "BAS" as the NCS
    And I enter "50" in the "BAS_MaximumIncomingRequestsPerSecond" field
    And I enter "5000" in the "BAS_MaximumIncomingRequestsPerDay" field
    And I click the "Save" button
    Then I should see a success message "BAS SLA for Service Provider configured successfully"

  @negative @validation @SLA @BAS
  Scenario Outline: Configure BAS SLA with invalid data
    When I select "BAS" as the NCS
    And I enter "<rps>" in the "Maximum BAS Incoming Requests Per Second" field for BAS
    And I enter "<rpd>" in the "Maximum BAS Incoming Requests Per Day" field for BAS
    And I click the "Save" button
    Then I should see an error message "<error_message>"

    Examples:
      | rps  | rpd    | error_message                                                                  |
      |      | 5000   | "Maximum BAS Incoming Requests per second required."                           |
      | 50   |        | "Maximum BAS Incoming Requests per day required."                              |
      | 150  | 5000   | "Max billing per second should not be greater than 100 billing per second."    |
      | 50   | 60000000| "Max billing per day should not be greater than 50000000 billing per day."     |
      | 50   | 40     | "Maximum BAS Incoming Requests per day should be greater than maximum requests per second."|

  @smoke @positive @SLA @SWS
  Scenario: Configure SWS SLA with valid data
    When I select "SWS" as the NCS
    And I enter "500" in the "SWS_MaximumIncomingRequestsPerSecond" field
    And I enter "10000" in the "SWS_MaximumIncomingRequestsPerDay" field
    And I click the "Save" button
    Then I should see a success message "SWS for Service Provider configured successfully"

  @negative @validation @SLA @SWS
  Scenario Outline: Configure SWS SLA with invalid data
    When I select "SWS" as the NCS
    And I enter "<rps>" in the "Maximum SWS Incoming Requests Per Second" field for SWS
    And I enter "<rpd>" in the "Maximum SWS Incoming Requests Per Day" field for SWS
    And I click the "Save" button
    Then I should see an error message "<error_message>"

    Examples:
      | rps  | rpd     | error_message                                                                  |
      |      | 10000   | "Maximum SWS Incoming Requests per second required."                           |
      | 500  |         | "Maximum SWS Incoming Requests per day required."                              |
      | 1500 | 10000   | "Maximum SWS per second should be less than 1,000."                            |
      | 500  | 60000000| "Maximum SWS per second should be less than 50,000,000."                       |
      | 500  | 400     | "Maximum SWS incoming requests per day should be greater than maximum requests per second."|


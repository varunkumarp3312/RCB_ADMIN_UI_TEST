@ui @regression
Feature: Regression coverage for the reported UI defects
  These scenarios express expected behavior from ui-bug-report.csv.
  Selectors and test credentials must be configured before execution.

  @BUG-001 @P1
  Scenario: Protected API session failures are not shown as valid page data
    Given I open the configured login page
    When I sign in with valid test credentials
    And I open the configured "chat-monitor" route
    Then protected content is visible or the user is clearly returned to login

  @BUG-002 @P2
  Scenario: Empty player search does not expose active pagination
    Given I open the configured login page
    When I sign in with valid test credentials
    And I open the configured "all-users" route
    And I filter players with a unique nonexistent username
    Then the empty player result does not have an enabled next page

  @BUG-003 @P2
  Scenario: Blank market submission does not change the board count
    Given I open the configured login page
    When I sign in with valid test credentials
    And I open the configured "main-market-list" route
    And I record the current market board count
    And I submit the market form with all fields blank
    Then the market form shows validation and the board count is unchanged

# RCB UI Cucumber + Selenium Framework

An easy-to-extend Java BDD test framework created from the UI defects and test inventory in this workspace. It uses Maven, Cucumber/Gherkin, Selenium WebDriver, JUnit Platform, AssertJ, and WebDriverManager.

## Requirements

- JDK 17 or newer
- Maven 3.8 or newer
- Chrome, Firefox, or Edge installed for the selected browser
- Authorized staging access and test credentials for the scenarios that require login

## Configure

1. Inspect the staging UI and fill the blank CSS selectors in `src/test/resources/application.properties`. Keep selectors centralized there rather than embedding them in step definitions.
2. Set `TEST_USERNAME` and `TEST_PASSWORD` in the shell or IDE run configuration. Do not commit credentials.
3. Set `test.no-match-username` to a value guaranteed not to match a player in the test environment.
4. Adjust the base URL, routes, browser, or timeout values in `application.properties` (or override any setting with a Maven `-Dkey=value` property).

Example PowerShell run configuration:

```powershell
$env:TEST_USERNAME = "staging-test-user"
$env:TEST_PASSWORD = "your-secret"
mvn test
```

The framework supports `chrome`, `firefox`, and `edge`; use `headless=true` for headless browser execution. WebDriverManager provisions the matching driver when needed.

## Run tests

```powershell
mvn test
```

Run only one defect scenario:

```powershell
mvn test "-Dcucumber.filter.tags=@BUG-002"
```

The Cucumber HTML and JSON reports are written to `target/cucumber-reports/`. Failed scenarios attach a screenshot to the Cucumber report.

The included scenarios are acceptance/regression checks for BUG-001 through BUG-003, not claims that those defects have been fixed. They require real, authorized staging credentials and selectors; no browser test is run successfully until these are configured. Never use production credentials or production data.

## Add a scenario

1. Write behavior in `src/test/resources/features/*.feature`.
2. Add or reuse a step in `src/test/java/com/rcb/uitest/steps/UiSteps.java`.
3. Keep element interaction in the page layer and selectors in `application.properties`.
4. Tag tests with a defect ID, priority, or suite tag to filter execution.

## Project layout

```text
src/test/java/com/rcb/uitest/
  config/       Configuration loading and validation
  driver/       Browser creation and lifecycle
  hooks/        Per-scenario setup, failure screenshots, teardown
  pages/        Reusable Selenium page actions
  steps/        Cucumber step definitions
  RunCucumberTest.java
src/test/resources/
  application.properties
  features/
```

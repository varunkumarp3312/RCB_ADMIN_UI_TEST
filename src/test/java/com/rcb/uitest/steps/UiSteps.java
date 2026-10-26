package com.rcb.uitest.steps;

import com.rcb.uitest.config.FrameworkConfig;
import com.rcb.uitest.pages.UiPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;

import java.util.HashMap;
import java.util.Map;

public class UiSteps {
    private final UiPage page = new UiPage();
    private final FrameworkConfig config = FrameworkConfig.getInstance();
    private final Map<String, String> scenarioValues = new HashMap<>();

    @Given("I open the configured login page")
    public void openLoginPage() {
        page.openConfiguredRoute("login");
    }

    @When("I sign in with valid test credentials")
    public void signIn() {
        String username = System.getenv("TEST_USERNAME");
        String password = System.getenv("TEST_PASSWORD");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Set TEST_USERNAME and TEST_PASSWORD environment variables; credentials are never stored in the project.");
        }

        page.enter("selector.login.username", username);
        page.enter("selector.login.password", password);
        page.click("selector.login.submit");
    }

    @When("I open the configured {string} route")
    public void openRoute(String routeName) {
        page.openConfiguredRoute(routeName);
    }

    @Then("protected content is visible or the user is clearly returned to login")
    public void protectedContentOrLoginRedirect() {
        boolean authErrorVisible = page.isVisible("selector.auth.error");
        String loginRoute = config.require("route.login");
        String currentUrl = com.rcb.uitest.driver.DriverFactory.getDriver().getCurrentUrl();
        boolean redirectedToLogin = currentUrl.contains(loginRoute);
        boolean protectedContentVisible = page.isVisible("selector.protected.content");

        Assertions.assertThat(protectedContentVisible || (authErrorVisible && redirectedToLogin))
                .as("Protected pages must show loaded content or a clear authentication error and login redirect")
                .isTrue();
        Assertions.assertThat(authErrorVisible && !redirectedToLogin)
                .as("An authentication error must not leave the user on a stale protected page")
                .isFalse();
    }

    @When("I filter players with a unique nonexistent username")
    public void filterPlayersWithNoMatch() {
        page.enter("selector.players.username-filter", config.require("test.no-match-username"));
        page.click("selector.players.filter");
    }

    @Then("the empty player result does not have an enabled next page")
    public void emptyPlayersDoNotHaveNextPage() {
        boolean hasResults = page.isVisible("selector.players.results");
        Assertions.assertThat(hasResults)
                .as("The configured unique search value should return no players")
                .isFalse();
        Assertions.assertThat(page.isEnabled("selector.players.next-page"))
                .as("Next must be disabled or absent for an empty result set")
                .isFalse();
    }

    @When("I record the current market board count")
    public void recordBoardCount() {
        scenarioValues.put("board-count", page.visibleText("selector.market.board-count"));
    }

    @When("I submit the market form with all fields blank")
    public void submitBlankMarketForm() {
        page.click("selector.market.submit");
    }

    @Then("the market form shows validation and the board count is unchanged")
    public void validateBlankMarketSubmission() {
        Assertions.assertThat(page.isVisible("selector.market.validation"))
                .as("A blank market form must show validation")
                .isTrue();
        Assertions.assertThat(page.visibleText("selector.market.board-count"))
                .as("A rejected blank form must not change the displayed board count")
                .isEqualTo(scenarioValues.get("board-count"));
    }
}

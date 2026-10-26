package com.rcb.uitest.pages;

import com.rcb.uitest.config.FrameworkConfig;
import com.rcb.uitest.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class UiPage {
    private final FrameworkConfig config = FrameworkConfig.getInstance();

    protected WebDriverWait waitFor() {
        return new WebDriverWait(
                DriverFactory.getDriver(),
                Duration.ofSeconds(config.getInt("timeout.element-seconds", 10)));
    }

    protected By locator(String configKey) {
        String selector = config.require(configKey);
        return By.cssSelector(selector);
    }

    public void openConfiguredRoute(String routeKey) {
        String baseUrl = config.require("base.url");
        String route = config.require("route." + routeKey);
        String normalizedBaseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
        String normalizedRoute = route.startsWith("/") ? route : "/" + route;
        DriverFactory.getDriver().get(normalizedBaseUrl + normalizedRoute);
    }

    public void enter(String selectorKey, String value) {
        WebElement element = waitFor().until(
                ExpectedConditions.visibilityOfElementLocated(locator(selectorKey)));
        element.clear();
        element.sendKeys(value);
    }

    public void click(String selectorKey) {
        waitFor().until(ExpectedConditions.elementToBeClickable(locator(selectorKey))).click();
    }

    public boolean isVisible(String selectorKey) {
        List<WebElement> elements = DriverFactory.getDriver().findElements(locator(selectorKey));
        return elements.stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isEnabled(String selectorKey) {
        List<WebElement> elements = DriverFactory.getDriver().findElements(locator(selectorKey));
        return elements.stream().anyMatch(element -> element.isDisplayed() && element.isEnabled());
    }

    public String visibleText(String selectorKey) {
        return waitFor().until(
                ExpectedConditions.visibilityOfElementLocated(locator(selectorKey))).getText().trim();
    }
}

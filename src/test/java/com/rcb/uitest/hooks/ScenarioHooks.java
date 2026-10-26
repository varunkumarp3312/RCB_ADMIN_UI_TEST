package com.rcb.uitest.hooks;

import com.rcb.uitest.driver.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class ScenarioHooks {
    @Before
    public void startBrowser() {
        DriverFactory.startDriver();
    }

    @After
    public void captureFailureAndClose(Scenario scenario) {
        try {
            if (scenario.isFailed() && DriverFactory.isDriverRunning()) {
                byte[] screenshot = ((org.openqa.selenium.TakesScreenshot) DriverFactory.getDriver())
                        .getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "failure-screenshot");
            }
        } finally {
            DriverFactory.stopDriver();
        }
    }
}

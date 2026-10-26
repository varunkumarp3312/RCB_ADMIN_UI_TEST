package com.rcb.uitest.driver;

import com.rcb.uitest.config.FrameworkConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.Locale;

public final class DriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver startDriver() {
        FrameworkConfig config = FrameworkConfig.getInstance();
        String browser = config.get("browser").toLowerCase(Locale.ROOT);
        boolean headless = config.getBoolean("headless", false);
        WebDriver driver = switch (browser) {
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                yield new ChromeDriver(options);
            }
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                yield new FirefoxDriver(options);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                yield new EdgeDriver(options);
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + browser + "'. Choose chrome, firefox, or edge.");
        };

        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(config.getInt("timeout.page-load-seconds", 30)));
        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not running for this scenario");
        }
        return driver;
    }

    public static boolean isDriverRunning() {
        return DRIVER.get() != null;
    }

    public static void stopDriver() {
        WebDriver driver = DRIVER.get();
        DRIVER.remove();
        if (driver != null) {
            driver.quit();
        }
    }
}

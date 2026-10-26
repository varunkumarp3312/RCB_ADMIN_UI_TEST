package com.rcb.uitest.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class FrameworkConfig {
    private static final String CONFIG_RESOURCE = "application.properties";
    private static final FrameworkConfig INSTANCE = new FrameworkConfig();

    private final Properties properties = new Properties();

    private FrameworkConfig() {
        try (InputStream input = FrameworkConfig.class.getClassLoader()
                .getResourceAsStream(CONFIG_RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Missing test configuration: " + CONFIG_RESOURCE);
            }
            properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load " + CONFIG_RESOURCE, exception);
        }
    }

    public static FrameworkConfig getInstance() {
        return INSTANCE;
    }

    public String get(String key) {
        String override = System.getProperty(key);
        if (override != null) {
            return override.trim();
        }
        return properties.getProperty(key, "").trim();
    }

    public String require(String key) {
        String value = get(key);
        if (value.isBlank() || value.startsWith("REPLACE_WITH_")) {
            throw new IllegalStateException(
                    "Required test setting '" + key + "' is not configured in "
                            + CONFIG_RESOURCE + " or with -D" + key + "=...");
        }
        return value;
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Test setting '" + key + "' must be an integer", exception);
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value.isBlank()) {
            return defaultValue;
        }
        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
            throw new IllegalStateException("Test setting '" + key + "' must be true or false");
        }
        return Boolean.parseBoolean(value);
    }
}

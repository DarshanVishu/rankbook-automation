package com.rankbook.framework.config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Could not locate config.properties in classpath");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading config.properties", e);
        }
    }

    public static String get(String key) {
        String sysProp = System.getProperty(key);
        return (sysProp != null && !sysProp.isBlank()) ? sysProp : properties.getProperty(key);
    }
}
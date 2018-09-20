package utils.config;

import utils.logger.ApplicationLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

public class ApplicationConfig {

    private static final String FILE_NAME = "config.properties";
    private static Properties properties;

    private ApplicationConfig() {}

    static {
        properties = new Properties();
        load();
    }

    public static String getStringProperty(String key, String defaultVal) {
        Object value = getProperty(key, defaultVal);
        if (value != null) {
            return String.valueOf(value);
        } else {
            return defaultVal;
        }
    }

    public static int getIntegerProperty(String key, int defaultVal) {
        Object value = getProperty(key, defaultVal);
        if (value != null) {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException nfe) {
                ApplicationLogger.log(Level.WARNING, "Failed to parse value for key {0}", key);
            }

        }
        return defaultVal;
    }

    public static long getLongProperty(String key, long defaultVal) {
        Object value = getProperty(key, defaultVal);
        if (value != null) {
            try {
                return Long.parseLong(String.valueOf(value));
            } catch (NumberFormatException nfe) {
                ApplicationLogger.log(Level.WARNING, "Failed to parse value for key {0}", key);
            }
        }
        return defaultVal;
    }

    private static void load() {
        //load a properties file from class path
        try (InputStream input = ApplicationConfig.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (input == null) {
                ApplicationLogger.log(Level.WARNING, "Warning, unable to find property file {0}", FILE_NAME);
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            ApplicationLogger.log(Level.SEVERE, "Error loading a property file {0}. Exception -> {1}", FILE_NAME, e);
        }
    }

    private static Object getProperty(String key, Object defaultVal) {
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        } else {
            return defaultVal;
        }
    }
}

package dev.jiricekm.petstore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        String environment = System.getProperty("env", "dev"); // Default to 'dev' profile
        String configFile = "config-" + environment + ".properties";

        try (InputStream input = Config.class.getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + configFile);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static String getBaseUri() {
        return System.getenv("BASE_URI") != null ? System.getenv("BASE_URI") :
                properties.getProperty("base.uri", "https://petstore.swagger.io/v2");
    }

    public static boolean isPrettyPrintEnabled() {
        return Boolean.parseBoolean(properties.getProperty("json.pretty.print", "false"));
    }

    public static long getLongProperty(String key, long defaultValue) {
        try {
            return Long.parseLong(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

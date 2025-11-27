package com.blueseals.qaforum.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfigLoader {

    private static final Properties appProps = new Properties();

    public static void loadConfig(String fileName) {

        try (InputStream input = AppConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {

            if (input == null) {
                System.out.println("Sorry, unable to find " + fileName);
                return;
            }

            appProps.load(input);

            System.out.println("=== CONFIG LOADED ===");
            System.out.println("Mode: " + appProps.getProperty("app.mode"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return appProps.getProperty(key);
    }
}
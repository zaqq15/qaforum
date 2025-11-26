package com.blueseals.qaforum.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfigLoader {

    public static void loadConfig(String configFilePath) {
        Properties appProps = new Properties();

        try (FileInputStream rootPath = new FileInputStream(configFilePath)) {
            appProps.load(rootPath);

            String appName = appProps.getProperty("app.name");
            String appVersion = appProps.getProperty("app.version");

            System.out.println("Starting" + appName + " version " + appVersion);
        } catch (IOException e) {
            System.err.println("Warning: Could not load config file. Using defaults.");
        }
    }
}

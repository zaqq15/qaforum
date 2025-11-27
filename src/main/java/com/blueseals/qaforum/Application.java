package com.blueseals.qaforum;

import com.blueseals.qaforum.config.AppConfigLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        AppConfigLoader.loadConfig("config.properties");

        SpringApplication.run(Application.class, args);
    }

}
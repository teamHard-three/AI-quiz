package com.aiquiz.aiquizs.config;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.*;

public class AIConfigLoader {
    public static String getApiKey() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("./src/main/resources/config.properties"));
            return prop.getProperty("api.key");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

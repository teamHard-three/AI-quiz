package com.aiquiz.aiquizs.config;


import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AIConfigLoader {
    public static String getApiKey() {
        Properties prop = new Properties();
        try {

            // 推荐方式：Spring方式加载
            Resource resource = new ClassPathResource("config.properties");

            prop.load(resource.getInputStream());

            return prop.getProperty("api.key");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

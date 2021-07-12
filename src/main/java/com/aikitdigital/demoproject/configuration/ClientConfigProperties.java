package com.aikitdigital.demoproject.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("com.aikitdigital")
public class ClientConfigProperties {
    private String baseUrl;

    public ClientConfigProperties() {
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}

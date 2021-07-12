package com.aikitdigital.demoproject.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("com.aikitdigital")
public class ClientConfigProperties {
    private String baseUrl;
}

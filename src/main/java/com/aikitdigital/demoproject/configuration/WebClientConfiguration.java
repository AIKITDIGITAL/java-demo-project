package com.aikitdigital.demoproject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

	@Bean
	public WebClient webClient(WebClient.Builder builder, ClientConfigProperties clientConfigProperties) {
		return builder.clone()
				.baseUrl(clientConfigProperties.getBaseUrl())
				.build();
	}
}

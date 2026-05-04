package com.jimsimrodev.orchestrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Value("${cors-settings.url-production}")
    private String urlProduction;

    @Value("${cors-settings.url-production1}")
    private String urlProduction1;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(urlProduction, urlProduction1)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "CONNECT");
    }

}

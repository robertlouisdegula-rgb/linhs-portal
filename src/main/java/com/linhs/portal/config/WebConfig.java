package com.linhs.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // The "file:uploads/" path works perfectly inside Docker environments
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
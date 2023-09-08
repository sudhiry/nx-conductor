package com.netflix.conductor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class}
)
public class ConductorServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConductorServerApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(name = "conductor.server.cors", havingValue = "unsecure")
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

}

package com.tekpyramid.kira_product_inventory.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Optional;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.tekpyramid.kira_product_inventory.repository")
public class MongoConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                // In real scenario, get from security context
                return Optional.of("system");
            }
        };
    }
}
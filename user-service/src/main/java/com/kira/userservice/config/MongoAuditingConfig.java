package com.kira.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorAware")
public class MongoAuditingConfig {
    // This enables auditing in Mongo (createdAt, updatedAt, createdBy, updatedBy)
}

package com.moonrider.identity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.moonrider.identity.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    
}
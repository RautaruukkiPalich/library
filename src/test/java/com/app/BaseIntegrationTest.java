package com.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Execution(ExecutionMode.SAME_THREAD)
@AutoConfigureMockMvc
@Testcontainers
public abstract class BaseIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    protected static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);

    static {
        POSTGRES_CONTAINER.start();
    }


    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);

        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");

        registry.add("spring.datasource.hikari.maximumPoolSize", () -> 5);
        registry.add("spring.datasource.hikari.minimumIdle", () -> 2);
        registry.add("spring.datasource.hikari.idleTimeout", () -> 2000);

        registry.add("spring.datasource.hikari.initializationFailTimeout", () -> "-1");
        registry.add("spring.datasource.hikari.connectionTestQuery", () -> "SELECT 1");
    }

    protected void truncate(JdbcTemplate jdbcTemplate, String... tableNames) {
        try {
            for (String table : tableNames) {
                try {
                    jdbcTemplate.execute("TRUNCATE TABLE " + table + " RESTART IDENTITY CASCADE;");
                } catch (Exception e) {
                    System.err.println("Failed to truncate " + table + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to truncate tables: " + e.getMessage());
        }
    }

    @AfterAll
    static void shutdown() {
        if (POSTGRES_CONTAINER != null && POSTGRES_CONTAINER.isRunning()) {
            POSTGRES_CONTAINER.stop();
        }
    }

    //fix connections pool problems
    @TestConfiguration
    static class TestDataSourceConfig {

        @Bean
        @Primary
        public DataSource dataSource() {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(POSTGRES_CONTAINER.getJdbcUrl());
            config.setUsername(POSTGRES_CONTAINER.getUsername());
            config.setPassword(POSTGRES_CONTAINER.getPassword());
            config.setDriverClassName("org.postgresql.Driver");
            config.setMaximumPoolSize(5);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(20000);
            config.setValidationTimeout(5000);
            config.setIdleTimeout(2000);
            config.setMaxLifetime(60000);
            config.setConnectionTestQuery("SELECT 1");
            config.setInitializationFailTimeout(-1);
            return new HikariDataSource(config);
        }
    }
}
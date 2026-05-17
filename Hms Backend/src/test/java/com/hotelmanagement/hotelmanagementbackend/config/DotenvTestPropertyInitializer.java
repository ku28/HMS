package com.hotelmanagement.hotelmanagementbackend.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DotenvTestPropertyInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Map<String, Object> properties = new HashMap<>();
        loadRootDotenv(properties);
        putDatasourceProperties(properties);
        putIfPresent(properties, "REDIS_HOST", "spring.data.redis.host");
        putIfPresent(properties, "REDIS_PORT", "spring.data.redis.port");
        putIfPresent(properties, "REDIS_PASSWORD", "spring.data.redis.password");
        applicationContext.getEnvironment().getPropertySources()
                .addFirst(new MapPropertySource("rootDotenv", properties));
    }

    private void loadRootDotenv(Map<String, Object> properties) {
        Path dotenv = Path.of("..", ".env").normalize();
        if (!Files.exists(dotenv)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(dotenv)) {
                String trimmed = line.trim();
                if (trimmed.isBlank() || trimmed.startsWith("#") || !trimmed.contains("=")) {
                    continue;
                }
                int separator = trimmed.indexOf('=');
                String key = trimmed.substring(0, separator).trim();
                String value = trimmed.substring(separator + 1).trim();
                properties.putIfAbsent(key, value);
            }
        } catch (IOException ignored) {
            // Tests can still use system environment variables or application-test.yml defaults.
        }
    }

    private void putIfPresent(Map<String, Object> properties, String sourceKey, String targetKey) {
        Object value = properties.getOrDefault(sourceKey, System.getenv(sourceKey));
        if (value != null) {
            properties.put(targetKey, value);
        }
    }

    private void putDatasourceProperties(Map<String, Object> properties) {
        String url = firstValue(properties, "TEST_DB_URL");
        if (url == null) {
            url = composeMysqlUrl(properties);
        }
        if (url == null) {
            url = firstValue(properties, "DB_URL");
        }
        if (url != null) {
            properties.put("spring.datasource.url", mysqlTestUrl(url));
        }

        String username = firstValue(properties, "TEST_DB_USERNAME");
        String password = firstValue(properties, "TEST_DB_PASSWORD");
        if (username == null && firstValue(properties, "MYSQL_ROOT_PASSWORD") != null) {
            username = "root";
            password = firstValue(properties, "MYSQL_ROOT_PASSWORD");
        }
        if (username == null) {
            username = firstValue(properties, "MYSQL_USER", "DB_USERNAME");
        }
        if (password == null) {
            password = firstValue(properties, "MYSQL_PASSWORD", "DB_PASSWORD");
        }
        if (username != null) {
            properties.put("spring.datasource.username", username);
        }
        if (password != null) {
            properties.put("spring.datasource.password", password);
        }
    }

    private String composeMysqlUrl(Map<String, Object> properties) {
        String database = firstValue(properties, "TEST_DB_NAME");
        if (database == null) {
            database = firstValue(properties, "MYSQL_DATABASE");
        }
        String port = firstValue(properties, "MYSQL_PORT");
        if (database == null || port == null) {
            return null;
        }
        return "jdbc:mysql://localhost:" + port + "/" + database;
    }

    private String firstValue(Map<String, Object> properties, String... keys) {
        for (String key : keys) {
            Object fromDotenv = properties.get(key);
            if (fromDotenv != null && !fromDotenv.toString().isBlank()) {
                return fromDotenv.toString();
            }
            String fromSystem = System.getenv(key);
            if (fromSystem != null && !fromSystem.isBlank()) {
                return fromSystem;
            }
        }
        return null;
    }

    private String mysqlTestUrl(String url) {
        if (!url.startsWith("jdbc:mysql:") || url.contains("createDatabaseIfNotExist")) {
            return url;
        }
        String separator = url.contains("?") ? "&" : "?";
        return url + separator + "createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false";
    }
}

package com.hotelmanagement.hotelmanagementbackend.repository;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.hotelmanagement.hotelmanagementbackend.config.DotenvTestPropertyInitializer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Target(TYPE)
@Retention(RUNTIME)
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
@ContextConfiguration(initializers = DotenvTestPropertyInitializer.class)
@TestPropertySource(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.jpa.show-sql=false"
})
public @interface RepositoryDataJpaTest {
}

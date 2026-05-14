package com.hotelmanagement.hotelmanagementbackend.auth.repository;

import com.hotelmanagement.hotelmanagementbackend.auth.entity.User;
import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryDataJpaTest
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByEmail should return the matching user")
    void findByEmailShouldReturnMatchingUser() {
        User user = persistUser("guest@example.com", "Guest User", "ROLE_USER");

        Optional<User> result = userRepository.findByEmail("guest@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    @DisplayName("existsByEmail should report whether email exists")
    void existsByEmailShouldReportWhetherEmailExists() {
        persistUser("admin@example.com", "Admin User", "ROLE_ADMIN");

        assertThat(userRepository.existsByEmail("admin@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("missing@example.com")).isFalse();
    }

    @Test
    @DisplayName("findByRole should return paginated users with role")
    void findByRoleShouldReturnPaginatedUsersWithRole() {
        persistUser("first@example.com", "First Admin", "ROLE_ADMIN");
        persistUser("second@example.com", "Second Admin", "ROLE_ADMIN");
        persistUser("guest@example.com", "Guest User", "ROLE_USER");

        Page<User> result = userRepository.findByRole("ROLE_ADMIN", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("first@example.com", "second@example.com");
    }

    private User persistUser(String email, String fullName, String role) {
        User user = User.builder()
                .email(email)
                .password("encoded-password")
                .fullName(fullName)
                .role(role)
                .enabled(true)
                .build();
        return entityManager.persistAndFlush(user);
    }
}

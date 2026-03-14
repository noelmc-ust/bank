package unaldi.userservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import unaldi.userservice.entity.User;
import unaldi.userservice.entity.enums.Gender;
import unaldi.userservice.entity.enums.Role;
import unaldi.userservice.repository.UserRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seedAdmin() {
        // Change these if you prefer env-based config
        final String adminUsername = "admin";
        final String adminPassword = "admin123";
        final String adminEmail = "admin@bankvault.local";

        Optional<User> existing = userRepository.findByUsername(adminUsername);
        if (existing.isPresent()) {
            return; // already seeded
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);

        User admin = User.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .email(adminEmail)
                .firstName("Admin")
                .lastName("User")
                .phoneNumber("0000000000")
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(Gender.MALE) // or FEMALE; just required for the entity
                .roles(roles)
                .build();

        userRepository.save(admin);
        System.out.println("✅ Seeded default ADMIN user: username='admin', password='admin123'");
    }
}
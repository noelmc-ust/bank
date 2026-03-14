package unaldi.userservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import unaldi.userservice.entity.enums.Gender;
import unaldi.userservice.entity.enums.Role;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_username", columnNames = {"username"}),
        @UniqueConstraint(name = "uk_users_email", columnNames = {"email"})
    },
    indexes = {
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_email", columnList = "email")
    }
)
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @NotBlank
    @Column(name = "password", nullable = false, length = 255) // BCrypt length
    private String password;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @NotBlank
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^[+\\d][\\d\\-\\s]{6,}$",
             message = "Phone number must contain digits and may include +, spaces or hyphens")
    @Column(name = "phone_number", nullable = false, length = 30)
    private String phoneNumber;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Set<Role> roles = new HashSet<>();
}
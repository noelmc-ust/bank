package unaldi.userservice.entity.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import unaldi.userservice.entity.enums.Gender;

import java.time.LocalDate;

@Builder
public record UserSaveRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank
        @Pattern(regexp = "^[+\\d][\\d\\-\\s]{6,}$",
                 message = "Phone number must contain digits and may include +, spaces or hyphens")
        String phoneNumber,
        @NotNull LocalDate birthDate,
        @NotNull Gender gender
) {}
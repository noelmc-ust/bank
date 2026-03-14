package unaldi.userservice.entity.dto;

import unaldi.userservice.entity.enums.Gender;

import java.time.LocalDate;

public record UserDTO(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthDate,
        Gender gender
) {}
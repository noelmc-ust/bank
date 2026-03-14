package unaldi.userservice.auth.dto;

import unaldi.userservice.entity.User;

import java.util.List;

public record AuthUserDTO(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        List<String> roles
) {
    public static AuthUserDTO from(User u) {
        return new AuthUserDTO(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getRoles().stream().map(Enum::name).toList()
        );
    }
}   
    


package unaldi.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;
import unaldi.userservice.auth.JwtService;
import unaldi.userservice.auth.dto.AuthResponse;
import unaldi.userservice.auth.dto.AuthUserDTO;
import unaldi.userservice.auth.dto.LoginRequest;
import unaldi.userservice.entity.User;
import unaldi.userservice.repository.UserRepository;
import unaldi.userservice.utils.result.DataResult;
import unaldi.userservice.utils.result.SuccessDataResult;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // (Optional) normalize username input
        String username = request.username() != null ? request.username().trim() : null;

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, AuthUserDTO.from(user)));
    }

    @GetMapping("/me")
    public ResponseEntity<DataResult<AuthUserDTO>> me(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        Long userId;
        try {
            userId = Long.valueOf(jwtService.parse(token).getBody().getSubject());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return ResponseEntity.ok(new SuccessDataResult<>(AuthUserDTO.from(user), "OK"));
    }
}
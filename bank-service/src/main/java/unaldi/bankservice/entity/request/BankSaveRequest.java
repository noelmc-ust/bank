package unaldi.bankservice.entity.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

/**
 * Copyright (c) 2024
 * All rights reserved.
 */
@Builder
public record BankSaveRequest(
        @NotBlank String bankName,
        @NotBlank String bankCode,
        @NotBlank String branchName,
        @NotBlank String branchCode,
        @NotBlank String accountNumber,
        @NotBlank String address,
        @NotBlank @Email String email,
        @NotBlank
        @Pattern(regexp = "^[+\\d][\\d\\-\\s]{6,}$",
                 message = "Phone number must contain digits and may include +, spaces or hyphens")
        String phoneNumber
) {}
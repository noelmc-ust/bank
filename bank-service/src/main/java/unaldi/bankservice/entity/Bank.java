package unaldi.bankservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Copyright (c) 2024
 * All rights reserved.
 */
@Entity
@Table(
    name = "banks",
    indexes = {
        @Index(name = "idx_banks_bank_code", columnList = "bank_code"),
        @Index(name = "idx_banks_branch_code", columnList = "branch_code")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_banks_bank_branch_code", columnNames = {"bank_code", "branch_code"}),
        @UniqueConstraint(name = "uk_banks_account_number", columnNames = {"account_number"})
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @NotBlank
    @Column(name = "bank_code", nullable = false, length = 20)
    private String bankCode;

    @NotBlank
    @Column(name = "branch_name", nullable = false, length = 100)
    private String branchName;

    @NotBlank
    @Column(name = "branch_code", nullable = false, length = 20)
    private String branchCode;

    @NotBlank
    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @NotBlank
    @Column(name = "address", nullable = false, length = 250)
    private String address;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, length = 250)
    private String email;

    @NotBlank
    @Pattern(
        regexp = "^[+\\d][\\d\\-\\s]{6,}$",
        message = "Phone number must contain digits and may include +, spaces or hyphens"
    )
    @Column(name = "phone_number", nullable = false, length = 250)
    private String phoneNumber;
}
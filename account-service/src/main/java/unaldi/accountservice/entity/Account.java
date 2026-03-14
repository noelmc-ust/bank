package unaldi.accountservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import unaldi.accountservice.entity.enums.AccountStatus;
import unaldi.accountservice.entity.enums.AccountType;

@Entity
@Table(
    name = "accounts",
    indexes = {
        @Index(name = "idx_accounts_user_id", columnList = "user_id"),
        @Index(name = "idx_accounts_bank_id", columnList = "bank_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_accounts_account_number", columnNames = {"account_number"})
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // String → keep NotBlank
    @NotBlank
    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    // Numeric/Enum → use NotNull
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @PositiveOrZero
    @Column(name = "balance", nullable = false)
    private Double balance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 20)
    private AccountStatus accountStatus;

    @NotNull
    @Column(name = "bank_id", nullable = false)
    private Long bankId;
}
package unaldi.creditcardservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "credit_cards",
    indexes = {
        @Index(name = "idx_credit_cards_user_id", columnList = "user_id"),
        @Index(name = "idx_credit_cards_bank_id", columnList = "bank_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_credit_cards_card_number", columnNames = {"card_number"})
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // String → keep NotBlank
    @NotBlank
    @Column(name = "card_number", nullable = false, length = 20)
    private String cardNumber;

    // IDs/numbers/dates → NotNull (not NotBlank)
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // A card must not be expired at creation/update
    @NotNull
    @FutureOrPresent
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    // 3 or 4 digits CVV
    @NotBlank
    @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3 or 4 digits")
    @Column(name = "cvv", nullable = false, length = 4)
    private String cvv;

    @NotNull
    @PositiveOrZero
    @Column(name = "credit_limit", nullable = false)
    private Double creditLimit;

    @NotNull
    @PositiveOrZero
    @Column(name = "debt_amount", nullable = false)
    private Double debtAmount;

    @NotNull
    @Column(name = "bank_id", nullable = false)
    private Long bankId;
}
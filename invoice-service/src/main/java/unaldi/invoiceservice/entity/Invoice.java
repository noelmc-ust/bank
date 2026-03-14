package unaldi.invoiceservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import unaldi.invoiceservice.entity.enums.PaymentStatus;

import java.time.LocalDate;

@Entity
@Table(
    name = "invoices",
    indexes = {
        @Index(name = "idx_invoices_user_id", columnList = "user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_invoices_invoice_number", columnNames = {"invoice_number"})
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "invoice_number", nullable = false, length = 20)
    private String invoiceNumber;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @PositiveOrZero
    @Column(name = "amount", nullable = false)
    private Double amount;

    @NotNull
    @PastOrPresent
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @NotNull
    @FutureOrPresent
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;
}
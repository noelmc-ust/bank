package unaldi.invoiceservice.entity.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import unaldi.invoiceservice.entity.enums.PaymentStatus;

import java.time.LocalDate;

@Builder
public record InvoiceUpdateRequest(
        @NotNull Long id,
        @NotBlank String invoiceNumber,
        @NotNull Long userId,
        @NotNull @PositiveOrZero Double amount,
        @NotNull @PastOrPresent LocalDate invoiceDate,
        @NotNull @FutureOrPresent LocalDate dueDate,
        @NotNull PaymentStatus paymentStatus
) {}
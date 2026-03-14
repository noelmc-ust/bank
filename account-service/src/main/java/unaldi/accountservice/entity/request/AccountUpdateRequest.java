package unaldi.accountservice.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import unaldi.accountservice.entity.enums.AccountStatus;
import unaldi.accountservice.entity.enums.AccountType;

@Builder
public record AccountUpdateRequest(
        @NotNull Long id,
        @NotBlank String accountNumber,
        @NotNull Long userId,
        @NotNull @PositiveOrZero Double balance,
        @NotNull AccountType accountType,
        @NotNull AccountStatus accountStatus,
        @NotNull Long bankId
) {}
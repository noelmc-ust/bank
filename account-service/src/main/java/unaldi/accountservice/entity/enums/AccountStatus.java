package unaldi.accountservice.entity.enums;

import lombok.Getter;

@Getter
public enum AccountStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    CLOSED("Closed"); // ✅ added

    private final String message;

    AccountStatus(String message) {
        this.message = message;
    }
}
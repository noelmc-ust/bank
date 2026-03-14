package unaldi.accountservice.entity.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    CREDIT("Credit"),
    DEPOSIT("Deposit"),
    SAVINGS("Savings"); // ✅ was CURRENT, align to SAVINGS for frontend

    private final String message;

    AccountType(String message) {
        this.message = message;
    }
}
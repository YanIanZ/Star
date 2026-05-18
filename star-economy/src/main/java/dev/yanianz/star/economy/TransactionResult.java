package dev.yanianz.star.economy;

import javax.annotation.Nonnull;

public record TransactionResult(boolean success, double amount, double newBalance, String message) {
    public static TransactionResult ok(double amount, double newBalance) {
        return new TransactionResult(true, amount, newBalance, "");
    }
    public static TransactionResult ok(double amount, double newBalance, String message) {
        return new TransactionResult(true, amount, newBalance, message);
    }
    public static TransactionResult fail(String message) {
        return new TransactionResult(false, 0, 0, message);
    }

    @Nonnull public TransactionResult withMessage(@Nonnull String newMessage) {
        return new TransactionResult(success, amount, newBalance, newMessage);
    }
}

package dev.yanianz.star.economy;
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
}

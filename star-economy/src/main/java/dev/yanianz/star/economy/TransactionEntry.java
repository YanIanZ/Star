package dev.yanianz.star.economy;
import java.util.UUID;
public record TransactionEntry(UUID playerId, TransactionType type, double amount, String reason, String formattedAmount, long timestamp) {}

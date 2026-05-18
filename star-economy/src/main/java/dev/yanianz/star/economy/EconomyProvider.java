package dev.yanianz.star.economy;

import dev.yanianz.star.economy.banks.Bank;
import org.bukkit.OfflinePlayer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public interface EconomyProvider {
    @Nonnull String getName();
    boolean isEnabled();
    boolean hasAccount(@Nonnull OfflinePlayer player);
    double getBalance(@Nonnull OfflinePlayer player);
    @Nonnull default TransactionResult withdraw(@Nonnull OfflinePlayer player, double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be positive");
        if (!hasAccount(player)) return TransactionResult.fail("No account");
        if (!canAfford(player, amount)) return TransactionResult.fail("Insufficient funds");
        setBalance(player, getBalance(player) - amount);
        return TransactionResult.ok(amount, getBalance(player));
    }
    @Nonnull default TransactionResult deposit(@Nonnull OfflinePlayer player, double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be positive");
        if (!hasAccount(player)) return TransactionResult.fail("No account");
        setBalance(player, getBalance(player) + amount);
        return TransactionResult.ok(amount, getBalance(player));
    }
    default boolean canAfford(@Nonnull OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }
    boolean setBalance(@Nonnull OfflinePlayer player, double amount);
    @Nonnull String format(double amount);
    @Nonnull default String currencyNameSingular() { return "Dollar"; }
    @Nonnull default String currencyNamePlural() { return "Dollars"; }
    @Nonnull default String currencySymbol() { return "$"; }
    default boolean hasBankSupport() { return false; }
    default boolean createBank(@Nonnull String name, @Nullable OfflinePlayer owner) { return false; }
    default double getBankBalance(@Nonnull String name) { return 0; }
    @Nonnull default TransactionResult bankDeposit(@Nonnull String name, double amount) { return TransactionResult.fail("Bank not supported"); }
    @Nonnull default TransactionResult bankWithdraw(@Nonnull String name, double amount) { return TransactionResult.fail("Bank not supported"); }
    default boolean isBankOwner(@Nonnull String name, @Nullable OfflinePlayer player) { return false; }
    default boolean isBankMember(@Nonnull String name, @Nullable OfflinePlayer player) { return false; }
    default boolean deleteBank(@Nonnull String name) { return false; }
    @Nonnull default List<Bank> getBanks() { return Collections.emptyList(); }
    @Nonnull default List<CurrencyType> getCurrencies() { return Collections.emptyList(); }
    default double getBalance(@Nonnull OfflinePlayer player, @Nonnull CurrencyType currency) { return getBalance(player); }
}

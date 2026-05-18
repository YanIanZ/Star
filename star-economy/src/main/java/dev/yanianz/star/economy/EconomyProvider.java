package dev.yanianz.star.economy;

import dev.yanianz.star.economy.banks.Bank;
import org.bukkit.OfflinePlayer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Interface for economy providers. Implement this to integrate a specific
 * economy backend ({@code Vault}, {@code EssentialsX}, {@code CMI}, etc.).
 * Default implementations for most methods provide safe fallbacks.
 */
public interface EconomyProvider {
    @Nonnull String getName();
    boolean isEnabled();

    /**
     * Checks whether the given player has an economy account.
     */
    boolean hasAccount(@Nonnull OfflinePlayer player);

    /**
     * Gets the current balance of the given player.
     */
    double getBalance(@Nonnull OfflinePlayer player);
    /**
     * Withdraws the specified amount from the player.
     * Returns a {@link TransactionResult} indicating success or failure.
     */
    @Nonnull default TransactionResult withdraw(@Nonnull OfflinePlayer player, double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be positive");
        if (!hasAccount(player)) return TransactionResult.fail("No account");
        if (!canAfford(player, amount)) return TransactionResult.fail("Insufficient funds");
        setBalance(player, getBalance(player) - amount);
        return TransactionResult.ok(amount, getBalance(player));
    }
    /**
     * Deposits the specified amount to the player.
     * Returns a {@link TransactionResult} indicating success or failure.
     */
    @Nonnull default TransactionResult deposit(@Nonnull OfflinePlayer player, double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be positive");
        if (!hasAccount(player)) return TransactionResult.fail("No account");
        setBalance(player, getBalance(player) + amount);
        return TransactionResult.ok(amount, getBalance(player));
    }
    /**
     * Checks if the player has at least the specified amount.
     */
    default boolean canAfford(@Nonnull OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }
    /**
     * Sets the player's balance to the exact amount.
     */
    boolean setBalance(@Nonnull OfflinePlayer player, double amount);

    /**
     * Formats the amount as a human-readable currency string (e.g. {@code $1,500.00}).
     */
    @Nonnull String format(double amount);

    /**
     * Returns the currency name (singular form).
     */
    @Nonnull default String currencyNameSingular() { return "Dollar"; }

    /**
     * Returns the currency name (plural form).
     */
    @Nonnull default String currencyNamePlural() { return "Dollars"; }

    /**
     * Returns the currency symbol (e.g. {@code $}, {@code €}).
     */
    @Nonnull default String currencySymbol() { return "$"; }

    /**
     * Returns whether this provider supports bank operations.
     */
    default boolean hasBankSupport() { return false; }

    /**
     * Creates a bank with the given name and optional owner.
     */
    default boolean createBank(@Nonnull String name, @Nullable OfflinePlayer owner) { return false; }

    /**
     * Gets the balance of a bank.
     */
    default double getBankBalance(@Nonnull String name) { return 0; }

    /**
     * Deposits the amount into the bank.
     */
    @Nonnull default TransactionResult bankDeposit(@Nonnull String name, double amount) { return TransactionResult.fail("Bank not supported"); }

    /**
     * Withdraws the amount from the bank.
     */
    @Nonnull default TransactionResult bankWithdraw(@Nonnull String name, double amount) { return TransactionResult.fail("Bank not supported"); }

    /**
     * Checks if the player is an owner of the bank.
     */
    default boolean isBankOwner(@Nonnull String name, @Nullable OfflinePlayer player) { return false; }

    /**
     * Checks if the player is a member of the bank.
     */
    default boolean isBankMember(@Nonnull String name, @Nullable OfflinePlayer player) { return false; }

    /**
     * Deletes the bank.
     */
    default boolean deleteBank(@Nonnull String name) { return false; }

    /**
     * Returns all banks managed by this provider.
     */
    @Nonnull default List<Bank> getBanks() { return Collections.emptyList(); }

    /**
     * Returns the list of supported currencies.
     */
    @Nonnull default List<CurrencyType> getCurrencies() { return Collections.emptyList(); }

    /**
     * Gets the player's balance in the specified currency.
     */
    default double getBalance(@Nonnull OfflinePlayer player, @Nonnull CurrencyType currency) { return getBalance(player); }
}

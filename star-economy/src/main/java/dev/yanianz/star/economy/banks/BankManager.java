package dev.yanianz.star.economy.banks;

import dev.yanianz.star.economy.EconomyProvider;
import dev.yanianz.star.economy.TransactionResult;
import org.bukkit.OfflinePlayer;
import javax.annotation.Nonnull;
import java.util.List;

public final class BankManager {
    private final EconomyProvider provider;

    public BankManager(@Nonnull EconomyProvider provider) {
        this.provider = provider;
    }

    @Nonnull
    public TransactionResult create(@Nonnull String name, @Nonnull OfflinePlayer owner) {
        if (!provider.hasBankSupport()) return TransactionResult.fail("Bank not supported");
        boolean created = provider.createBank(name, owner);
        return created ? TransactionResult.ok(0, 0, "Bank created") : TransactionResult.fail("Failed to create bank");
    }

    public double getBalance(@Nonnull String name) {
        return provider.getBankBalance(name);
    }

    @Nonnull
    public TransactionResult deposit(@Nonnull String name, double amount) {
        return provider.bankDeposit(name, amount);
    }

    @Nonnull
    public TransactionResult withdraw(@Nonnull String name, double amount) {
        return provider.bankWithdraw(name, amount);
    }

    public boolean isOwner(@Nonnull String name, @Nonnull OfflinePlayer player) {
        return provider.isBankOwner(name, player);
    }

    public boolean isMember(@Nonnull String name, @Nonnull OfflinePlayer player) {
        return provider.isBankMember(name, player);
    }

    @Nonnull
    public TransactionResult delete(@Nonnull String name) {
        boolean deleted = provider.deleteBank(name);
        return deleted ? TransactionResult.ok(0, 0, "Bank deleted") : TransactionResult.fail("Failed to delete bank");
    }

    @Nonnull
    public List<Bank> getBanks() {
        return provider.getBanks();
    }
}

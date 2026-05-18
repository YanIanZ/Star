package dev.yanianz.star.economy.providers;

import dev.yanianz.star.economy.EconomyProvider;
import dev.yanianz.star.economy.TransactionResult;
import dev.yanianz.star.economy.banks.Bank;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class VaultEconomyProvider extends AbstractEconomyProvider {

    private Economy vaultEconomy;

    public VaultEconomyProvider() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            this.vaultEconomy = rsp.getProvider();
            this.enabled = vaultEconomy != null && vaultEconomy.isEnabled();
        }
    }

    @Override @Nonnull public String getName() { return "Vault"; }

    @Override
    public boolean hasAccount(@Nonnull OfflinePlayer player) {
        return enabled && vaultEconomy.hasAccount(player);
    }

    @Override
    public double getBalance(@Nonnull OfflinePlayer player) {
        return enabled && hasAccount(player) ? vaultEconomy.getBalance(player) : 0;
    }

    @Override
    public boolean canAfford(@Nonnull OfflinePlayer player, double amount) {
        return enabled && vaultEconomy.has(player, amount);
    }

    @Override
    public boolean setBalance(@Nonnull OfflinePlayer player, double amount) {
        if (!enabled || !hasAccount(player)) return false;
        double diff = amount - getBalance(player);
        if (diff > 0) vaultEconomy.depositPlayer(player, diff);
        else if (diff < 0) vaultEconomy.withdrawPlayer(player, -diff);
        return true;
    }

    @Override @Nonnull
    public TransactionResult withdraw(@Nonnull OfflinePlayer player, double amount) {
        if (!enabled) return TransactionResult.fail("Economy not enabled");
        if (amount < 0) throw new IllegalArgumentException("Amount must be positive");
        net.milkbowl.vault.economy.EconomyResponse resp = vaultEconomy.withdrawPlayer(player, amount);
        return resp.transactionSuccess()
            ? TransactionResult.ok(amount, resp.balance, resp.errorMessage)
            : TransactionResult.fail(resp.errorMessage);
    }

    @Override @Nonnull
    public TransactionResult deposit(@Nonnull OfflinePlayer player, double amount) {
        if (!enabled) return TransactionResult.fail("Economy not enabled");
        if (amount < 0) throw new IllegalArgumentException("Amount must be positive");
        net.milkbowl.vault.economy.EconomyResponse resp = vaultEconomy.depositPlayer(player, amount);
        return resp.transactionSuccess()
            ? TransactionResult.ok(amount, resp.balance, resp.errorMessage)
            : TransactionResult.fail(resp.errorMessage);
    }

    @Override @Nonnull public String format(double amount) {
        return enabled ? vaultEconomy.format(amount) : super.format(amount);
    }

    @Override @Nonnull public String currencyNameSingular() {
        return enabled ? vaultEconomy.currencyNameSingular() : super.currencyNameSingular();
    }

    @Override @Nonnull public String currencyNamePlural() {
        return enabled ? vaultEconomy.currencyNamePlural() : super.currencyNamePlural();
    }

    @Override
    public boolean hasBankSupport() {
        return enabled && vaultEconomy.hasBankSupport();
    }

    @Override
    public boolean createBank(@Nonnull String name, OfflinePlayer owner) {
        if (!enabled || !hasBankSupport()) return false;
        vaultEconomy.createBank(name, owner);
        return vaultEconomy.isBankOwner(name, owner).transactionSuccess();
    }

    @Override
    public double getBankBalance(@Nonnull String name) {
        if (!enabled || !hasBankSupport()) return 0;
        net.milkbowl.vault.economy.EconomyResponse resp = vaultEconomy.bankBalance(name);
        return resp.transactionSuccess() ? resp.balance : 0;
    }

    @Override @Nonnull
    public TransactionResult bankDeposit(@Nonnull String name, double amount) {
        if (!enabled || !hasBankSupport()) return TransactionResult.fail("Banks not supported");
        net.milkbowl.vault.economy.EconomyResponse resp = vaultEconomy.bankDeposit(name, amount);
        return resp.transactionSuccess()
            ? TransactionResult.ok(amount, resp.balance)
            : TransactionResult.fail(resp.errorMessage);
    }

    @Override @Nonnull
    public TransactionResult bankWithdraw(@Nonnull String name, double amount) {
        if (!enabled || !hasBankSupport()) return TransactionResult.fail("Banks not supported");
        net.milkbowl.vault.economy.EconomyResponse resp = vaultEconomy.bankWithdraw(name, amount);
        return resp.transactionSuccess()
            ? TransactionResult.ok(amount, resp.balance)
            : TransactionResult.fail(resp.errorMessage);
    }

    @Override
    public boolean isBankOwner(@Nonnull String name, OfflinePlayer player) {
        if (!enabled || !hasBankSupport() || player == null) return false;
        return vaultEconomy.isBankOwner(name, player).transactionSuccess();
    }

    @Override
    public boolean isBankMember(@Nonnull String name, OfflinePlayer player) {
        if (!enabled || !hasBankSupport() || player == null) return false;
        return vaultEconomy.isBankMember(name, player).transactionSuccess();
    }

    @Override
    public boolean deleteBank(@Nonnull String name) {
        if (!enabled || !hasBankSupport()) return false;
        return vaultEconomy.deleteBank(name).transactionSuccess();
    }

    @Override @Nonnull
    public List<Bank> getBanks() {
        List<Bank> banks = new ArrayList<>();
        if (enabled && hasBankSupport()) {
            for (String name : vaultEconomy.getBanks()) {
                Bank bank = new Bank(name, null);
                bank.setBalance(getBankBalance(name));
                banks.add(bank);
            }
        }
        return banks;
    }
}

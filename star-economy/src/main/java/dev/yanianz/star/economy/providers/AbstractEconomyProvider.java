package dev.yanianz.star.economy.providers;

import dev.yanianz.star.economy.CurrencyType;
import dev.yanianz.star.economy.EconomyProvider;
import dev.yanianz.star.economy.TransactionResult;
import dev.yanianz.star.economy.banks.Bank;
import org.bukkit.OfflinePlayer;
import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public abstract class AbstractEconomyProvider implements EconomyProvider {
    protected boolean enabled;

    @Override public boolean isEnabled() { return enabled; }

    @Override @Nonnull public String format(double amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return nf.format(amount);
    }

    @Override public boolean hasBankSupport() { return false; }
    @Override @Nonnull public List<Bank> getBanks() { return Collections.emptyList(); }
    @Override @Nonnull public List<CurrencyType> getCurrencies() { return Collections.emptyList(); }
}

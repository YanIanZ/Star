package dev.yanianz.star.economy;

import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.util.Locale;

public final class CurrencyFormatter {
    private CurrencyFormatter() {}

    @Nonnull
    public static String format(@Nonnull EconomyProvider provider, double amount) {
        return provider.format(amount);
    }

    @Nonnull
    public static String formatShort(@Nonnull EconomyProvider provider, double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        if (amount >= 1_000_000) return nf.format(amount / 1_000_000) + "M " + provider.currencySymbol();
        if (amount >= 1_000) return nf.format(amount / 1_000) + "K " + provider.currencySymbol();
        return nf.format(amount) + " " + provider.currencySymbol();
    }

    @Nonnull
    public static String formatRaw(double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return nf.format(amount);
    }
}

package dev.yanianz.star.economy;
import javax.annotation.Nonnull;
public record CurrencyType(@Nonnull String id, @Nonnull String name, @Nonnull String symbol) {
    @Nonnull public CurrencyType withName(@Nonnull String name) { return new CurrencyType(id, name, symbol); }
    @Nonnull public CurrencyType withSymbol(@Nonnull String symbol) { return new CurrencyType(id, name, symbol); }
}

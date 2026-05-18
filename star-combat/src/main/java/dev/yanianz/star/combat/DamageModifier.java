package dev.yanianz.star.combat;
import javax.annotation.Nonnull;

public record DamageModifier(@Nonnull String name, double multiplier, @Nonnull DamageType... types) {
    public boolean appliesTo(@Nonnull DamageType type) {
        if (types.length == 0) return true;
        for (DamageType t : types) if (t == type) return true;
        return false;
    }
}

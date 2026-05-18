package dev.yanianz.star.combat;

import javax.annotation.Nonnull;

/** Multiplier applied to damage for specific damage types. */
public final class DamageModifier {
    private final String name;
    private final double multiplier;
    private final DamageType[] types;

    public DamageModifier(@Nonnull String name, double multiplier, @Nonnull DamageType... types) {
        this.name = name;
        this.multiplier = multiplier;
        this.types = types;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public boolean appliesTo(@Nonnull DamageType type) {
        for (DamageType t : types) {
            if (t == type) return true;
        }
        return types.length == 0;
    }
}

package dev.yanianz.star.combat;

/** Types of damage with base armor penetration. */
public enum DamageType {
    MELEE(0), RANGED(0.3), MAGIC(0.7), FIRE(0.5), EXPLOSION(0.4), POISON(1.0), FALL(0.8), VOID(1.0), CUSTOM(0);

    private final double armorPenetration;

    DamageType(double ap) {
        this.armorPenetration = ap;
    }

    public double getArmorPenetration() {
        return armorPenetration;
    }
}

package dev.yanianz.star.combat;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/** Calculates damage with armor, enchantments, and custom modifiers. */
public final class DamageCalculator {
    private final List<DamageModifier> modifiers = new ArrayList<>();

    public void addModifier(@Nonnull DamageModifier mod) {
        modifiers.add(mod);
    }

    public void removeModifier(@Nonnull String name) {
        modifiers.removeIf(m -> m.name().equals(name));
    }

    public void clearModifiers() {
        modifiers.clear();
    }

    public double calculate(double baseDamage, @Nonnull LivingEntity attacker, @Nonnull LivingEntity defender, @Nonnull DamageType type) {
        double damage = baseDamage;
        for (DamageModifier mod : modifiers) {
            if (mod.appliesTo(type)) damage *= mod.multiplier();
        }
        double pen = type.getArmorPenetration();
        double armor = getArmor(defender) * (1 - pen);
        double reduction = armor / (armor + 100);
        damage *= (1 - reduction);
        if (defender instanceof Player p && p.getActivePotionEffects() != null) {
            damage *= 1 - 0.04 * p.getActivePotionEffects().size();
        }
        return Math.max(0, Math.round(damage * 100.0) / 100.0);
    }

    private double getArmor(LivingEntity entity) {
        double armor = 0;
        for (ItemStack item : entity.getEquipment().getArmorContents()) {
            if (item != null) {
                String type = item.getType().name();
                if (type.endsWith("_HELMET")) armor += 1.5;
                else if (type.endsWith("_CHESTPLATE")) armor += 3;
                else if (type.endsWith("_LEGGINGS")) armor += 2;
                else if (type.endsWith("_BOOTS")) armor += 1;
                if (item.containsEnchantment(Enchantment.PROTECTION))
                    armor += item.getEnchantmentLevel(Enchantment.PROTECTION) * 0.5;
            }
        }
        return armor;
    }
}

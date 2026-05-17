package dev.yanianz.star.protection.modules;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import dev.yanianz.star.protection.ActionType;
import dev.yanianz.star.protection.Interaction;
import dev.yanianz.star.protection.ProtectionModule;

import nl.rutgerkok.blocklocker.BlockLockerAPIv2;
import nl.rutgerkok.blocklocker.BlockLockerPlugin;
import nl.rutgerkok.blocklocker.profile.Profile;
import nl.rutgerkok.blocklocker.protection.Protection;

public class BlockLockerProtectionModule implements ProtectionModule {

    private BlockLockerPlugin api;
    private final Plugin plugin;

    public BlockLockerProtectionModule(@Nonnull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void load() {
        api = BlockLockerAPIv2.getPlugin();
    }

    @Override
    public boolean hasPermission(OfflinePlayer p, Location l, Interaction action) {
        if (action.getType() != ActionType.BLOCK) {
            return true;
        }

        Optional<Protection> protection = api.getProtectionFinder().findProtection(l.getBlock());

        if (protection.isPresent()) {
            Profile profile = api.getProfileFactory().fromNameAndUniqueId(p.getName(), Optional.of(p.getUniqueId()));
            return protection.get().isAllowed(profile);
        } else {
            return true;
        }

    }
}

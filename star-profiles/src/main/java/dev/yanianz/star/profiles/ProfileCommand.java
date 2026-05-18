package dev.yanianz.star.profiles;

import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class ProfileCommand {
    private final ProfileManager manager;

    public ProfileCommand(@Nonnull ProfileManager manager) { this.manager = manager; }

    public void show(@Nonnull Player player) {
        player.sendMessage("Profiles: " + String.join(", ", manager.getProfileNames(player)));
    }
}

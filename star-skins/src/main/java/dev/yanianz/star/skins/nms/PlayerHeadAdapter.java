package dev.yanianz.star.skins.nms;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.block.Block;

import com.mojang.authlib.GameProfile;

import dev.yanianz.star.common.StarLogger;
import dev.yanianz.star.versions.MinecraftVersion;

public interface PlayerHeadAdapter {

    @ParametersAreNonnullByDefault
    void setGameProfile(Block block, GameProfile profile, boolean sendBlockUpdate) throws IllegalAccessException, InvocationTargetException, InstantiationException;

    public static @Nullable PlayerHeadAdapter get() {
        try {
            MinecraftVersion version = MinecraftVersion.get();

            if (version.isAtLeast(1, 21, 11)) {
                return new PlayerHeadAdapter21v11();
            }
            if (version.isAtLeast(1, 21, 4)) {
                return new PlayerHeadAdapter21v4();
            }
            if (version.isAtLeast(1, 21)) {
                return new PlayerHeadAdapter21();
            }
            if (version.isAtLeast(1, 20, 5)) {
                return new PlayerHeadAdapter20v5();
            } else if (version.isAtLeast(1, 18)) {
                return new PlayerHeadAdapter18();
            } else if (version.isAtLeast(1, 17)) {
                return new PlayerHeadAdapter17();
            } else {
                return new PlayerHeadAdapterBefore17();
            }
        } catch (Exception x) {
            StarLogger logger = new StarLogger("skins");
            logger.log(Level.SEVERE, "Failed to create player head adapter", x);
            return null;
        }

    }
}

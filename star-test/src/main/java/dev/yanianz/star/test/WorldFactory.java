package dev.yanianz.star.test;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import javax.annotation.Nonnull;

public final class WorldFactory {
    private WorldFactory() {}

    @Nonnull public static World create(@Nonnull String name) { return new WorldCreator(name).createWorld(); }
}

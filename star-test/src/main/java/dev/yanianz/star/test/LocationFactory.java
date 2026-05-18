package dev.yanianz.star.test;
import org.bukkit.Location;
import org.bukkit.World;
import javax.annotation.Nonnull;

public final class LocationFactory {
    private LocationFactory() {}

    @Nonnull public static Location of(@Nonnull World world, double x, double y, double z) { return new Location(world, x, y, z); }
    @Nonnull public static Location zero(@Nonnull World world) { return new Location(world, 0, 0, 0); }
    @Nonnull public static Location ofBlock(@Nonnull World world, int x, int y, int z) { return new Location(world, x, y, z); }
}

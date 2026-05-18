package dev.yanianz.star.test;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

public final class PlayerFactory {
    private PlayerFactory() {}

    @Nonnull
    public static PlayerMock create(@Nonnull String name) {
        return new PlayerMock(name);
    }

    public static final class PlayerMock {
        private final String name;
        private double health = 20;
        private GameMode gameMode = GameMode.SURVIVAL;
        private int level;
        private float exp;

        PlayerMock(String name) { this.name = name; }

        @Nonnull public PlayerMock withHealth(double health) { this.health = health; return this; }
        @Nonnull public PlayerMock withGameMode(@Nonnull GameMode gm) { this.gameMode = gm; return this; }
        @Nonnull public PlayerMock withLevel(int level) { this.level = level; return this; }
        @Nonnull public PlayerMock withExp(float exp) { this.exp = exp; return this; }

        @Nonnull public String getName() { return name; }
        public double getHealth() { return health; }
        @Nonnull public GameMode getGameMode() { return gameMode; }
        public int getLevel() { return level; }
        public float getExp() { return exp; }
    }
}

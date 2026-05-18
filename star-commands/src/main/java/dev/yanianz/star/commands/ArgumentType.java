package dev.yanianz.star.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ArgumentType<T> {
    public static final ArgumentType<Integer> INTEGER = new ArgumentType<>(Integer::parseInt, List.of("0", "1", "10"));
    public static final ArgumentType<Double> DOUBLE = new ArgumentType<>(Double::parseDouble, List.of("0.0", "1.0"));
    public static final ArgumentType<String> STRING = new ArgumentType<>(s -> s, List.of());
    public static final ArgumentType<Boolean> BOOLEAN = new ArgumentType<>(s -> "true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s) || "1".equals(s), List.of("true", "false"));
    public static final ArgumentType<Player> PLAYER = new ArgumentType<>(s -> Bukkit.getPlayer(s), () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
    public static final ArgumentType<OfflinePlayer> OFFLINE_PLAYER = new ArgumentType<>(s -> Bukkit.getOfflinePlayer(s), () -> Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList()));
    public static final ArgumentType<World> WORLD = new ArgumentType<>(s -> Bukkit.getWorld(s), () -> Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
    public static final ArgumentType<Material> MATERIAL = new ArgumentType<>(s -> Material.matchMaterial(s), () -> Arrays.stream(Material.values()).filter(Material::isItem).map(m -> m.name().toLowerCase()).collect(Collectors.toList()));
    public static final ArgumentType<GameMode> GAMEMODE = new ArgumentType<>(s -> GameMode.valueOf(s.toUpperCase()), List.of("survival", "creative", "adventure", "spectator"));

    private final Function<String, T> parser;
    private final TabSupplier tabSupplier;

    public interface TabSupplier { @Nonnull List<String> get(); }

    public ArgumentType(@Nonnull Function<String, T> parser, @Nonnull List<String> suggestions) {
        this.parser = parser;
        this.tabSupplier = () -> suggestions;
    }

    public ArgumentType(@Nonnull Function<String, T> parser, @Nonnull TabSupplier tabSupplier) {
        this.parser = parser; this.tabSupplier = tabSupplier;
    }

    @Nullable
    public T parse(@Nonnull String input) {
        try { return parser.apply(input); } catch (Exception e) { return null; }
    }

    @Nonnull
    public List<String> tabComplete() { return tabSupplier.get(); }

    @Nonnull
    public static <E extends Enum<E>> ArgumentType<E> ofEnum(@Nonnull Class<E> enumClass) {
        return new ArgumentType<>(s -> Enum.valueOf(enumClass, s.toUpperCase()),
            Arrays.stream(enumClass.getEnumConstants()).map(e -> e.name().toLowerCase()).collect(Collectors.toList()));
    }

    @Nonnull
    public static <T> ArgumentType<T> custom(@Nonnull Function<String, T> parser, @Nonnull List<String> suggestions) {
        return new ArgumentType<>(parser, suggestions);
    }
}

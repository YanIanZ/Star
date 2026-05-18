package dev.yanianz.star.commands;

import dev.yanianz.star.common.ChatColors;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class CommandContext {
    private final CommandSender sender;
    private final String label;
    private final String[] args;
    private final Map<String, Object> parsed = new HashMap<>();

    public CommandContext(@Nonnull CommandSender sender, @Nonnull String label, @Nonnull String[] args) {
        this.sender = sender;
        this.label = label;
        this.args = args;
    }

    @Nonnull
    public CommandSender sender() { return sender; }

    @Nonnull
    public Player asPlayer() { return (Player) sender; }

    public boolean isPlayer() { return sender instanceof Player; }

    @Nonnull
    public String label() { return label; }

    @Nonnull
    public String[] args() { return args; }

    public int argCount() { return args.length; }

    @Nullable
    public String arg(int index) {
        return index < args.length ? args[index] : null;
    }

    public void send(@Nonnull String message) {
        sender.sendMessage(ChatColors.colorToComponent(message));
    }

    public void send(@Nonnull Component component) {
        sender.sendMessage(component);
    }

    public void set(@Nonnull String key, @Nullable Object value) { parsed.put(key, value); }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(@Nonnull String key) { return (T) parsed.get(key); }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <T> T getOrDefault(@Nonnull String key, @Nonnull T defaultValue) {
        return (T) parsed.getOrDefault(key, defaultValue);
    }

    public boolean has(@Nonnull String key) { return parsed.containsKey(key); }

    @Nonnull
    public Map<String, Object> parsed() { return Map.copyOf(parsed); }
}

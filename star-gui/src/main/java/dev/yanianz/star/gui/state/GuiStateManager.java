package dev.yanianz.star.gui.state;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class GuiStateManager {

    private final Map<UUID, GuiState<?>> states = new ConcurrentHashMap<>();

    public void register(@Nonnull Player player, @Nonnull GuiState<?> state) {
        states.put(player.getUniqueId(), state);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <T> T get(@Nonnull Player player, @Nonnull Class<T> type) {
        GuiState<?> state = states.get(player.getUniqueId());
        if (state == null) {
            throw new IllegalStateException("No GUI state registered for player " + player.getName());
        }
        return (T) state.getData();
    }

    public void update(@Nonnull Player player, @Nonnull Object data) {
        GuiState<?> state = states.get(player.getUniqueId());
        if (state != null) {
            ((GuiState<Object>) state).setData(data);
        }
    }

    public <D> void updateState(@Nonnull Player player, @Nonnull GuiState<D> state) {
        states.put(player.getUniqueId(), state);
    }

    public void unregister(@Nonnull Player player) {
        states.remove(player.getUniqueId());
    }

    public boolean isRegistered(@Nonnull Player player) {
        return states.containsKey(player.getUniqueId());
    }
}

package dev.yanianz.star.quests;

import dev.yanianz.star.common.StarLogger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class QuestManager {

    private final Plugin plugin;
    private final StarLogger logger;
    private final Map<String, Quest> quests = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerQuestData> playerData = new ConcurrentHashMap<>();

    public QuestManager(@Nonnull Plugin plugin) {
        this.plugin = plugin;
        this.logger = new StarLogger(plugin.getServer(), "quests");
    }

    public void register(@Nonnull Quest quest) {
        quests.put(quest.getName(), quest);
    }

    @Nonnull
    public Optional<Quest> get(@Nonnull String name) {
        return Optional.ofNullable(quests.get(name));
    }

    @Nonnull
    public Collection<Quest> getAll() {
        return quests.values();
    }

    public void assign(@Nonnull Player player, @Nonnull Quest quest) {
        getOrCreate(player).start(quest.getName());
    }

    public boolean isActive(@Nonnull Player player, @Nonnull String name) {
        return getOrCreate(player).isActive(name);
    }

    public boolean isCompleted(@Nonnull Player player, @Nonnull String name) {
        return getOrCreate(player).isCompleted(name);
    }

    public void progress(@Nonnull Player player, @Nonnull ObjectiveType type, @Nonnull String target, int amount) {
        PlayerQuestData data = getOrCreate(player);
        for (String activeQuest : data.getActive()) {
            data.addProgress(activeQuest, type.name() + ":" + target, amount);
            Quest quest = quests.get(activeQuest);
            if (quest != null && quest.isComplete(data)) {
                complete(player, quest);
            }
        }
    }

    public void complete(@Nonnull Player player, @Nonnull Quest quest) {
        PlayerQuestData data = getOrCreate(player);
        data.complete(quest.getName());
        logger.log(Level.INFO, player.getName() + " completed quest: " + quest.getName());
        quest.getRewards().forEach(r -> {
            /* reward dispatch — handled by consumer via listener */
        });
        if (quest.getChain() != null) {
            quest.getChain().getNext(quest.getName())
                .flatMap(this::get)
                .ifPresent(next -> assign(player, next));
        }
    }

    private PlayerQuestData getOrCreate(Player player) {
        return playerData.computeIfAbsent(player.getUniqueId(), id -> new PlayerQuestData());
    }
}

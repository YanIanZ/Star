package dev.yanianz.star.quests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

public final class PlayerQuestData {

    private final Set<String> completed = new HashSet<>();
    private final Set<String> active = new HashSet<>();
    private final Map<String, Map<String, Integer>> progress = new HashMap<>();

    public void start(@Nonnull String questName) {
        active.add(questName);
        progress.put(questName, new HashMap<>());
    }

    public void complete(@Nonnull String questName) {
        active.remove(questName);
        completed.add(questName);
    }

    public boolean isCompleted(@Nonnull String questName) {
        return completed.contains(questName);
    }

    public boolean isActive(@Nonnull String questName) {
        return active.contains(questName);
    }

    public int getProgress(@Nonnull String questName, @Nonnull String objective) {
        return progress.getOrDefault(questName, Map.of()).getOrDefault(objective, 0);
    }

    public void addProgress(@Nonnull String questName, @Nonnull String objective, int amount) {
        progress.computeIfAbsent(questName, k -> new HashMap<>()).merge(objective, amount, Integer::sum);
    }

    @Nonnull
    public Set<String> getCompleted() {
        return completed;
    }

    @Nonnull
    public Set<String> getActive() {
        return active;
    }
}

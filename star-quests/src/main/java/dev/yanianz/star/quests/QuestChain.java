package dev.yanianz.star.quests;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

public final class QuestChain {

    private final List<String> questNames;
    private final boolean sequential;

    public QuestChain(@Nonnull List<String> questNames, boolean sequential) {
        this.questNames = questNames;
        this.sequential = sequential;
    }

    @Nonnull
    public static QuestChain sequential(@Nonnull String... names) {
        return new QuestChain(List.of(names), true);
    }

    @Nonnull
    public static QuestChain parallel(@Nonnull String... names) {
        return new QuestChain(List.of(names), false);
    }

    @Nonnull
    public List<String> getQuestNames() {
        return questNames;
    }

    public boolean isSequential() {
        return sequential;
    }

    @Nonnull
    public Optional<String> getNext(@Nonnull String current) {
        if (!sequential) {
            return Optional.empty();
        }
        int idx = questNames.indexOf(current);
        return idx >= 0 && idx < questNames.size() - 1
            ? Optional.of(questNames.get(idx + 1))
            : Optional.empty();
    }
}

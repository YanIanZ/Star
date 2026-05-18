package dev.yanianz.star.quests;

import javax.annotation.Nonnull;

public record QuestObjective(@Nonnull ObjectiveType type, @Nonnull String target, int required) {

    public boolean isComplete(int progress) {
        return progress >= required;
    }
}

package dev.yanianz.star.quests;

import javax.annotation.Nonnull;

public record QuestReward(@Nonnull RewardType type, @Nonnull String value, int amount) {

    @Nonnull
    public static QuestReward of(@Nonnull RewardType type, @Nonnull String value, int amount) {
        return new QuestReward(type, value, amount);
    }

    @Nonnull
    public static QuestReward xp(int amount) {
        return new QuestReward(RewardType.XP, "", amount);
    }

    @Nonnull
    public static QuestReward money(int amount) {
        return new QuestReward(RewardType.MONEY, "", amount);
    }

    @Nonnull
    public static QuestReward item(@Nonnull String material, int amount) {
        return new QuestReward(RewardType.ITEM, material, amount);
    }

    @Nonnull
    public static QuestReward command(@Nonnull String cmd) {
        return new QuestReward(RewardType.COMMAND, cmd, 1);
    }
}

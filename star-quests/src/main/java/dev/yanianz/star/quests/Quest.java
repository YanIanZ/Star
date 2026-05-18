package dev.yanianz.star.quests;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Quest {

    private final String name;
    private final String description;
    private final List<QuestObjective> objectives;
    private final List<QuestReward> rewards;
    private final QuestChain chain;

    Quest(String name, String description, List<QuestObjective> objectives, List<QuestReward> rewards, QuestChain chain) {
        this.name = name;
        this.description = description;
        this.objectives = objectives;
        this.rewards = rewards;
        this.chain = chain;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDescription() {
        return description != null ? description : "";
    }

    @Nonnull
    public List<QuestObjective> getObjectives() {
        return objectives;
    }

    @Nonnull
    public List<QuestReward> getRewards() {
        return rewards;
    }

    @Nullable
    public QuestChain getChain() {
        return chain;
    }

    public boolean isComplete(@Nonnull PlayerQuestData data) {
        return objectives.stream().allMatch(obj ->
            obj.isComplete(data.getProgress(name, obj.type().name() + ":" + obj.target()))
        );
    }

    @Nonnull
    public static Builder builder(@Nonnull String name) {
        return new Builder(name);
    }

    public static final class Builder {

        private final String name;
        private String description;
        private final List<QuestObjective> objectives = new ArrayList<>();
        private final List<QuestReward> rewards = new ArrayList<>();
        private QuestChain chain;

        Builder(String name) {
            this.name = name;
        }

        @Nonnull
        public Builder description(@Nonnull String d) {
            this.description = d;
            return this;
        }

        @Nonnull
        public Builder objective(@Nonnull ObjectiveType type, @Nonnull String target, int required) {
            objectives.add(new QuestObjective(type, target, required));
            return this;
        }

        @Nonnull
        public Builder reward(@Nonnull QuestReward r) {
            rewards.add(r);
            return this;
        }

        @Nonnull
        public Builder reward(@Nonnull RewardType type, @Nonnull String value, int amount) {
            rewards.add(new QuestReward(type, value, amount));
            return this;
        }

        @Nonnull
        public Builder chain(@Nonnull QuestChain c) {
            this.chain = c;
            return this;
        }

        @Nonnull
        public Quest build() {
            return new Quest(name, description, objectives, rewards, chain);
        }
    }
}

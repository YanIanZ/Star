package dev.yanianz.star.quests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Quests")
class TestQuests {

    @Test
    @DisplayName("Quest builder creates objectives")
    void questBuilder() {
        Quest q = Quest.builder("TestQuest")
            .description("Kill 5 zombies")
            .objective(ObjectiveType.KILL, "ZOMBIE", 5)
            .reward(RewardType.XP, "", 100)
            .build();
        assertEquals("TestQuest", q.getName());
        assertEquals(1, q.getObjectives().size());
        assertEquals(1, q.getRewards().size());
    }

    @Test
    @DisplayName("QuestChain sequential gets next")
    void questChain() {
        QuestChain chain = QuestChain.sequential("A", "B", "C");
        assertTrue(chain.isSequential());
        assertEquals("B", chain.getNext("A").orElseThrow());
        assertEquals("C", chain.getNext("B").orElseThrow());
        assertTrue(chain.getNext("C").isEmpty());
    }

    @Test
    @DisplayName("PlayerQuestData tracks progress")
    void playerData() {
        PlayerQuestData data = new PlayerQuestData();
        data.start("Q1");
        assertTrue(data.isActive("Q1"));
        data.addProgress("Q1", "KILL:ZOMBIE", 3);
        assertEquals(3, data.getProgress("Q1", "KILL:ZOMBIE"));
        data.complete("Q1");
        assertTrue(data.isCompleted("Q1"));
        assertFalse(data.isActive("Q1"));
    }

    @Test
    @DisplayName("QuestObjective completion check")
    void objectiveComplete() {
        QuestObjective obj = new QuestObjective(ObjectiveType.KILL, "ZOMBIE", 5);
        assertTrue(obj.isComplete(5));
        assertFalse(obj.isComplete(3));
    }

    @Test
    @DisplayName("QuestReward factory methods")
    void rewardFactory() {
        assertEquals(RewardType.XP, QuestReward.xp(100).type());
        assertEquals(100, QuestReward.xp(100).amount());
        assertEquals(RewardType.ITEM, QuestReward.item("DIAMOND", 3).type());
    }

    @Test
    @DisplayName("QuestManager assigns and completes")
    void questManager() {
        Plugin plugin = mock(Plugin.class);
        Server server = mock(Server.class);
        when(server.getLogger()).thenReturn(Logger.getGlobal());
        when(plugin.getServer()).thenReturn(server);
        QuestManager mgr = new QuestManager(plugin);
        Quest q = Quest.builder("Q")
            .objective(ObjectiveType.KILL, "ZOMBIE", 1)
            .reward(RewardType.XP, "", 10)
            .build();
        mgr.register(q);
        assertTrue(mgr.get("Q").isPresent());
    }
}

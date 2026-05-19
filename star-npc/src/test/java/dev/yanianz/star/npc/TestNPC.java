package dev.yanianz.star.npc;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

@DisplayName("NPC")
class TestNPC {
    @Test
    @DisplayName("NPCProfile creates identity")
    void profile() {
        NPCProfile profile = NPCProfile.of("Steve", "base64data");
        assertEquals("Steve", profile.name());
        assertEquals("base64data", profile.skin());
        assertNotNull(profile.uuid());
    }

    @Test
    @DisplayName("NPC creates with profile")
    void npcCreate() {
        NPCProfile profile = NPCProfile.of("Test", "skin");
        NPC npc = new NPC("id1", profile, null);
        assertEquals("id1", npc.getId());
        assertEquals(profile, npc.getProfile());
        assertNull(npc.getEntity());
    }

    @Test
    @DisplayName("NPC behaviours add/remove")
    void npcBehaviours() {
        NPC npc = new NPC("id2", NPCProfile.of("Bot", "s"), null);
        dev.yanianz.star.npc.behaviours.LookAtPlayerBehaviour look = new dev.yanianz.star.npc.behaviours.LookAtPlayerBehaviour(10);
        npc.addBehaviour(look);
        assertEquals(1, npc.getBehaviours().size());
        npc.removeBehaviour(dev.yanianz.star.npc.behaviours.LookAtPlayerBehaviour.class);
        assertEquals(0, npc.getBehaviours().size());
    }

    @Test
    @DisplayName("NPCDialogue returns text")
    void dialogue() {
        NPCDialogue dlg = new NPCDialogue(net.kyori.adventure.text.Component.text("Hello!"));
        assertEquals("Hello!", net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(dlg.text()));
    }
}

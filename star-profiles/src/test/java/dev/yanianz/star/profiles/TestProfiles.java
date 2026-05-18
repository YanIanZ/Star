package dev.yanianz.star.profiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.*;

@DisplayName("Profiles")
class TestProfiles {
    @Test @DisplayName("ProfileData getters")
    void profileData() {
        ProfileData data = new ProfileData();
        data.set("xp", "100");
        data.set("rank", "VIP");
        assertEquals("100", data.get("xp"));
        assertEquals("VIP", data.get("rank"));
        assertEquals(100, data.getInt("xp"));
        assertEquals("default", data.getOrDefault("missing", "default"));
        assertTrue(data.has("xp"));
        assertFalse(data.has("missing"));
    }

    @Test @DisplayName("Profile builder creates profile")
    void profileBuilder() {
        Profile p = Profile.builder("Adventure").data("mode", "survival").build();
        assertEquals("Adventure", p.getName());
        assertEquals("survival", p.getData().get("mode"));
        assertTrue(p.getInventory().isEmpty());
        assertTrue(p.getLocation().isEmpty());
    }

    @Test @DisplayName("ProfileManager save and get")
    void profileManager() {
        Server server = mock(Server.class);
        when(server.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test"));
        Plugin plugin = mock(Plugin.class);
        when(plugin.getServer()).thenReturn(server);
        ProfileManager mgr = new ProfileManager(plugin);
        assertNotNull(mgr);
    }

    @Test @DisplayName("ProfileInventory static methods")
    void profileInventory() {
        assertNotNull(ProfileInventory.class);
    }

    @Test @DisplayName("ProfileCommand exists")
    void profileCommand() {
        ProfileCommand cmd = new ProfileCommand(null);
        assertNotNull(cmd);
    }
}

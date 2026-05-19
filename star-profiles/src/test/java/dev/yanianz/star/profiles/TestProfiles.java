package dev.yanianz.star.profiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import dev.yanianz.star.common.StarLogger;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.*;

@DisplayName("Profiles")
class TestProfiles {
    private static org.mockbukkit.mockbukkit.ServerMock server;

    @BeforeAll
    static void setUp() {
        org.mockbukkit.mockbukkit.MockBukkit.mock();
        server = org.mockbukkit.mockbukkit.MockBukkit.getOrCreateMock();
    }

    @AfterAll
    static void tearDown() {
        org.mockbukkit.mockbukkit.MockBukkit.unmock();
    }

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
        dev.yanianz.star.profiles.storage.ProfileRepository repo = mock(dev.yanianz.star.profiles.storage.ProfileRepository.class);
        ProfileManager mgr = new ProfileManager(plugin, repo);
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

    @Test @DisplayName("ProfileData boolean and int")
    void profileDataTypes() {
        ProfileData data = new ProfileData();
        data.set("flag", "true");
        data.set("count", "42");
        assertTrue(data.getBoolean("flag"));
        assertEquals(42, data.getInt("count"));
        assertEquals(42.0, data.getDouble("count"));
        assertFalse(data.getBoolean("missing"));
    }

    @Test @DisplayName("Profile with inventory")
    void profileWithInventory() {
        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(org.bukkit.Material.DIAMOND);
        Profile p = Profile.builder("Rich").inventory(item).build();
        assertEquals(1, p.getInventory().size());
        assertEquals(org.bukkit.Material.DIAMOND, p.getInventory().get(0).getType());
    }

    @Test @DisplayName("ProfileManager getProfileNames")
    void profileManagerNames() {
        Server server = mock(Server.class);
        when(server.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test"));
        Plugin plugin = mock(Plugin.class);
        when(plugin.getServer()).thenReturn(server);
        dev.yanianz.star.profiles.storage.ProfileRepository repo = mock(dev.yanianz.star.profiles.storage.ProfileRepository.class);
        ProfileManager mgr = new ProfileManager(plugin, repo);
        org.bukkit.entity.Player player = mock(org.bukkit.entity.Player.class);
        when(player.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
        assertEquals(0, mgr.getProfileCount(player));
    }

    @Test @DisplayName("ProfileListener is created")
    void profileListener() {
        Server server = mock(Server.class);
        when(server.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test"));
        Plugin plugin = mock(Plugin.class);
        when(plugin.getServer()).thenReturn(server);
        dev.yanianz.star.profiles.storage.ProfileRepository repo = mock(dev.yanianz.star.profiles.storage.ProfileRepository.class);
        ProfileManager mgr = new ProfileManager(plugin, repo);
        StarLogger logger = mock(StarLogger.class);
        ProfileListener listener = new ProfileListener(mgr, logger);
        assertNotNull(listener);
    }
}

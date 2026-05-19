package dev.yanianz.star.hooks;
import static org.junit.jupiter.api.Assertions.*;
import dev.yanianz.star.hooks.hooks.LuckPermsHook;
import dev.yanianz.star.hooks.hooks.EssentialsHook;
import dev.yanianz.star.hooks.hooks.DiscordSRVHook;
import dev.yanianz.star.hooks.hooks.PlaceholderAPIHook;
import dev.yanianz.star.hooks.hooks.WorldGuardHook;
import dev.yanianz.star.hooks.hooks.ViaVersionHook;
import org.junit.jupiter.api.*;

@DisplayName("Hooks")
class TestHooks {
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

    @Test @DisplayName("LuckPerms hook name")
    void luckPerms() {
        LuckPermsHook hook = new LuckPermsHook();
        assertEquals("LuckPerms", hook.getName());
        assertFalse(hook.isPresent());
    }

    @Test @DisplayName("Essentials hook name")
    void essentials() {
        EssentialsHook hook = new EssentialsHook();
        assertEquals("Essentials", hook.getName());
    }

    @Test @DisplayName("DiscordSRV hook name")
    void discordSRV() {
        DiscordSRVHook hook = new DiscordSRVHook();
        assertEquals("DiscordSRV", hook.getName());
    }

    @Test @DisplayName("PlaceholderAPI hook name")
    void placeholderAPI() {
        PlaceholderAPIHook hook = new PlaceholderAPIHook();
        assertEquals("PlaceholderAPI", hook.getName());
    }

    @Test @DisplayName("WorldGuard hook defaults")
    void worldGuard() {
        WorldGuardHook hook = new WorldGuardHook();
        assertTrue(hook.canBuild(null, null));
    }

    @Test @DisplayName("HookManager register and detect")
    void hookManager() {
        HookManager mgr = new HookManager(org.mockbukkit.mockbukkit.MockBukkit.createMockPlugin("StarHooks"));
        mgr.register(new LuckPermsHook());
        mgr.register(new DiscordSRVHook());
        mgr.detectAll();
        assertEquals(2, mgr.getAll().size());
        assertNotNull(mgr.get("LuckPerms"));
    }

    @Test @DisplayName("ViaVersion hook name")
    void viaVersion() {
        ViaVersionHook hook = new ViaVersionHook();
        assertEquals("ViaVersion", hook.getName());
        assertEquals(-1, hook.getProtocolVersion(null));
    }

    @Test @DisplayName("HookManager enable check")
    void hookManagerEnabled() {
        HookManager mgr = new HookManager(org.mockbukkit.mockbukkit.MockBukkit.createMockPlugin("StarHooks"));
        assertFalse(mgr.isEnabled("nonexistent"));
    }
}

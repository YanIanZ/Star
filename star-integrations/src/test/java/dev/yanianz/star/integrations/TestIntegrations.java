package dev.yanianz.star.integrations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Integrations")
class TestIntegrations {

    @Test
    @DisplayName("PlaceholderExpansion registers providers")
    void expansion() {
        PlaceholderExpansion exp = new PlaceholderExpansion("test", "author", "1.0");
        exp.register("balance", (p, params) -> "100");
        assertEquals("100", exp.onRequest(null, "balance"));
        assertNull(exp.onRequest(null, "unknown"));
        assertTrue(exp.getPlaceholders().contains("balance"));
    }

    @Test
    @DisplayName("PlaceholderRegistry registers expansions")
    void registry() {
        PlaceholderRegistry reg = new PlaceholderRegistry(null);
        reg.register(new PlaceholderExpansion("test", "author", "1.0"));
        assertEquals(1, reg.getExpansions().size());
    }

    @Test
    @DisplayName("ConfigMigrator adds migrations")
    void configMigrator() throws Exception {
        File file = new File("build/tmp/test-migrator.yml");
        file.getParentFile().mkdirs();
        file.delete();
        file.deleteOnExit();

        ConfigMigrator migrator = new ConfigMigrator(null, file);
        migrator.addVersion(1, config -> config.set("new-key", "val"));
        assertDoesNotThrow(migrator::migrate);
    }

    @Test
    @DisplayName("ConfigVersioner get/set")
    void configVersioner() throws Exception {
        File file = File.createTempFile("config", ".yml");
        file.deleteOnExit();
        assertEquals(0, ConfigVersioner.getVersion(file));
        ConfigVersioner.setVersion(file, 3);
        assertEquals(3, ConfigVersioner.getVersion(file));
    }

    @Test
    @DisplayName("PlaceholderExpansion metadata")
    void expansionMeta() {
        PlaceholderExpansion exp = new PlaceholderExpansion("myplugin", "Me", "2.0");
        assertEquals("myplugin", exp.getIdentifier());
        assertEquals("Me", exp.getAuthor());
        assertEquals("2.0", exp.getVersion());
    }
}

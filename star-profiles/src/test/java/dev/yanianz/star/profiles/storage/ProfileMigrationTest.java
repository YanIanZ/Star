package dev.yanianz.star.profiles.storage;

import dev.yanianz.star.common.StarLogger;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ProfileMigration")
class ProfileMigrationTest {
    private static ServerMock server;

    @BeforeAll
    static void setUp() {
        MockBukkit.mock();
        server = MockBukkit.getOrCreateMock();
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    @Test @DisplayName("Migration returns empty list when no YAML files exist")
    void noFilesToMigrate() throws Exception {
        File tempDir = Files.createTempDirectory("star-profiles-migration-test").toFile();
        tempDir.deleteOnExit();
        MongoProfileRepository repo = mock(MongoProfileRepository.class);
        StarLogger logger = mock(StarLogger.class);
        ProfileMigration migration = new ProfileMigration(tempDir, repo, "test", logger);
        java.util.List<dev.yanianz.star.profiles.Profile> result = migration.migrate(UUID.randomUUID()).get();
        assertTrue(result.isEmpty());
    }
}

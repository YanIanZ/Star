package dev.yanianz.star.profiles.storage;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;
import dev.yanianz.star.common.StarLogger;
import dev.yanianz.star.profiles.Profile;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("MongoProfileRepository")
class MongoProfileRepositoryTest {
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

    @Test
    @DisplayName("Has no profiles for unknown player")
    void noProfiles() {
        MongoDatabase db = mock(MongoDatabase.class);
        com.mongodb.client.ListCollectionNamesIterable namesIterable =
            mock(com.mongodb.client.ListCollectionNamesIterable.class);
        when(db.listCollectionNames()).thenReturn(namesIterable);
        MongoCursor<String> cursor = mock(MongoCursor.class);
        when(cursor.hasNext()).thenReturn(false);
        when(namesIterable.iterator()).thenReturn(cursor);
        StarLogger logger = mock(StarLogger.class);
        MongoProfileRepository repo = new MongoProfileRepository(db, "test", logger);
        assertFalse(repo.hasProfiles(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Load returns empty for missing profile")
    @SuppressWarnings("unchecked")
    void loadMissing() throws Exception {
        MongoDatabase db = mock(MongoDatabase.class);
        MongoCollection<Document> coll = mock(MongoCollection.class);
        when(db.getCollection(anyString())).thenReturn(coll);
        FindIterable<Document> findIterable = mock(FindIterable.class);
        when(coll.find(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(null);
        when(coll.createIndex(any(Bson.class), any(IndexOptions.class))).thenReturn("idx");
        StarLogger logger = mock(StarLogger.class);
        MongoProfileRepository repo = new MongoProfileRepository(db, "test", logger);
        Optional<Profile> result = repo.load(UUID.randomUUID(), "default").get();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Save and load roundtrip")
    @SuppressWarnings("unchecked")
    void saveAndLoad() throws Exception {
        MongoDatabase db = mock(MongoDatabase.class);
        MongoCollection<Document> coll = mock(MongoCollection.class);
        when(db.getCollection(anyString())).thenReturn(coll);
        when(coll.createIndex(any(Bson.class), any(IndexOptions.class))).thenReturn("idx");
        StarLogger logger = mock(StarLogger.class);
        MongoProfileRepository repo = new MongoProfileRepository(db, "test", logger);
        Profile profile = Profile.builder("default")
            .data("xp", "100")
            .serverId("test")
            .createdAt(12345L)
            .build();
        UUID uuid = UUID.randomUUID();

        repo.save(uuid, profile).get();
        verify(coll).replaceOne(any(Bson.class), any(Document.class), any(ReplaceOptions.class));
    }
}

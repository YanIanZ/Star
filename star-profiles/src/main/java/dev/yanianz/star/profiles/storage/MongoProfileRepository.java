package dev.yanianz.star.profiles.storage;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;
import dev.yanianz.star.common.StarLogger;
import dev.yanianz.star.profiles.Profile;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class MongoProfileRepository implements ProfileRepository {
    private final MongoDatabase database;
    private final String serverId;
    private final StarLogger logger;
    private static final String COLLECTION_PREFIX = "profiles_";
    private final Set<UUID> indexedCollections = ConcurrentHashMap.newKeySet();

    public MongoProfileRepository(@Nonnull MongoDatabase database, @Nonnull String serverId, @Nonnull StarLogger logger) {
        this.database = database;
        this.serverId = serverId;
        this.logger = logger;
    }

    @Nonnull
    private String collectionName(@Nonnull UUID playerUuid) {
        return COLLECTION_PREFIX + playerUuid.toString();
    }

    @Nonnull
    private MongoCollection<Document> collection(@Nonnull UUID playerUuid) {
        MongoCollection<Document> coll = database.getCollection(collectionName(playerUuid));
        if (indexedCollections.add(playerUuid)) {
            coll.createIndex(new Document("serverId", 1), new IndexOptions().background(true));
            coll.createIndex(new Document("updatedAt", 1), new IndexOptions().background(true));
        }
        return coll;
    }

    @Override
    @Nonnull
    public CompletableFuture<Void> save(@Nonnull UUID playerUuid, @Nonnull Profile profile) {
        return CompletableFuture.runAsync(() -> {
            profile.setUpdatedAt(System.currentTimeMillis());
            Document doc = profileToDocument(profile);
            collection(playerUuid).replaceOne(
                Filters.eq("_id", profile.getName()),
                doc,
                new ReplaceOptions().upsert(true)
            );
        }).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Failed to save profile " + profile.getName() + " for " + playerUuid, ex);
            return null;
        });
    }

    @Override
    @Nonnull
    public CompletableFuture<Optional<Profile>> load(@Nonnull UUID playerUuid, @Nonnull String profileName) {
        return CompletableFuture.<Optional<Profile>>supplyAsync(() -> {
            Document doc = collection(playerUuid).find(Filters.eq("_id", profileName)).first();
            return doc != null ? Optional.of(documentToProfile(doc)) : Optional.empty();
        }).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Failed to load profile " + profileName + " for " + playerUuid, ex);
            return Optional.<Profile>empty();
        });
    }

    @Override
    @Nonnull
    public CompletableFuture<List<Profile>> loadAll(@Nonnull UUID playerUuid) {
        return CompletableFuture.supplyAsync(() ->
            StreamSupport.stream(collection(playerUuid).find().spliterator(), false)
                .map(this::documentToProfile)
                .collect(Collectors.toList())
        ).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Failed to load profiles for " + playerUuid, ex);
            return List.of();
        });
    }

    @Override
    @Nonnull
    public CompletableFuture<Void> delete(@Nonnull UUID playerUuid, @Nonnull String profileName) {
        return CompletableFuture.runAsync(() ->
            collection(playerUuid).deleteOne(Filters.eq("_id", profileName))
        ).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Failed to delete profile " + profileName + " for " + playerUuid, ex);
            return null;
        });
    }

    @Override
    @Nonnull
    public CompletableFuture<Void> deleteAll(@Nonnull UUID playerUuid) {
        return CompletableFuture.runAsync(() ->
            collection(playerUuid).drop()
        ).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Failed to delete profiles for " + playerUuid, ex);
            return null;
        });
    }

    private Document profileToDocument(Profile profile) {
        Document doc = new Document();
        doc.put("_id", profile.getName());
        doc.put("name", profile.getName());
        doc.put("serverId", profile.getServerId().isEmpty() ? serverId : profile.getServerId());
        doc.put("createdAt", profile.getCreatedAt());
        doc.put("updatedAt", profile.getUpdatedAt());

        Document dataDoc = new Document();
        profile.getData().getAll().forEach(dataDoc::put);
        doc.put("data", dataDoc);

        List<String> inventoryB64 = new ArrayList<>();
        for (ItemStack item : profile.getInventory()) {
            inventoryB64.add(item != null ? itemStackToBase64(item) : "");
        }
        doc.put("inventory", inventoryB64);

        profile.getLocation().ifPresent(loc -> {
            Document locDoc = new Document();
            locDoc.put("world", loc.getWorld().getName());
            locDoc.put("x", loc.getX());
            locDoc.put("y", loc.getY());
            locDoc.put("z", loc.getZ());
            locDoc.put("yaw", (double) loc.getYaw());
            locDoc.put("pitch", (double) loc.getPitch());
            doc.put("location", locDoc);
        });

        return doc;
    }

    private Profile documentToProfile(Document doc) {
        Profile.Builder builder = Profile.builder(doc.getString("name"))
            .createdAt(doc.getLong("createdAt"))
            .updatedAt(doc.getLong("updatedAt"))
            .serverId(doc.getString("serverId"));

        Document dataDoc = doc.get("data", Document.class);
        if (dataDoc != null) {
            dataDoc.forEach((k, v) -> builder.data(k, v.toString()));
        }

        List<String> inventoryB64 = doc.getList("inventory", String.class);
        if (inventoryB64 != null) {
            for (String b64 : inventoryB64) {
                if (b64 != null && !b64.isEmpty()) {
                    ItemStack item = itemStackFromBase64(b64);
                    builder.inventory(item != null ? item : (ItemStack) null);
                } else {
                    builder.inventory((ItemStack) null);
                }
            }
        }

        Document locDoc = doc.get("location", Document.class);
        if (locDoc != null) {
            World world = Bukkit.getWorld(locDoc.getString("world"));
            if (world != null) {
                builder.location(new Location(world,
                    locDoc.getDouble("x"),
                    locDoc.getDouble("y"),
                    locDoc.getDouble("z"),
                    ((Double) locDoc.getOrDefault("yaw", 0.0)).floatValue(),
                    ((Double) locDoc.getOrDefault("pitch", 0.0)).floatValue()));
            }
        }

        return builder.build();
    }

    private static String itemStackToBase64(ItemStack item) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos)) {
            boos.writeObject(item);
            boos.flush();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    private static ItemStack itemStackFromBase64(String base64) {
        if (base64 == null) return null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                 BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)) {
                return (ItemStack) bois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public boolean hasProfiles(@Nonnull UUID playerUuid) {
        MongoCollection<Document> coll = database.getCollection(collectionName(playerUuid));
        return coll.countDocuments() > 0;
    }
}

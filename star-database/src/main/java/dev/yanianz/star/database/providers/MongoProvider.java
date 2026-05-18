package dev.yanianz.star.database.providers;

import dev.yanianz.star.database.DatabaseProvider;
import dev.yanianz.star.database.QueryResult;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public final class MongoProvider implements DatabaseProvider {
    private final String connectionString;
    private final String databaseName;
    private MongoClient client;
    private MongoDatabase database;
    private boolean connected;

    public MongoProvider(@Nonnull String host, int port, @Nonnull String database, @Nonnull String user, @Nonnull String password) {
        this.connectionString = "mongodb://" + user + ":" + password + "@" + host + ":" + port + "/" + database;
        this.databaseName = database;
    }

    public MongoProvider(@Nonnull String connectionString, @Nonnull String database) {
        this.connectionString = connectionString;
        this.databaseName = database;
    }

    @Override public void connect() throws SQLException {
        client = MongoClients.create(connectionString);
        database = client.getDatabase(databaseName);
        connected = true;
    }

    @Override public void disconnect() { if (client != null) client.close(); connected = false; }
    @Override public boolean isConnected() { return connected; }

    @Override @Nonnull
    public QueryResult query(@Nonnull String sql, Object... params) throws SQLException {
        String collection = sql.startsWith("find:") ? sql.substring(5).split(" ")[0] : sql;
        MongoCollection<Document> coll = database.getCollection(collection);
        FindIterable<Document> docs = coll.find();
        return new MongoQueryResult(docs.iterator());
    }

    @Override
    public int execute(@Nonnull String sql, Object... params) throws SQLException { return 0; }

    @Override @Nonnull public Connection getConnection() throws SQLException { throw new UnsupportedOperationException("MongoDB uses MongoClient"); }

    public void insert(@Nonnull String collection, @Nonnull Document document) {
        database.getCollection(collection).insertOne(document);
    }

    public void insertMany(@Nonnull String collection, @Nonnull List<Document> documents) {
        database.getCollection(collection).insertMany(documents);
    }

    @Nonnull
    public FindIterable<Document> find(@Nonnull String collection, @Nullable Bson filter) {
        return filter != null ? database.getCollection(collection).find(filter) : database.getCollection(collection).find();
    }

    @Nonnull
    public FindIterable<Document> find(@Nonnull String collection, @Nonnull String key, @Nonnull Object value) {
        return database.getCollection(collection).find(Filters.eq(key, value));
    }

    public long countDocuments(@Nonnull String collection, @Nullable Bson filter) {
        return filter != null ? database.getCollection(collection).countDocuments(filter) : database.getCollection(collection).countDocuments();
    }

    public void update(@Nonnull String collection, @Nonnull Bson filter, @Nonnull Bson update) {
        database.getCollection(collection).updateOne(filter, update);
    }

    public void updateMany(@Nonnull String collection, @Nonnull Bson filter, @Nonnull Bson update) {
        database.getCollection(collection).updateMany(filter, update);
    }

    public void delete(@Nonnull String collection, @Nonnull Bson filter) {
        database.getCollection(collection).deleteOne(filter);
    }

    public void deleteMany(@Nonnull String collection, @Nonnull Bson filter) {
        database.getCollection(collection).deleteMany(filter);
    }

    @Nonnull public MongoDatabase getDatabase() { return database; }

    private static final class MongoQueryResult implements QueryResult {
        private final Iterator<Document> iterator;
        @Nullable private Document current;

        MongoQueryResult(Iterator<Document> iterator) { this.iterator = iterator; }

        @Override public boolean next() { if (iterator.hasNext()) { current = iterator.next(); return true; } return false; }
        @Override @Nullable public String getString(@Nonnull String col) { return current != null ? current.getString(col) : null; }
        @Override @Nullable public Integer getInt(@Nonnull String col) { return current != null ? current.getInteger(col) : null; }
        @Override @Nullable public Double getDouble(@Nonnull String col) { return current != null ? current.getDouble(col) : null; }
        @Override @Nullable public Long getLong(@Nonnull String col) { return current != null ? current.getLong(col) : null; }
        @Override @Nullable public Boolean getBoolean(@Nonnull String col) { return current != null ? current.getBoolean(col) : null; }
        @Override public int getColumnCount() { return current != null ? current.keySet().size() : 0; }
        @Override public void close() {}
    }
}

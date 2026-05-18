package dev.yanianz.star.database;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Database")
class TestDatabase {
    @Test
    @DisplayName("QueryBuilder builds SELECT")
    void queryBuilderSelect() {
        String sql = QueryBuilder.select("name", "level")
                .from("players")
                .where("level > ?", 10)
                .orderBy("level DESC")
                .limit(5)
                .build();
        assertEquals("SELECT name, level FROM players WHERE level > ? ORDER BY level DESC LIMIT 5", sql);
    }

    @Test
    @DisplayName("QueryBuilder multiple conditions")
    void queryBuilderMultiWhere() {
        String sql = QueryBuilder.select("*")
                .from("items")
                .where("type = ?", "sword")
                .where("damage > ?", 5)
                .build();
        assertEquals("SELECT * FROM items WHERE type = ? AND damage > ?", sql);
    }

    @Test
    @DisplayName("QueryBuilder offset")
    void queryBuilderOffset() {
        String sql = QueryBuilder.select("id")
                .from("data")
                .limit(10)
                .offset(20)
                .build();
        assertEquals("SELECT id FROM data LIMIT 10 OFFSET 20", sql);
    }

    @Test
    @DisplayName("ConnectionPool API exists")
    void connectionPoolApi() {
        ConnectionPool pool = new ConnectionPool("jdbc:sqlite:test.db", "", "");
        assertNotNull(pool);
    }

    @Test
    @DisplayName("SQLiteProvider and MySQLProvider constructors")
    void providerConstructors() {
        assertDoesNotThrow(() -> new dev.yanianz.star.database.providers.SQLiteProvider(new java.io.File("test.db")));
        assertDoesNotThrow(() -> new dev.yanianz.star.database.providers.MySQLProvider("localhost", 3306, "test", "root", ""));
    }
}

package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

@DisplayName("World Utils")
class TestWorldUtils {

    private ServerMock server;
    private World world;
    private Location loc1, loc2;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        world = new WorldMock();
        loc1 = new Location(world, 0, 64, 0);
        loc2 = new Location(world, 10, 70, 10);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test @DisplayName("CuboidRegion dimensions")
    void cuboidRegion() {
        CuboidRegion region = new CuboidRegion(loc1, loc2);
        assertEquals(11, region.getWidth());
        assertEquals(7, region.getHeight());
        assertEquals(11, region.getLength());
        assertEquals(11 * 7 * 11, region.getVolume());
        assertEquals(0, region.getMinX());
        assertEquals(64, region.getMinY());
        assertEquals(0, region.getMinZ());
        assertEquals(10, region.getMaxX());
        assertEquals(70, region.getMaxY());
        assertEquals(10, region.getMaxZ());
        assertTrue(region.contains(5, 65, 5));
        assertFalse(region.contains(100, 65, 5));
    }

    @Test @DisplayName("LocationUtils distance")
    void distance() {
        double dist = LocationUtils.distance2D(new Location(world, 0, 0, 0), new Location(world, 3, 0, 4));
        assertEquals(5.0, dist);
    }

    @Test @DisplayName("LocationUtils midpoint")
    void midpoint() {
        Location mid = LocationUtils.midpoint(new Location(world, 0, 0, 0), new Location(world, 10, 10, 10));
        assertEquals(5.0, mid.getX());
        assertEquals(5.0, mid.getY());
        assertEquals(5.0, mid.getZ());
    }

    @Test @DisplayName("LocationUtils centerBlock")
    void centerBlock() {
        Location centered = LocationUtils.centerBlock(new Location(world, 3, 64, 7));
        assertEquals(3.5, centered.getX());
        assertEquals(64.0, centered.getY());
        assertEquals(7.5, centered.getZ());
    }

    @Test @DisplayName("LocationUtils isSameBlock")
    void isSameBlock() {
        assertTrue(LocationUtils.isSameBlock(new Location(world, 3.1, 64.9, 7.2), new Location(world, 3.8, 64.1, 7.9)));
        assertFalse(LocationUtils.isSameBlock(new Location(world, 3, 64, 7), new Location(world, 4, 64, 7)));
    }

    @Test @DisplayName("ChunkCoord conversion")
    void chunkCoord() {
        ChunkCoord cc = ChunkCoord.fromLocation(new Location(world, 47, 64, -3));
        assertEquals(2, cc.x());
        assertEquals(-1, cc.z());
        assertEquals(world.getName(), cc.worldName());
        assertEquals(32, cc.getBlockX());
        assertEquals(-16, cc.getBlockZ());

        ChunkCoord offset = cc.offset(1, -2);
        assertEquals(3, offset.x());
        assertEquals(-3, offset.z());
    }

    @Test @DisplayName("ChunkCoord key")
    void chunkKey() {
        long key = ChunkCoord.key(5, -3);
        assertNotNull(key);
        ChunkCoord cc = new ChunkCoord("world", 5, -3);
        assertEquals(key, cc.toKey());
    }

    @Test @DisplayName("CuboidRegion contains self corners")
    void containsCorners() {
        CuboidRegion region = new CuboidRegion(loc1, loc2);
        assertTrue(region.contains(region.getMin()));
        assertTrue(region.contains(region.getMax()));
        assertTrue(region.contains(new Location(world, 5, 67, 5)));
    }

    @Test @DisplayName("CuboidRegion center")
    void center() {
        CuboidRegion region = new CuboidRegion(new Location(world, 0, 0, 0), new Location(world, 10, 10, 10));
        Location center = region.getCenter();
        assertEquals(5.5, center.getX(), 0.1);
        assertEquals(5.0, center.getY(), 0.1);
        assertEquals(5.5, center.getZ(), 0.1);
    }
}

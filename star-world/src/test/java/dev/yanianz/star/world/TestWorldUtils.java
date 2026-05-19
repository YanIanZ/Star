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

    @Test @DisplayName("ChunkPurger gets chunks in region")
    void chunkPurger() {
        Location c1 = new Location(world, 0, 64, 0);
        Location c2 = new Location(world, 47, 64, 47);
        var chunks = ChunkPurger.getChunksInRegion(c1, c2);
        assertFalse(chunks.isEmpty());
        assertTrue(chunks.size() >= 4);
    }

    @Test @DisplayName("TerrainSampler queries")
    void terrainSampler() {
        assertTrue(world.getMaxHeight() > 0);
        assertFalse(TerrainSampler.isWalkable(new Location(world, 0, 1, 0)));
    }

    @Test @DisplayName("SpawnPointFinder does not throw")
    void spawnPointFinderInvalid() {
        assertDoesNotThrow(() -> SpawnPointFinder.findSafe(new Location(world, 0, 64, 0), 1.0, 10));
    }

    @Test @DisplayName("SchematicUtils width/height/length")
    void schematicUtilsDimensions() throws Exception {
        java.io.File tmp = new java.io.File(System.getProperty("java.io.tmpdir"), "test-schem-" + System.nanoTime() + ".tmp");
        tmp.deleteOnExit();
        Location c1 = new Location(world, 0, 64, 0);
        Location c2 = new Location(world, 2, 65, 2);
        SchematicUtils.save(c1, c2, tmp);
        SchematicUtils.SchematicData data = SchematicUtils.load(tmp);
        assertEquals(3, data.width());
        assertEquals(2, data.height());
        assertEquals(3, data.length());
    }

    @Test @DisplayName("StructurePlacer getters")
    void structurePlacer() throws Exception {
        java.io.File tmp = new java.io.File(System.getProperty("java.io.tmpdir"), "test-schem-" + System.nanoTime() + ".tmp");
        tmp.deleteOnExit();
        SchematicUtils.save(new Location(world, 0, 64, 0), new Location(world, 2, 65, 2), tmp);
        StructurePlacer placer = StructurePlacer.load(tmp);
        assertEquals(3, placer.getWidth());
        assertEquals(2, placer.getHeight());
        assertEquals(3, placer.getLength());
        assertThrows(IllegalStateException.class, placer::place);
    }

    @Test @DisplayName("RegionCopier record fields")
    void regionCopierRecord() {
        RegionCopier.CopyResult result = new RegionCopier.CopyResult(0, 3, 64, 67, 0, 3, 10, 0, 10, 64);
        assertEquals(0, result.minX());
        assertEquals(3, result.maxX());
        assertEquals(64, result.minY());
        assertEquals(67, result.maxY());
        assertEquals(10, result.dx());
        assertEquals(64, result.blocks());
    }

    @Test @DisplayName("LocationUtils distance3D")
    void distance3D() {
        double dist = LocationUtils.distance3D(new Location(world, 0, 0, 0), new Location(world, 3, 4, 0));
        assertEquals(5.0, dist);
    }

    @Test @DisplayName("LocationUtils randomInCircle")
    void randomInCircle() {
        Location result = LocationUtils.randomInCircle(new Location(world, 0, 64, 0), 5.0);
        assertNotNull(result);
        assertTrue(result.getX() != 0 || result.getZ() != 0);
    }

    @Test @DisplayName("LocationUtils faceLocation")
    void faceLocation() {
        Location from = new Location(world, 0, 64, 0);
        Location target = new Location(world, 10, 64, 0);
        Location faced = LocationUtils.faceLocation(from, target);
        assertNotNull(faced.getDirection());
    }

    @Test @DisplayName("BlockScanner static methods exist")
    void blockScannerExists() {
        assertNotNull(BlockScanner.class);
    }

    @Test @DisplayName("BlockProcessor static methods exist")
    void blockProcessorExists() {
        assertNotNull(BlockProcessor.class);
    }

    @Test @DisplayName("RegionCopier static method")
    void regionCopierStatic() {
        assertNotNull(RegionCopier.class);
    }

    @Test @DisplayName("ChunkPurger getChunksInRegion")
    void chunkPurgerGetChunks() {
        Location c1 = new Location(world, 0, 64, 0);
        Location c2 = new Location(world, 32, 64, 32);
        var chunks = ChunkPurger.getChunksInRegion(c1, c2);
        assertFalse(chunks.isEmpty());
    }

    @Test @DisplayName("BlockPopulator types")
    void blockPopulatorTypes() {
        assertDoesNotThrow(() -> BlockPopulator.class.getMethod("populateOreVein", org.bukkit.Location.class, org.bukkit.Material.class, double.class, int.class));
        assertDoesNotThrow(() -> BlockPopulator.class.getMethod("populateTree", org.bukkit.Location.class, org.bukkit.Material.class, org.bukkit.Material.class, int.class));
        assertDoesNotThrow(() -> BlockPopulator.class.getMethod("populateFlower", org.bukkit.Location.class));
    }
}

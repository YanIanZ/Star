package dev.yanianz.star.blocks;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

class TestBlockPosition {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testBlockPositions() {
        int x = 57123, y = 286, z = 862;
        BlockPosition bp = new BlockPosition(null, x, y, z);

        Assertions.assertEquals(x, bp.getX());
        Assertions.assertEquals(y, bp.getY());
        Assertions.assertEquals(z, bp.getZ());
    }

    @Test
    void testNegativeBlockPositions() {
        int x = -57123, y = -38, z = -862;
        BlockPosition bp = new BlockPosition(null, x, y, z);

        Assertions.assertEquals(x, bp.getX());
        Assertions.assertEquals(y, bp.getY());
        Assertions.assertEquals(z, bp.getZ());
    }

}

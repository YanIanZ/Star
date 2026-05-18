package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Utility for saving and loading region data in a simple binary schematic format.
 * Format: [width(short)][height(short)][length(short)][materials...][blockData...]
 */
public final class SchematicUtils {
    private SchematicUtils() {}

    public record SchematicData(short width, short height, short length, Material[] blocks, BlockData[] blockData) {}

    public static void save(@Nonnull Location pos1, @Nonnull Location pos2, @Nonnull File file) throws IOException {
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        short w = (short) (maxX - minX + 1), h = (short) (maxY - minY + 1), l = (short) (maxZ - minZ + 1);
        int total = w * h * l;
        Material[] blocks = new Material[total];
        BlockData[] data = new BlockData[total];
        int i = 0;
        for (int y = minY; y <= maxY; y++)
            for (int z = minZ; z <= maxZ; z++)
                for (int x = minX; x <= maxX; x++) {
                    Block b = pos1.getWorld().getBlockAt(x, y, z);
                    blocks[i] = b.getType();
                    data[i] = b.getBlockData();
                    i++;
                }
        try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
            out.writeShort(w); out.writeShort(h); out.writeShort(l);
            for (Material m : blocks) out.writeUTF(m.name());
            for (BlockData d : data) out.writeUTF(d.getAsString());
        }
    }

    @Nonnull
    public static SchematicData load(@Nonnull File file) throws IOException {
        try (DataInputStream in = new DataInputStream(new GZIPInputStream(new FileInputStream(file)))) {
            short w = in.readShort(), h = in.readShort(), l = in.readShort();
            int total = w * h * l;
            Material[] blocks = new Material[total];
            BlockData[] data = new BlockData[total];
            for (int i = 0; i < total; i++) blocks[i] = Material.valueOf(in.readUTF());
            for (int i = 0; i < total; i++) data[i] = org.bukkit.Bukkit.createBlockData(in.readUTF());
            return new SchematicData(w, h, l, blocks, data);
        }
    }

    public static void paste(@Nonnull SchematicData schem, @Nonnull Location origin) {
        World world = origin.getWorld();
        int bx = origin.getBlockX(), by = origin.getBlockY(), bz = origin.getBlockZ();
        int i = 0;
        for (int y = 0; y < schem.height; y++)
            for (int z = 0; z < schem.length; z++)
                for (int x = 0; x < schem.width; x++) {
                    Block b = world.getBlockAt(bx + x, by + y, bz + z);
                    b.setType(schem.blocks[i]);
                    b.setBlockData(schem.blockData[i]);
                    i++;
                }
    }
}

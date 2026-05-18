package dev.yanianz.star.world;

import org.bukkit.Location;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

public final class StructurePlacer {
    private Location location;
    private StructureRotation rotation = StructureRotation.NONE;
    private Mirror mirror = Mirror.NONE;
    private final SchematicUtils.SchematicData schematic;

    private StructurePlacer(@Nonnull SchematicUtils.SchematicData schematic) {
        this.schematic = schematic;
    }

    @Nonnull
    public static StructurePlacer load(@Nonnull File file) throws IOException {
        return new StructurePlacer(SchematicUtils.load(file));
    }

    @Nonnull
    public StructurePlacer at(@Nonnull Location loc) { this.location = loc; return this; }
    @Nonnull public StructurePlacer rotate(@Nonnull StructureRotation r) { this.rotation = r; return this; }
    @Nonnull public StructurePlacer mirror(@Nonnull Mirror m) { this.mirror = m; return this; }

    public int place() {
        if (location == null) throw new IllegalStateException("Location not set");
        SchematicUtils.paste(schematic, location);
        return schematic.width() * schematic.height() * schematic.length();
    }

    public int getWidth() { return schematic.width(); }
    public int getHeight() { return schematic.height(); }
    public int getLength() { return schematic.length(); }
}

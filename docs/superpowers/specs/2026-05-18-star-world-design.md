# Design: star-world — World Utilities Module

**Date:** 2026-05-18 | **Project:** Star

## 1. Architecture
13 classes covering region operations, structure generation, terrain, chunks, and location utilities.

## 2. Classes

| Class | Purpose |
|-------|---------|
| `BlockScanner` | Scan blocks in box/sphere/chunk with consumer callback |
| `BlockProcessor` | Apply replace/fill/set operations to scanned blocks |
| `RegionCopier` | Copy blocks + block data + tile entities between regions |
| `RegionSelector` | Wand-based region selection (left/right click pos1/pos2) |
| `ChunkPurger` | Chunk load/unload/regen with progress callback |
| `SchematicUtils` | Load/save schematic files (`.schem`/`.schematic`) |
| `StructurePlacer` | Place schematic at location with rotation/mirror |
| `SpawnPointFinder` | Find safe spawn point within radius |
| `TerrainSampler` | Biome, highest block Y, block at height queries |
| `BlockPopulator` | Populate ores, trees, decorations in chunk |
| `CuboidRegion` | Box region data class (min/max point, volume, center) |
| `LocationUtils` | Distance, midpoint, random location, direction helpers |
| `ChunkCoord` | Chunk coordinate conversions and hashing |

## 3. Dependencies
- star-common (StarLogger)
- Paper API (World, Chunk, Block, Location, Material)

## 4. Testing
- Unit tests for LocationUtils, CuboidRegion, ChunkCoord (pure math)
- MockBukkit integration for BlockScanner, TerrainSampler

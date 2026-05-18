# Modernization Pass Implementation Plan

**Goal:** Apply Java 25 records, sealed classes, pattern matching, remove legacy ChatColor, standardize logging/Validate/CopyOf, add record with-methods.

---

### Task 1: Records conversion (4 classes)
**Files:** GuiItem.java → record, NPCProfile.java → record, NPCDialogue.java → record, DamageModifier.java → record

### Task 2: Sealed interfaces (2)
**Files:** Version.java → sealed, HologramAnimation.java → sealed

### Task 3: Pattern matching + switch (8 sites)
**Files:** SemanticVersion.java, SimpleNumericVersion.java, PrefixedVersion.java — instanceof patterns; CommandManager.java — Map lookup

### Task 4: Remove legacy ChatColor (2 files)
**Files:** Config.java, ItemStackUtil.java — ChatColor → Adventure Component

### Task 5: VFX plugin resolution (4 files)
**Files:** TrailEffect, ExplosionEffect, DeathEffect, BlockBreakEffect — Plugin constructor param

### Task 6: StarLogger + Validate + CopyOf + err.println (13 files)
**Files:** ArgumentType, Config, MinecraftRecipe, AbstractPluginUpdater, SlimeWorldManager, + all CopyOf sites + Validate adoption

### Task 7: Record with-methods + PaginatedGui (5 files)
**Files:** TransactionResult, ChunkCoord, CurrencyType, ArgDef, PaginatedGui

### Task 8: Tests + build + release
Run all tests, bump version, commit

---

Execute tasks sequentially. Each task touches 2-8 files. Verify compilation after each.

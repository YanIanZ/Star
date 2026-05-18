# Design: Modernization Pass — Java 25 + Paper API + Consistency

**Date:** 2026-05-19 | **Project:** Star v1.3.0

---

## 1. Java 25 Upgrades

### Records (4 conversions)
| Class | → Record |
|-------|----------|
| `star-gui/.../GuiItem.java` | `record GuiItem(ItemStack item, Consumer<GuiClickEvent> handler)` |
| `star-npc/.../NPCProfile.java` | `record NPCProfile(String name, UUID uuid, String skin)` with static `of()` |
| `star-npc/.../NPCDialogue.java` | `record NPCDialogue(Component text)` |
| `star-combat/.../DamageModifier.java` | `record DamageModifier(String name, double multiplier, DamageType... types)` |

### Sealed Interfaces (2)
- `Version` → `sealed interface Version permits SemanticVersion, SimpleNumericVersion, PrefixedVersion, AbstractNumericVersion`
- `HologramAnimation` → `sealed interface HologramAnimation permits ScrollAnimation, RainbowAnimation, BlinkAnimation`

### Pattern Matching (7 sites)
`SemanticVersion.java`, `SimpleNumericVersion.java`, `PrefixedVersion.java` — replace `instanceof X` then `((X) obj)` with `instanceof X x`.

### Enhanced Switch (1 site)
`CommandManager.java` parameter type resolution: `if/else if` chain → `Map<Class<?>, ArgumentType<?>>` lookup.

---

## 2. Paper API Modernization

### Remove legacy ChatColor (2 files)
- `Config.java:476,496` → `ChatColors.colorToComponent()` + `Bukkit.createInventory(null, size, Component)`
- `ItemStackUtil.java:45,76` → `meta.displayName(ChatColors.legacyToComponent(name))`

### Fix VFX plugin resolution (4 files)
`TrailEffect`, `ExplosionEffect`, `DeathEffect`, `BlockBreakEffect` → accept `Plugin` in constructor, remove `getPlugins()[0]` hack.

---

## 3. Consistency

### StarLogger adoption (6 files)
Replace raw `java.util.logging.Logger` with `StarLogger` in: ArgumentType, Config, MinecraftRecipe, AbstractPluginUpdater, SlimeWorldManager, ProtectionManager.

### Validate.notNull() adoption
Add `Validate.notNull()` calls in all new module constructors where inline null checks exist but don't use the project's Validate utility.

### List/Map.copyOf() (8 sites)
Replace `Collections.unmodifiableList/Map` with `List.copyOf()/Map.copyOf()` in: GuiStateManager, NPC, NPCManager, HologramManager, Hologram, CompositeCommand, CommandContext, EconomyManager.

### Fix System.err.println
`MinecraftRecipe.java:139` → `StarLogger`.

---

## 4. Missing Modern Features

### Record with methods (4 records)
- `TransactionResult.withMessage(String)`
- `ChunkCoord.withX(int)`, `.withZ(int)`
- `CurrencyType.withName(String)`, `.withSymbol(String)`
- `CommandNode.ArgDef.withType(ArgumentType<?>)`

### CommandManager reflection → Map lookup
Replace parameter-type `if/else` chain with `Map<Class<?>, ArgumentType<?>> TYPE_MAP`.

### PaginatedGui fix
Refactor `PaginatedGui` to not pass empty `HashMap` to `Gui` super — use direct inventory manipulation in `renderPage()`.

---

## 5. Testing
- Update existing tests for record API changes (getters → accessors)
- No expected test breakage — records preserve method signatures

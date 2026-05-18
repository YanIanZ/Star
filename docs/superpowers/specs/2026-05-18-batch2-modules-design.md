# Design: Batch 2 Modules — Holograms, Database, Combat, NPC

**Date:** 2026-05-18 | **Project:** Star

---

## 1. star-holograms

### Architecture
```
star-holograms/
  Hologram.java                  # Text lines at location
  HologramLine.java              # Single line (text + item slot)
  HologramManager.java           # CRUD + visibility management
  HologramAnimation.java         # Animation interface
  animations/
    ScrollAnimation.java         # Horizontal text scroll
    RainbowAnimation.java        # Color cycling
    BlinkAnimation.java          # Toggle visibility
    HighlightAnimation.java      # Pulsing brightness
```

### API
```java
HologramManager hm = new HologramManager(plugin);
Hologram holo = hm.create(location, "&eHello", "&bWorld");
holo.insertLine(1, "New line");
holo.setLine(0, "Updated");
holo.removeLine(2);
holo.show(player);
holo.hide(player);
hm.delete(holo);
hm.deleteAll();

// Animation
holo.setAnimation(new RainbowAnimation(20)); // cycle colors every 20 ticks
holo.getAnimation().start();
holo.getAnimation().stop();
```

### Dependencies: star-common, Paper API (armor stands or text displays)

---

## 2. star-database

### Architecture
```
star-database/
  DatabaseProvider.java          # Connect/query/execute/transaction interface
  QueryResult.java               # Row + column results
  QueryBuilder.java              # Fluent SQL builder
  ConnectionPool.java            # HikariCP wrapper
  providers/
    MySQLProvider.java           # MySQL/MariaDB
    SQLiteProvider.java          # SQLite file-based
    MongoProvider.java           # MongoDB (optional backend)
  async/
    AsyncDatabase.java           # Wraps queries in CompletableFuture
```

### API
```java
DatabaseProvider db = MySQLProvider.create("host", 3306, "db", "user", "pass");
db.connect();
db.execute("CREATE TABLE IF NOT EXISTS players (...)");

QueryResult result = db.query("SELECT * FROM players WHERE level > ?", 10);
while (result.next()) { String name = result.getString("name"); }

// QueryBuilder
QueryBuilder.select("name", "level").from("players").where("level > ?", 10).orderBy("level DESC").limit(5);

// Async
AsyncDatabase async = new AsyncDatabase(db);
async.query("SELECT ...").thenAccept(result -> { ... });
```

### Dependencies: star-common, HikariCP (compileOnly), MongoDB driver (optional compileOnly)

---

## 3. star-combat

### Architecture
```
star-combat/
  DamageType.java                # Custom damage type enum + metadata
  DamageCalculator.java          # Base + armor + enchants + potions + custom
  DamageEvent.java               # Pre-damage event wrapper
  DamageModifier.java            # Individual damage modifier
  CombatLog.java                 # Per-player combat state tracker
  CombatTagger.java              # Damage source tagging
```

### API
```java
DamageCalculator calc = new DamageCalculator();
calc.addModifier(new DamageModifier("berserk", 1.5, DamageType.MELEE));
double finalDamage = calc.calculate(baseDamage, attacker, defender, DamageType.MELEE);

CombatLog log = new CombatLog();
log.enter(player);
log.getTaggedBy(player); // who damaged you
log.getRemaining(); // seconds left
log.exit(player);
log.isInCombat(player);
```

### Dependencies: star-common, Paper API

---

## 4. star-npc

### Architecture
```
star-npc/
  NPC.java                       # NPC wrapper (entity + metadata)
  NPCProfile.java                # Skin + name + UUID
  NPCManager.java                # Create/delete/track NPCs
  NPCBehaviour.java              # Behaviour interface
  behaviours/
    LookAtPlayerBehaviour.java   # Head rotation towards nearest player
    WanderBehaviour.java         # Random pathfinding
    FollowBehaviour.java         # Follow target entity
    DialogueBehaviour.java       # Dialogue tree interaction
  NPCDialogue.java               # Dialogue node + responses
```

### API
```java
NPCManager manager = new NPCManager(plugin);
NPC npc = manager.create(location, NPCProfile.of("Steve", skinBase64));
npc.setEquipment(EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD));
npc.setBehaviour(new LookAtPlayerBehaviour(10)); // 10 block range
npc.addBehaviour(new WanderBehaviour(5)); // 5 block wander radius
npc.setInteractHandler((player, clickType) -> {
    player.sendMessage("Hello!");
});
manager.delete(npc);
```

### Dependencies: star-common, star-skins, PacketEvents (compileOnly)

---

## 5. Testing
Each module gets 4-6 tests covering builder APIs, core logic, edge cases.

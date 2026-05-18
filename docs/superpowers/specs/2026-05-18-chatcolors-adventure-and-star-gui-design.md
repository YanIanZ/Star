# Design: ChatColors Adventure Migration + star-gui Framework

**Date:** 2026-05-18
**Author:** rheninxy
**Project:** Star (dev.yanianz:star)

---

## 1. ChatColors → Adventure API Hybrid Migration

### Motivation

Replace the legacy Bukkit `ChatColor` API with Adventure API for modern hex color support while maintaining backward compatibility.

### Scope

- `ChatColors.java` — enhanced with new Adventure-based methods
- `ItemStackUtil.java` — new `Component` overloads
- `Config.java` — new `Component` overloads for inventory titles
- All remaining "dough" brand references → "Star"

### ChatColors.java Design

**Kept (backward compatible):**
- `color(String)` → returns `String` (`&` codes translated via legacy ChatColor)
- `alternating(String, ChatColor...)` → returns `String`

**New methods:**

| Method | Returns | Purpose |
|--------|---------|---------|
| `legacyToComponent(String)` | `Component` | Parse `&` codes into Adventure Component |
| `hex(String hex)` | `TextColor` | Create TextColor from hex (`#FF5500` or `FF5500`) |
| `hexToComponent(String hex)` | `Component` | Empty component with hex text color applied |
| `colorToComponent(String)` | `Component` | Combined: translate `&` codes and return Component |
| `alternating(String, TextColor...)` | `Component` | Alternating colors as Component |
| `gradient(String, TextColor, TextColor)` | `Component` | Gradient text effect across string |

**Hex parsing logic:**
- Accepts both `#FF5500` and `FF5500` formats
- Strips `#` prefix if present
- Validates 6-char hex string, throws `IllegalArgumentException` on invalid input

**Dependencies added to `star-common/build.gradle.kts`:**
```kotlin
compileOnly("net.kyori:adventure-api:4.17.0")
compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
```

### ItemStackUtil.java Changes

New overloads alongside existing `String` versions:
- `setName(ItemStack item, Component name)` — set display name as Component
- `setLore(ItemStack item, List<Component> lore)` — set lore lines as Components

### Config.java Changes

New overloads for inventory creation:
- `getInventory(String path, Component title)` — create inventory with Component title
- `getInventory(String path, Component title, int size)` — create sized inventory with Component title

### Branding: "dough" → "Star"

| File | Change |
|------|--------|
| `README.md` L32, L144 | Update mentions of "Dough" to describe fork relationship neutrally |
| `star-protection/.../HuskClaimsProtectionModule.java` L45, L54, L56, L59, L60 | Rename variable `doughAction` → `starAction`, `dough Interaction` → `Star Interaction` |
| `star-protection/.../HuskTownsProtectionModule.java` L45, L54, L56, L59, L60 | Same as above |
| `star-protection/.../ProtectionManager.java` L57 | Update javadoc: "integrates dough" → "integrates Star" |

---

## 2. star-gui — Full GUI Framework

### Motivation

New module providing a complete fluent GUI framework for Paper plugins. Covers simple menus through advanced stateful UIs.

### Architecture

```
star-gui/
  build.gradle.kts
  src/main/java/dev/yanianz/star/gui/
    Gui.java                 # Main GUI wrapper around Inventory
    GuiBuilder.java          # Fluent builder for Gui construction
    PaginatedGui.java        # Multi-page GUI with content slots
    GuiClickEvent.java       # Click event wrapper (player, slot, click type, etc.)
    GuiItem.java             # ItemStack + click handler tuple
    GuiTemplate.java         # Reusable layout skeleton
    animation/
      GuiAnimation.java      # Animation interface (start/stop/tick)
      SlotAnimation.java     # Per-slot icon cycling animation
      FrameAnimation.java    # Full-inventory frame animation
    state/
      GuiState.java          # Per-player stateful GUI base
      GuiStateManager.java   # Registers/retrieves stateful GUIs per player
```

### Dependencies

- `star-common` — StarLogger, ChatColors
- `star-items` — ItemStackEditor, ItemUtils
- `star-inventories` — InvUtils
- Paper-provided: Adventure API (Component titles)

### Gui — Core Class

Wraps a Bukkit `Inventory`. Responsibilities:
- Open GUI for a player
- Handle click events via registered `GuiItem` slots
- Optional close handler
- Optional drag blocking
- Fill empty slots with a default item

**Key fields:**
- `Inventory inventory`
- `Map<Integer, GuiItem> slotItems` — slot → item+handler
- `Consumer<InventoryCloseEvent> closeHandler`
- `boolean draggable`
- `ItemStack fillItem`

### GuiBuilder — Fluent Construction

```java
Gui gui = GuiBuilder.create(6, Component.text("My GUI"))
    .slot(13, diamondItem, click -> player.sendMessage("clicked"))
    .fillBorder(glassPane)
    .closeHandler(event -> saveData())
    .draggable(false)
    .build();
```

**Builder methods:**
- `create(int rows, Component title)` — static factory
- `slot(int, ItemStack, Consumer<GuiClickEvent>)` — register a clickable slot
- `slots(int[], ItemStack, Consumer<GuiClickEvent>)` — register multiple slots with same handler
- `fill(ItemStack)` — fill all empty slots
- `fillRect(int from, int to, ItemStack)` — fill a rectangular area
- `fillBorder(ItemStack)` — fill top/bottom rows and side columns
- `slotUpdate(int, Supplier<ItemStack>)` — lazy-evaluated slot (for dynamic updates)
- `closeHandler(Consumer<InventoryCloseEvent>)`
- `draggable(boolean)`
- `build()` — construct the Gui

### PaginatedGui — Multi-Page Support

Extends `Gui`. Manages a list of items distributed across pages.

```java
PaginatedGui gui = GuiBuilder.paginated(6, Component.text("Items"))
    .items(allItems)
    .contentSlots(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25) // 14 slots
    .nextPageSlot(53, nextArrowItem)
    .prevPageSlot(45, prevArrowItem)
    .pageIndicatorSlot(49, (page, total) -> buildIndicator(page, total))
    .emptyPageSlot(22, emptyItem)
    .build();
```

**Key methods:**
- `nextPage(Player)` / `prevPage(Player)` — navigate
- `refresh()` — rebuild current page from items list
- `setItems(List<ItemStack>)` — replace all items, reset to page 0
- Auto-hides next/prev buttons when at first/last page

### GuiClickEvent

Wraps `InventoryClickEvent` with convenience:
- `player()` — the clicking player
- `slot()` — clicked slot index
- `clickType()` — ClickType enum
- `isLeftClick()` / `isRightClick()` / `isShiftClick()`
- `currentItem()` — item in clicked slot
- `cursorItem()` — item on cursor
- `setCancelled(boolean)` — cancel the event

### GuiTemplate

Define a layout skeleton once, instantiate with different data per player.

```java
GuiTemplate template = GuiTemplate.create(3, Component.text("Confirm"))
    .slot(11, ctx -> ctx.get("confirmItem"), click -> ctx.callback("confirm"))
    .slot(15, ctx -> ctx.get("cancelItem"), click -> ctx.callback("cancel"))
    .buildTemplate();

// Later:
Map<String, Object> data = Map.of("confirmItem", greenPane, "cancelItem", redPane);
Gui gui = template.build(player, data, callbackMap);
```

### Animation System

**GuiAnimation interface:**
```java
interface GuiAnimation {
    void start(Gui gui, Player player);
    void stop(Gui gui, Player player);
}
```

**SlotAnimation** — cycle through a list of ItemStacks at a given slot on a timer.
```java
new SlotAnimation(slot, List.of(frame1, frame2, frame3), 10 /* ticks */);
```

**FrameAnimation** — replace entire inventory with frames on a timer.
```java
new FrameAnimation(List.of(frame1Gui, frame2Gui), 20 /* ticks */);
```

### Stateful GUI

**GuiState** — abstract base for GUIs that hold per-player state.
```java
abstract class GuiState<D> {
    D data;
    abstract Gui build(Player player);
}
```

**GuiStateManager** — maps Player → GuiState. Handles lifecycle: register, update, unregister on close/server quit.

### Error Handling

- All builder methods throw `IllegalStateException` if misconfigured (e.g., building without calling `.build()`)
- Invalid hex colors throw `IllegalArgumentException` with descriptive message
- Null parameters throw `NullPointerException` via `Validate.notNull()` where appropriate
- Invalid slot indices throw `IndexOutOfBoundsException`

### Testing Strategy

- **ChatColors:** Unit tests for hex parsing (`#FF5500`, `FF5500`, invalid, edge cases), MiniMessage parsing, legacy → component conversion
- **star-gui:** Unit tests for GuiBuilder slot registration, PaginatedGui page calculation, GuiTemplate data injection; integration tests with MockBukkit for click handling, pagination navigation, animation ticking

---

## 3. Files Changed / Created

| File | Action |
|------|--------|
| `star-common/build.gradle.kts` | Add adventure-api + minimessage deps |
| `star-common/.../ChatColors.java` | Add Adventure methods |
| `star-items/.../ItemStackUtil.java` | Component overloads |
| `star-config/.../Config.java` | Component overloads |
| `star-gui/build.gradle.kts` | New module build file |
| `star-gui/.../gui/*.java` | New GUI framework classes |
| `settings.gradle.kts` | Include `star-gui` |
| `star-api/build.gradle.kts` | Shade `star-gui` |
| `README.md` | Update dough references |
| `star-protection/.../*.java` | Rename doughAction → starAction |

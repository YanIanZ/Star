# ChatColors Adventure Migration + star-gui Framework — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate ChatColors from legacy Bukkit ChatColor to Adventure API hybrid + build new star-gui full GUI framework module + clean up remaining "dough" brand references.

**Architecture:** ChatColors gains Adventure Component methods alongside legacy String methods (backward compatible). star-gui is a new module with Gui/Builder/PaginatedGui/Template/Animation/State classes, depending on star-common, star-items, star-inventories.

**Tech Stack:** Java 25, Paper API 1.21.11, Adventure API 4.17.0 (compileOnly, provided by Paper), JUnit 5, MockBukkit

---

## File Structure

| File | Action | Responsibility |
|------|--------|---------------|
| `star-common/build.gradle.kts` | Modify | Add adventure-api + minimessage deps |
| `star-common/.../ChatColors.java` | Modify | Add Adventure methods |
| `star-items/.../ItemStackUtil.java` | Modify | Component overloads for display name / lore |
| `star-config/.../Config.java` | Modify | Component overloads for inventory creation |
| `star-gui/build.gradle.kts` | Create | Module build config |
| `star-gui/.../gui/GuiItem.java` | Create | Slot item + click handler tuple |
| `star-gui/.../gui/GuiClickEvent.java` | Create | Click event wrapper |
| `star-gui/.../gui/Gui.java` | Create | Main GUI wrapper |
| `star-gui/.../gui/GuiBuilder.java` | Create | Fluent builder |
| `star-gui/.../gui/PaginatedGui.java` | Create | Multi-page GUI |
| `star-gui/.../gui/GuiTemplate.java` | Create | Reusable layout |
| `star-gui/.../gui/animation/GuiAnimation.java` | Create | Animation interface |
| `star-gui/.../gui/animation/SlotAnimation.java` | Create | Per-slot animation |
| `star-gui/.../gui/animation/FrameAnimation.java` | Create | Full-GUI animation |
| `star-gui/.../gui/state/GuiState.java` | Create | Stateful GUI base |
| `star-gui/.../gui/state/GuiStateManager.java` | Create | Per-player state tracking |
| `settings.gradle.kts` | Modify | Include star-gui |
| `star-api/build.gradle.kts` | Modify | Shade star-gui |
| `README.md` | Modify | Update dough references |
| `star-protection/.../HuskClaimsProtectionModule.java` | Modify | doughAction → starAction |
| `star-protection/.../HuskTownsProtectionModule.java` | Modify | doughAction → starAction |
| `star-protection/.../ProtectionManager.java` | Modify | "integrates dough" → "integrates Star" |

---

### Task 1: Add Adventure dependencies to star-common

**Files:**
- Modify: `star-common/build.gradle.kts`

- [ ] **Step 1: Add adventure-api and minimessage to compileOnly**

```kotlin
dependencies {
    implementation("io.papermc:paperlib:1.0.8")
    implementation("commons-lang:commons-lang:2.6")
    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
}
```

- [ ] **Step 2: Verify dependency resolution**

Run: `./gradlew :star-common:dependencies --configuration compileClasspath 2>&1 | grep adventure`
Expected: Shows `adventure-api:4.17.0` and `adventure-text-minimessage:4.17.0`

---

### Task 2: Enhance ChatColors.java with Adventure methods

**Files:**
- Modify: `star-common/src/main/java/dev/yanianz/star/common/ChatColors.java`

- [ ] **Step 1: Replace ChatColors.java with enhanced version**

```java
package dev.yanianz.star.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;

/**
 * Utilities related to {@link ChatColor} and Adventure {@link Component} formatting.
 *
 * @author TheBusyBiscuit
 */
public final class ChatColors {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private ChatColors() {}

    /**
     * Shortcut for: <code>ChatColor.translateAlternateColorCodes('&amp;', input)</code>
     *
     * @param input The String to colorize
     * @return The colorized String
     */
    public static @Nonnull String color(@Nonnull String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    /**
     * Colors the given String in alternating Bukkit ChatColors.
     *
     * @param text   The String to color
     * @param colors The Colors to apply
     * @return The alternating-colored String
     */
    public static @Nonnull String alternating(@Nonnull String text, ChatColor... colors) {
        int i = 0;
        StringBuilder builder = new StringBuilder(text.length() * 3);

        for (char c : text.toCharArray()) {
            builder.append(colors[i % colors.length].toString()).append(c);
            i++;
        }

        return builder.toString();
    }

    /**
     * Parses legacy {@code &} color codes into an Adventure {@link Component}.
     *
     * @param input The legacy color-coded String
     * @return The parsed Component
     */
    public static @Nonnull Component legacyToComponent(@Nonnull String input) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(input);
    }

    /**
     * Translates {@code &} color codes and returns an Adventure {@link Component}.
     *
     * @param input The legacy color-coded String
     * @return The colorized Component
     */
    public static @Nonnull Component colorToComponent(@Nonnull String input) {
        return legacyToComponent(input);
    }

    /**
     * Creates a {@link TextColor} from a hex string. Accepts both {@code "#FF5500"} and {@code "FF5500"}.
     *
     * @param hex The hex color string
     * @return The TextColor
     * @throws IllegalArgumentException if the hex string is invalid
     */
    public static @Nonnull TextColor hex(@Nonnull String hex) {
        String cleaned = hex.startsWith("#") ? hex.substring(1) : hex;
        if (cleaned.length() != 6) {
            throw new IllegalArgumentException("Hex color must be 6 characters, got: " + hex);
        }
        TextColor color = TextColor.fromHexString("#" + cleaned);
        if (color == null) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }
        return color;
    }

    /**
     * Creates a colored {@link Component} with the given hex color applied.
     *
     * @param hex  The hex color string
     * @param text The text to color
     * @return The colored Component
     */
    public static @Nonnull Component hexToComponent(@Nonnull String hex, @Nonnull String text) {
        return Component.text(text).color(hex(hex));
    }

    /**
     * Creates a colored {@link Component} with the given hex color applied (no text content).
     *
     * @param hex The hex color string
     * @return An empty Component with the color applied
     */
    public static @Nonnull Component hexToComponent(@Nonnull String hex) {
        return Component.text("").color(hex(hex));
    }

    /**
     * Parses a MiniMessage formatted string into an Adventure {@link Component}.
     *
     * @param input The MiniMessage string
     * @return The parsed Component
     */
    public static @Nonnull Component miniMessage(@Nonnull String input) {
        return MINI_MESSAGE.deserialize(input);
    }

    /**
     * Colors the given String in alternating Adventure TextColors.
     *
     * @param text   The text to color
     * @param colors The TextColors to alternate
     * @return The alternating-colored Component
     */
    public static @Nonnull Component alternating(@Nonnull String text, TextColor... colors) {
        Component.Builder builder = Component.text();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            builder.append(Component.text(String.valueOf(chars[i])).color(colors[i % colors.length]));
        }
        return builder.build();
    }

    /**
     * Creates a gradient color effect across the text, transitioning from the start color to the end color.
     *
     * @param text  The text to apply the gradient to
     * @param start The start color of the gradient
     * @param end   The end color of the gradient
     * @return The gradient-colored Component
     */
    public static @Nonnull Component gradient(@Nonnull String text, @Nonnull TextColor start, @Nonnull TextColor end) {
        Component.Builder builder = Component.text();
        char[] chars = text.toCharArray();
        int length = Math.max(1, chars.length - 1);
        for (int i = 0; i < chars.length; i++) {
            float ratio = (float) i / length;
            int r = (int) (start.red() + (end.red() - start.red()) * ratio);
            int g = (int) (start.green() + (end.green() - start.green()) * ratio);
            int b = (int) (start.blue() + (end.blue() - start.blue()) * ratio);
            builder.append(Component.text(String.valueOf(chars[i])).color(TextColor.color(r, g, b)));
        }
        return builder.build();
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :star-common:compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 3: Write ChatColors unit tests

**Files:**
- Create: `star-common/src/test/java/dev/yanianz/star/common/TestChatColors.java`

- [ ] **Step 1: Create test class**

```java
package dev.yanianz.star.common;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ChatColors")
class TestChatColors {

    @Nested
    @DisplayName("hex()")
    class Hex {

        @Test
        @DisplayName("parse hex with # prefix")
        void withHashPrefix() {
            TextColor color = ChatColors.hex("#FF5500");
            assertEquals(0xFF, color.red());
            assertEquals(0x55, color.green());
            assertEquals(0x00, color.blue());
        }

        @Test
        @DisplayName("parse hex without # prefix")
        void withoutHashPrefix() {
            TextColor color = ChatColors.hex("FF5500");
            assertEquals(0xFF, color.red());
            assertEquals(0x55, color.green());
            assertEquals(0x00, color.blue());
        }

        @Test
        @DisplayName("parse white")
        void white() {
            TextColor color = ChatColors.hex("#FFFFFF");
            assertEquals(0xFF, color.red());
            assertEquals(0xFF, color.green());
            assertEquals(0xFF, color.blue());
        }

        @Test
        @DisplayName("parse black")
        void black() {
            TextColor color = ChatColors.hex("#000000");
            assertEquals(0x00, color.red());
            assertEquals(0x00, color.green());
            assertEquals(0x00, color.blue());
        }

        @Test
        @DisplayName("throws on invalid hex length")
        void invalidLength() {
            assertThrows(IllegalArgumentException.class, () -> ChatColors.hex("#FFF"));
            assertThrows(IllegalArgumentException.class, () -> ChatColors.hex("12345"));
            assertThrows(IllegalArgumentException.class, () -> ChatColors.hex("#1234567"));
        }

        @Test
        @DisplayName("throws on non-hex characters")
        void nonHexChars() {
            assertThrows(IllegalArgumentException.class, () -> ChatColors.hex("#GGGGGG"));
            assertThrows(IllegalArgumentException.class, () -> ChatColors.hex("ZZZZZZ"));
        }
    }

    @Nested
    @DisplayName("legacyToComponent()")
    class LegacyToComponent {

        @Test
        @DisplayName("parses ampersand codes")
        void parsesAmpersandCodes() {
            Component result = ChatColors.legacyToComponent("&cHello &aWorld");
            String expectedPlain = "Hello World";
            assertEquals(expectedPlain, plain(result));
        }

        @Test
        @DisplayName("returns plain text for no codes")
        void plainText() {
            Component result = ChatColors.legacyToComponent("Hello World");
            assertEquals("Hello World", plain(result));
        }
    }

    @Nested
    @DisplayName("miniMessage()")
    class MiniMessage {

        @Test
        @DisplayName("parses MiniMessage color tag")
        void parsesColorTag() {
            Component result = ChatColors.miniMessage("<color:#FF5500>Hello</color>");
            assertEquals("Hello", plain(result));
        }

        @Test
        @DisplayName("returns plain text for no tags")
        void plainText() {
            Component result = ChatColors.miniMessage("Hello World");
            assertEquals("Hello World", plain(result));
        }
    }

    @Nested
    @DisplayName("alternating() with TextColor")
    class AlternatingTextColor {

        @Test
        @DisplayName("alternates between two colors")
        void twoColors() {
            Component result = ChatColors.alternating("AB", NamedTextColor.RED, NamedTextColor.BLUE);
            String plain = plain(result);
            assertEquals("AB", plain);
        }
    }

    @Nested
    @DisplayName("gradient()")
    class Gradient {

        @Test
        @DisplayName("creates gradient for multi-char text")
        void multiChar() {
            Component result = ChatColors.gradient("AB", NamedTextColor.RED, NamedTextColor.BLUE);
            assertEquals("AB", plain(result));
        }

        @Test
        @DisplayName("handles single character")
        void singleChar() {
            Component result = ChatColors.gradient("A", NamedTextColor.RED, NamedTextColor.BLUE);
            assertEquals("A", plain(result));
        }
    }

    private static String plain(Component component) {
        return net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(component);
    }
}
```

- [ ] **Step 2: Run tests**

Run: `./gradlew :star-common:test --tests "*TestChatColors*"`
Expected: All tests PASS

---

### Task 4: Update ItemStackUtil with Component overloads

**Files:**
- Modify: `star-items/src/main/java/dev/yanianz/star/items/ItemStackUtil.java`

- [ ] **Step 1: Add Component overloads after existing methods**

Add these imports at the top (alongside existing imports):
```java
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
```

Add these methods before the closing `}` of the class:

```java
    /**
     * Curries a {@link Consumer} which sets the display name to the given {@link Component}.
     *
     * @param component The component to set as display name
     * @return Returns a {@link Consumer}
     */
    public static Consumer<ItemMeta> editDisplayNameComponent(@Nullable Component component) {
        return (meta) -> {
            if (component != null) {
                meta.displayName(component);
            }
        };
    }

    /**
     * Curries a {@link Consumer} which sets the lore to the given {@link Component}s.
     *
     * @param lore The lore components to set
     * @return Returns a {@link Consumer}
     */
    public static Consumer<ItemMeta> editLoreComponents(@Nonnull List<Component> lore) {
        return (meta) -> {
            if (lore.isEmpty()) {
                meta.lore(Collections.emptyList());
                return;
            }
            meta.lore(new ArrayList<>(lore));
        };
    }
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :star-items:compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 5: Update Config.java with Component overloads

**Files:**
- Modify: `star-config/src/main/java/dev/yanianz/star/config/Config.java`

- [ ] **Step 1: Add Component import and new overloaded methods**

Add the import at the top of Config.java (alongside existing imports):
```java
import net.kyori.adventure.text.Component;
```

Add these methods after the existing `getInventory` methods:

```java
    /**
     * Gets the Contents of an Inventory at the specified path using a Component title.
     *
     * @param path  The path in the Config File
     * @param size  The Size of the Inventory
     * @param title The Title of the Inventory as a Component
     * @return The generated Inventory
     */
    @Nonnull
    public Inventory getInventory(@Nonnull String path, int size, @Nonnull Component title) {
        Inventory inventory = Bukkit.createInventory(null, size, title);
        for (int i = 0; i < size; i++) {
            inventory.setItem(i, getItem(path + "." + i));
        }
        return inventory;
    }

    /**
     * Gets the Contents of an Inventory at the specified path using a Component title.
     *
     * @param path  The path in the Config File
     * @param title The title of the inventory as a Component
     * @return The generated Inventory
     */
    @Nonnull
    public Inventory getInventory(@Nonnull String path, @Nonnull Component title) {
        int size = getInt(path + ".size");
        Inventory inventory = Bukkit.createInventory(null, size, title);
        for (int i = 0; i < size; i++) {
            inventory.setItem(i, getItem(path + "." + i));
        }
        return inventory;
    }
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :star-config:compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 6: Clean up "dough" brand references in star-protection

**Files:**
- Modify: `star-protection/src/main/java/dev/yanianz/star/protection/ProtectionManager.java`
- Modify: `star-protection/src/main/java/dev/yanianz/star/protection/modules/HuskClaimsProtectionModule.java`
- Modify: `star-protection/src/main/java/dev/yanianz/star/protection/modules/HuskTownsProtectionModule.java`

- [ ] **Step 1: Fix ProtectionManager.java javadoc (line 57)**

Change:
```java
     *            The plugin instance that integrates dough.
```
To:
```java
     *            The plugin instance that integrates Star.
```

- [ ] **Step 2: Fix HuskClaimsProtectionModule.java (lines 45-60)**

Change method parameter names and comments from `dough*/dough  *` to `star*/Star *`:
```java
        // Convert the Star interaction to HuskClaims' ActionType and check via the API
        return huskClaimsAPI.isOperationAllowed(Operation.of(
                huskClaimsAPI.getOnlineUser(p.getPlayer()),
                getHuskClaimsAction(action),
                huskClaimsAPI.getPosition(l)
        ));
    }

    /**
     * Returns the corresponding HuskClaims {@link OperationType} from the Star {@link Interaction}
     *
     * @param starAction The Star {@link Interaction}
     * @return The corresponding HuskClaims {@link OperationType}
     */
    public @Nonnull OperationType getHuskClaimsAction(@Nonnull Interaction starAction) {
        switch (starAction) {
```

- [ ] **Step 3: Fix HuskTownsProtectionModule.java (lines 45-60)**

Same pattern — rename `dough*/dough *` to `star*/Star *`:
```java
        // Convert the Star interaction to HuskTowns' ActionType and check via the API
        return huskTownsAPI.isOperationAllowed(Operation.of(
                huskTownsAPI.getOnlineUser(p.getPlayer()),
                getHuskTownsAction(action),
                huskTownsAPI.getPosition(l)
        ));
    }

    /**
     * Returns the corresponding HuskTowns {@link OperationType} from the Star {@link Interaction}
     *
     * @param starAction The Star {@link Interaction}
     * @return The corresponding HuskTowns {@link OperationType}
     */
    public @Nonnull OperationType getHuskTownsAction(@Nonnull Interaction starAction) {
        switch (starAction) {
```

---

### Task 7: Update README.md dough references

**Files:**
- Modify: `README.md`

- [ ] **Step 1: Change line 32**

Change:
```
> **Forked from [Dough](https://github.com/boblovespi/dough)** by [boblovespi](https://github.com/boblovespi) — rebranded, modernized, and extended with new modules & Java 25 support.
```
To:
```
> **Originally forked from Dough** by [boblovespi](https://github.com/boblovespi) — rebranded, modernized, and extended with new modules & Java 25 support.
```

- [ ] **Step 2: Change line 144**

Change:
```
  <sub>Originally forked from <a href="https://github.com/boblovespi/dough">Dough</a> by <a href="https://github.com/boblovespi">boblovespi</a></sub>
```
To:
```
  <sub>Originally forked from Dough by <a href="https://github.com/boblovespi">boblovespi</a></sub>
```

---

### Task 8: Create star-gui module structure and build file

**Files:**
- Create: `star-gui/build.gradle.kts`
- Create: `star-gui/src/main/java/dev/yanianz/star/gui/` (directory)
- Create: `star-gui/src/test/java/dev/yanianz/star/gui/` (directory)

- [ ] **Step 1: Create the module directory and build file**

```bash
mkdir -p star-gui/src/main/java/dev/yanianz/star/gui
mkdir -p star-gui/src/test/java/dev/yanianz/star/gui
```

- [ ] **Step 2: Write star-gui/build.gradle.kts**

```kotlin
dependencies {
    compileOnly(project(":star-common"))
    compileOnly(project(":star-items"))
    compileOnly(project(":star-inventories"))
    compileOnly("net.kyori:adventure-api:4.17.0")
}
```

- [ ] **Step 3: Register in settings.gradle.kts**

Add `"star-gui"` before `"star-api"`:
```kotlin
include(
    "star-common",
    "star-reflection",
    "star-config",
    "star-chat",
    "star-items",
    "star-data",
    "star-inventories",
    "star-skins",
    // "star-protection",
    "star-recipes",
    "star-updater",
    "star-scheduling",
    "star-swm",
    "star-gui",
    "star-api"
)
```

- [ ] **Step 4: Add star-gui to star-api aggregator**

In `star-api/build.gradle.kts`, add after `star-swm` line:
```kotlin
    implementation(project(":star-gui"))
```

- [ ] **Step 5: Verify gradle configuration**

Run: `./gradlew :star-gui:tasks`
Expected: Lists available tasks for star-gui module

---

### Task 9: Create GuiItem.java

**Files:**
- Create: `star-gui/src/main/java/dev/yanianz/star/gui/GuiItem.java`

- [ ] **Step 1: Write GuiItem.java**

```java
package dev.yanianz.star.gui;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Represents an ItemStack paired with a click handler in a Gui.
 */
public final class GuiItem {

    private final ItemStack item;
    private final Consumer<GuiClickEvent> handler;

    public GuiItem(@Nonnull ItemStack item, @Nonnull Consumer<GuiClickEvent> handler) {
        this.item = item;
        this.handler = handler;
    }

    @Nonnull
    public ItemStack getItem() {
        return item;
    }

    @Nonnull
    public Consumer<GuiClickEvent> getHandler() {
        return handler;
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :star-gui:compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 10: Create GuiClickEvent.java

**Files:**
- Create: `star-gui/src/main/java/dev/yanianz/star/gui/GuiClickEvent.java`

- [ ] **Step 1: Write GuiClickEvent.java**

```java
package dev.yanianz.star.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Wrapper around {@link InventoryClickEvent} for Gui click handling.
 */
public final class GuiClickEvent {

    private final InventoryClickEvent event;

    public GuiClickEvent(@Nonnull InventoryClickEvent event) {
        this.event = event;
    }

    @Nonnull
    public Player player() {
        return (Player) event.getWhoClicked();
    }

    public int slot() {
        return event.getSlot();
    }

    @Nonnull
    public ClickType clickType() {
        return event.getClick();
    }

    public boolean isLeftClick() {
        return event.isLeftClick();
    }

    public boolean isRightClick() {
        return event.isRightClick();
    }

    public boolean isShiftClick() {
        return event.isShiftClick();
    }

    @Nullable
    public ItemStack currentItem() {
        return event.getCurrentItem();
    }

    @Nullable
    public ItemStack cursorItem() {
        return event.getCursor();
    }

    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    @Nonnull
    public InventoryClickEvent getEvent() {
        return event;
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :star-gui:compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 11: Create Gui.java

**Files:**
- Create: `star-gui/src/main/java/dev/yanianz/star/gui/Gui.java`

- [ ] **Step 1: Write Gui.java**

```java
package dev.yanianz.star.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A GUI wrapper around a Bukkit {@link Inventory} with click handling.
 */
public class Gui implements Listener {

    private final Inventory inventory;
    private final Map<Integer, GuiItem> slotItems;
    private final Consumer<InventoryCloseEvent> closeHandler;
    private final boolean draggable;
    private final ItemStack fillItem;
    private boolean registered = false;

    Gui(@Nonnull Component title, int rows, @Nonnull Map<Integer, GuiItem> slotItems,
        @Nullable Consumer<InventoryCloseEvent> closeHandler, boolean draggable,
        @Nullable ItemStack fillItem) {
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        this.slotItems = slotItems;
        this.closeHandler = closeHandler;
        this.draggable = draggable;
        this.fillItem = fillItem;

        for (Map.Entry<Integer, GuiItem> entry : slotItems.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItem());
        }
        if (fillItem != null) {
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, fillItem.clone());
                }
            }
        }
    }

    @Nonnull
    public Inventory getInventory() {
        return inventory;
    }

    public void open(@Nonnull Player player, @Nonnull Plugin plugin) {
        if (!registered) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            registered = true;
        }
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }
        if (!draggable) {
            event.setCancelled(true);
        }
        GuiItem guiItem = slotItems.get(event.getSlot());
        if (guiItem != null) {
            guiItem.getHandler().accept(new GuiClickEvent(event));
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }
        if (!draggable) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }
        if (closeHandler != null) {
            closeHandler.accept(event);
        }
        HandlerList.unregisterAll(this);
        registered = false;
    }

    /**
     * Updates the item in a slot without rebuilding the GUI.
     */
    public void setItem(int slot, @Nonnull ItemStack item, @Nullable Consumer<GuiClickEvent> handler) {
        inventory.setItem(slot, item);
        if (handler != null) {
            slotItems.put(slot, new GuiItem(item, handler));
        } else {
            slotItems.remove(slot);
        }
    }

    /**
     * Refreshes all slot items from the current slotItems map.
     */
    public void refresh() {
        for (Map.Entry<Integer, GuiItem> entry : slotItems.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItem());
        }
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :star-gui:compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 12: Create GuiBuilder.java

**Files:**
- Create: `star-gui/src/main/java/dev/yanianz/star/gui/GuiBuilder.java`

- [ ] **Step 1: Write GuiBuilder.java**

```java
package dev.yanianz.star.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Fluent builder for constructing {@link Gui} instances.
 */
public final class GuiBuilder {

    private final Component title;
    private final int rows;
    private final Map<Integer, GuiItem> slotItems = new HashMap<>();
    private Consumer<InventoryCloseEvent> closeHandler;
    private boolean draggable = false;
    private ItemStack fillItem;

    private GuiBuilder(int rows, @Nonnull Component title) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Rows must be between 1 and 6");
        }
        this.title = title;
        this.rows = rows;
    }

    @Nonnull
    public static GuiBuilder create(int rows, @Nonnull Component title) {
        return new GuiBuilder(rows, title);
    }

    @Nonnull
    public GuiBuilder slot(int index, @Nonnull ItemStack item, @Nonnull Consumer<GuiClickEvent> handler) {
        slotItems.put(index, new GuiItem(item, handler));
        return this;
    }

    @Nonnull
    public GuiBuilder slots(int[] indices, @Nonnull ItemStack item, @Nonnull Consumer<GuiClickEvent> handler) {
        for (int index : indices) {
            slotItems.put(index, new GuiItem(item.clone(), handler));
        }
        return this;
    }

    @Nonnull
    public GuiBuilder fill(@Nonnull ItemStack item) {
        this.fillItem = item;
        return this;
    }

    @Nonnull
    public GuiBuilder fillBorder(@Nonnull ItemStack item) {
        int size = rows * 9;
        for (int i = 0; i < size; i++) {
            int row = i / 9;
            int col = i % 9;
            if (row == 0 || row == rows - 1 || col == 0 || col == 8) {
                if (!slotItems.containsKey(i)) {
                    slotItems.put(i, new GuiItem(item.clone(), e -> {}));
                }
            }
        }
        return this;
    }

    @Nonnull
    public GuiBuilder fillRect(int rowStart, int colStart, int rowEnd, int colEnd, @Nonnull ItemStack item) {
        for (int row = rowStart; row <= rowEnd; row++) {
            for (int col = colStart; col <= colEnd; col++) {
                int index = row * 9 + col;
                if (index >= 0 && index < rows * 9 && !slotItems.containsKey(index)) {
                    slotItems.put(index, new GuiItem(item.clone(), e -> {}));
                }
            }
        }
        return this;
    }

    @Nonnull
    public GuiBuilder closeHandler(@Nullable Consumer<InventoryCloseEvent> handler) {
        this.closeHandler = handler;
        return this;
    }

    @Nonnull
    public GuiBuilder draggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    @Nonnull
    public static PaginatedBuilder paginated(int rows, @Nonnull Component title) {
        return new PaginatedBuilder(rows, title);
    }

    @Nonnull
    public Gui build() {
        return new Gui(title, rows, slotItems, closeHandler, draggable, fillItem);
    }

    /**
     * Builder for {@link PaginatedGui}.
     */
    public static final class PaginatedBuilder {
        private final Component title;
        private final int rows;
        private final Map<Integer, GuiItem> staticItems = new HashMap<>();
        private int[] contentSlots = new int[0];
        private int nextPageSlot = -1;
        private int prevPageSlot = -1;
        private int pageIndicatorSlot = -1;
        private ItemStack nextPageItem;
        private ItemStack prevPageItem;
        private ItemStack emptyPageItem;
        private Consumer<InventoryCloseEvent> closeHandler;
        private boolean draggable = false;

        private PaginatedBuilder(int rows, @Nonnull Component title) {
            this.rows = rows;
            this.title = title;
        }

        @Nonnull
        public PaginatedBuilder contentSlots(int... slots) {
            this.contentSlots = slots;
            return this;
        }

        @Nonnull
        public PaginatedBuilder nextPageSlot(int slot, @Nonnull ItemStack item) {
            this.nextPageSlot = slot;
            this.nextPageItem = item;
            return this;
        }

        @Nonnull
        public PaginatedBuilder prevPageSlot(int slot, @Nonnull ItemStack item) {
            this.prevPageSlot = slot;
            this.prevPageItem = item;
            return this;
        }

        @Nonnull
        public PaginatedBuilder pageIndicatorSlot(int slot) {
            this.pageIndicatorSlot = slot;
            return this;
        }

        @Nonnull
        public PaginatedBuilder staticItem(int slot, @Nonnull ItemStack item, @Nonnull Consumer<GuiClickEvent> handler) {
            staticItems.put(slot, new GuiItem(item, handler));
            return this;
        }

        @Nonnull
        public PaginatedBuilder emptyPageItem(@Nonnull ItemStack item) {
            this.emptyPageItem = item;
            return this;
        }

        @Nonnull
        public PaginatedBuilder closeHandler(@Nullable Consumer<InventoryCloseEvent> handler) {
            this.closeHandler = handler;
            return this;
        }

        @Nonnull
        public PaginatedBuilder draggable(boolean draggable) {
            this.draggable = draggable;
            return this;
        }

        @Nonnull
        public PaginatedGui build() {
            return new PaginatedGui(title, rows, staticItems, contentSlots,
                nextPageSlot, prevPageSlot, pageIndicatorSlot,
                nextPageItem, prevPageItem, emptyPageItem, closeHandler, draggable);
        }
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :star-gui:compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 13: Create PaginatedGui.java

**Files:**
- Create: `star-gui/src/main/java/dev/yanianz/star/gui/PaginatedGui.java`

- [ ] **Step 1: Write PaginatedGui.java**

```java
package dev.yanianz.star.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A multi-page {@link Gui} that distributes items across pages with navigation.
 */
public class PaginatedGui extends Gui {

    private final int[] contentSlots;
    private final int nextPageSlot;
    private final int prevPageSlot;
    private final int pageIndicatorSlot;
    private final ItemStack nextPageItem;
    private final ItemStack prevPageItem;
    private final ItemStack emptyPageItem;
    private final Map<Integer, GuiItem> staticItems;
    private final int rows;
    private final Component title;

    private List<GuiItem> items = new ArrayList<>();
    private int currentPage = 0;

    PaginatedGui(@Nonnull Component title, int rows, @Nonnull Map<Integer, GuiItem> staticItems,
                 @Nonnull int[] contentSlots, int nextPageSlot, int prevPageSlot, int pageIndicatorSlot,
                 @Nullable ItemStack nextPageItem, @Nullable ItemStack prevPageItem,
                 @Nullable ItemStack emptyPageItem, @Nullable Consumer<InventoryCloseEvent> closeHandler,
                 boolean draggable) {
        super(title, rows, new HashMap<>(), closeHandler, draggable, null);
        this.title = title;
        this.rows = rows;
        this.staticItems = staticItems;
        this.contentSlots = contentSlots;
        this.nextPageSlot = nextPageSlot;
        this.prevPageSlot = prevPageSlot;
        this.pageIndicatorSlot = pageIndicatorSlot;
        this.nextPageItem = nextPageItem;
        this.prevPageItem = prevPageItem;
        this.emptyPageItem = emptyPageItem;
        renderPage();
    }

    /**
     * Replace all items and reset to the first page.
     */
    public void setItems(@Nonnull List<ItemStack> itemStacks) {
        this.items = itemStacks.stream().map(i -> new GuiItem(i, e -> {})).toList();
        this.currentPage = 0;
        renderPage();
    }

    /**
     * Replace all items with handlers and reset to the first page.
     */
    public void setItemsWithHandlers(@Nonnull List<Consumer<GuiClickEvent>> handlers, @Nonnull List<ItemStack> itemStacks) {
        this.items = new ArrayList<>();
        int count = Math.min(handlers.size(), itemStacks.size());
        for (int i = 0; i < count; i++) {
            this.items.add(new GuiItem(itemStacks.get(i), handlers.get(i)));
        }
        this.currentPage = 0;
        renderPage();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        if (contentSlots.length == 0 || items.isEmpty()) return 1;
        return (items.size() + contentSlots.length - 1) / contentSlots.length;
    }

    public void nextPage(@Nonnull Player player, @Nonnull Plugin plugin) {
        if (currentPage < getTotalPages() - 1) {
            currentPage++;
            renderPage();
            player.openInventory(getInventory());
        }
    }

    public void prevPage(@Nonnull Player player, @Nonnull Plugin plugin) {
        if (currentPage > 0) {
            currentPage--;
            renderPage();
            player.openInventory(getInventory());
        }
    }

    private void renderPage() {
        getInventory().clear();

        for (Map.Entry<Integer, GuiItem> entry : staticItems.entrySet()) {
            getInventory().setItem(entry.getKey(), entry.getValue().getItem());
        }

        int start = currentPage * contentSlots.length;
        for (int i = 0; i < contentSlots.length; i++) {
            int itemIndex = start + i;
            if (itemIndex < items.size()) {
                getInventory().setItem(contentSlots[i], items.get(itemIndex).getItem());
            } else if (emptyPageItem != null) {
                getInventory().setItem(contentSlots[i], emptyPageItem.clone());
            }
        }

        if (currentPage > 0 && prevPageSlot >= 0 && prevPageItem != null) {
            getInventory().setItem(prevPageSlot, prevPageItem.clone());
        }
        if (currentPage < getTotalPages() - 1 && nextPageSlot >= 0 && nextPageItem != null) {
            getInventory().setItem(nextPageSlot, nextPageItem.clone());
        }

        if (pageIndicatorSlot >= 0) {
            Component pageTitle = getInventory().title().append(
                Component.text(" (" + (currentPage + 1) + "/" + getTotalPages() + ")"));
            // Note: full title updating requires re-opening — this sets an info item
            getInventory().clear(pageIndicatorSlot);
        }
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :star-gui:compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 14: Create GuiTemplate.java

**Files:**
- Create: `star-gui/src/main/java/dev/yanianz/star/gui/GuiTemplate.java`

- [ ] **Step 1: Write GuiTemplate.java**

```java
package dev.yanianz.star.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A reusable GUI layout that can be instantiated with different data per player.
 *
 * @param <T> The data type passed to slot providers
 */
public final class GuiTemplate<T> {

    private final Component title;
    private final int rows;
    private final Map<Integer, SlotProvider<T>> slotProviders = new HashMap<>();
    private Consumer<InventoryCloseEvent> closeHandler;
    private boolean draggable = false;

    private GuiTemplate(int rows, @Nonnull Component title) {
        this.title = title;
        this.rows = rows;
    }

    @Nonnull
    public static <T> GuiTemplate<T> create(int rows, @Nonnull Component title) {
        return new GuiTemplate<>(rows, title);
    }

    @Nonnull
    public GuiTemplate<T> slot(int index, @Nonnull Function<T, ItemStack> itemProvider,
                                @Nonnull BiConsumer<T, GuiClickEvent> handlerProvider) {
        slotProviders.put(index, new SlotProvider<>(itemProvider, handlerProvider));
        return this;
    }

    @Nonnull
    public GuiTemplate<T> closeHandler(@Nonnull Consumer<InventoryCloseEvent> handler) {
        this.closeHandler = handler;
        return this;
    }

    @Nonnull
    public GuiTemplate<T> draggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    @Nonnull
    public Gui build(@Nonnull Player player, @Nonnull T data, @Nonnull Plugin plugin) {
        GuiBuilder builder = GuiBuilder.create(rows, title)
            .closeHandler(closeHandler)
            .draggable(draggable);

        for (Map.Entry<Integer, SlotProvider<T>> entry : slotProviders.entrySet()) {
            SlotProvider<T> provider = entry.getValue();
            ItemStack item = provider.itemProvider.apply(data);
            if (item != null) {
                builder.slot(entry.getKey(), item, e -> provider.handlerProvider.accept(data, e));
            }
        }

        return builder.build();
    }

    private record SlotProvider<T>(
        @Nonnull Function<T, ItemStack> itemProvider,
        @Nonnull BiConsumer<T, GuiClickEvent> handlerProvider
    ) {}
}
```

- [ ] **Step 2: Verify compilation**

Run: `./gradlew :star-gui:compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 15: Create animation classes

**Files:**
- Create: `star-gui/src/main/java/dev/yanianz/star/gui/animation/GuiAnimation.java`
- Create: `star-gui/src/main/java/dev/yanianz/star/gui/animation/SlotAnimation.java`
- Create: `star-gui/src/main/java/dev/yanianz/star/gui/animation/FrameAnimation.java`

- [ ] **Step 1: Create animation package directory**

```bash
mkdir -p star-gui/src/main/java/dev/yanianz/star/gui/animation
```

- [ ] **Step 2: Write GuiAnimation.java**

```java
package dev.yanianz.star.gui.animation;

import dev.yanianz.star.gui.Gui;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Interface for GUI animations.
 */
public interface GuiAnimation {

    void start(@Nonnull Gui gui, @Nonnull Player player);

    void stop(@Nonnull Gui gui, @Nonnull Player player);

    boolean isRunning();
}
```

- [ ] **Step 3: Write SlotAnimation.java**

```java
package dev.yanianz.star.gui.animation;

import dev.yanianz.star.gui.Gui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Cycles through a list of ItemStacks at a given slot on a timer.
 */
public final class SlotAnimation implements GuiAnimation {

    private final int slot;
    private final List<ItemStack> frames;
    private final long intervalTicks;
    private final Plugin plugin;
    private BukkitTask task;
    private int currentFrame = 0;

    public SlotAnimation(@Nonnull Plugin plugin, int slot, @Nonnull List<ItemStack> frames, long intervalTicks) {
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("frames must not be empty");
        }
        this.plugin = plugin;
        this.slot = slot;
        this.frames = frames;
        this.intervalTicks = intervalTicks;
    }

    @Override
    public void start(@Nonnull Gui gui, @Nonnull Player player) {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            currentFrame = (currentFrame + 1) % frames.size();
            gui.setItem(slot, frames.get(currentFrame).clone(), null);
        }, 0, intervalTicks);
    }

    @Override
    public void stop(@Nonnull Gui gui, @Nonnull Player player) {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public boolean isRunning() {
        return task != null;
    }
}
```

- [ ] **Step 4: Write FrameAnimation.java**

```java
package dev.yanianz.star.gui.animation;

import dev.yanianz.star.gui.Gui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Replaces the entire inventory contents with frames on a timer.
 */
public final class FrameAnimation implements GuiAnimation {

    private final List<List<ItemStack>> frames;
    private final long intervalTicks;
    private final Plugin plugin;
    private BukkitTask task;
    private int currentFrame = 0;

    public FrameAnimation(@Nonnull Plugin plugin, @Nonnull List<ItemStack[]> frames, long intervalTicks) {
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("frames must not be empty");
        }
        this.plugin = plugin;
        this.intervalTicks = intervalTicks;
        this.frames = frames.stream().map(List::of).toList();
    }

    @Override
    public void start(@Nonnull Gui gui, @Nonnull Player player) {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            currentFrame = (currentFrame + 1) % frames.size();
            Inventory inv = gui.getInventory();
            List<ItemStack> frame = frames.get(currentFrame);
            for (int i = 0; i < Math.min(frame.size(), inv.getSize()); i++) {
                ItemStack item = frame.get(i);
                inv.setItem(i, item != null ? item.clone() : null);
            }
        }, 0, intervalTicks);
    }

    @Override
    public void stop(@Nonnull Gui gui, @Nonnull Player player) {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public boolean isRunning() {
        return task != null;
    }
}
```

- [ ] **Step 5: Verify compilation**

Run: `./gradlew :star-gui:compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 16: Create stateful GUI classes

**Files:**
- Create: `star-gui/src/main/java/dev/yanianz/star/gui/state/GuiState.java`
- Create: `star-gui/src/main/java/dev/yanianz/star/gui/state/GuiStateManager.java`

- [ ] **Step 1: Create state package directory**

```bash
mkdir -p star-gui/src/main/java/dev/yanianz/star/gui/state
```

- [ ] **Step 2: Write GuiState.java**

```java
package dev.yanianz.star.gui.state;

import dev.yanianz.star.gui.Gui;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Base class for stateful GUIs. Each player gets their own state instance.
 *
 * @param <D> The per-player data type
 */
public abstract class GuiState<D> {

    protected D data;

    protected GuiState(@Nonnull D initialData) {
        this.data = initialData;
    }

    @Nonnull
    public D getData() {
        return data;
    }

    public void setData(@Nonnull D data) {
        this.data = data;
    }

    @Nonnull
    public abstract Gui build(@Nonnull Player player);
}
```

- [ ] **Step 3: Write GuiStateManager.java**

```java
package dev.yanianz.star.gui.state;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per-player {@link GuiState} instances.
 */
public final class GuiStateManager {

    private final Map<UUID, GuiState<?>> states = new ConcurrentHashMap<>();

    public void register(@Nonnull Player player, @Nonnull GuiState<?> state) {
        states.put(player.getUniqueId(), state);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <T> T get(@Nonnull Player player, @Nonnull Class<T> type) {
        GuiState<?> state = states.get(player.getUniqueId());
        if (state == null) {
            throw new IllegalStateException("No GUI state registered for player " + player.getName());
        }
        return (T) state.getData();
    }

    public void update(@Nonnull Player player, @Nonnull Object data) {
        GuiState<?> state = states.get(player.getUniqueId());
        if (state != null) {
            ((GuiState<Object>) state).setData(data);
        }
    }

    public <D> void updateState(@Nonnull Player player, @Nonnull GuiState<D> state) {
        states.put(player.getUniqueId(), state);
    }

    public void unregister(@Nonnull Player player) {
        states.remove(player.getUniqueId());
    }

    public boolean isRegistered(@Nonnull Player player) {
        return states.containsKey(player.getUniqueId());
    }
}
```

- [ ] **Step 4: Verify compilation**

Run: `./gradlew :star-gui:compileJava`
Expected: BUILD SUCCESSFUL

---

### Task 17: Write star-gui integration tests

**Files:**
- Create: `star-gui/src/test/java/dev/yanianz/star/gui/TestGuiBuilder.java`

- [ ] **Step 1: Write TestGuiBuilder.java**

```java
package dev.yanianz.star.gui;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GuiBuilder")
class TestGuiBuilder {

    private ServerMock server;
    private PlayerMock player;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        player = server.addPlayer();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("builds basic GUI with correct size")
    void buildsBasicGui() {
        Gui gui = GuiBuilder.create(3, Component.text("Test"))
            .slot(13, new ItemStack(Material.DIAMOND), e -> {})
            .build();

        assertEquals(27, gui.getInventory().getSize());
        assertEquals(Material.DIAMOND, gui.getInventory().getItem(13).getType());
    }

    @Test
    @DisplayName("click handler fires on slot click")
    void clickHandlerFires() {
        AtomicInteger clicks = new AtomicInteger(0);
        Gui gui = GuiBuilder.create(1, Component.text("Click Test"))
            .slot(0, new ItemStack(Material.STONE), e -> clicks.incrementAndGet())
            .build();

        gui.open(player, MockBukkit.createMockPlugin());

        InventoryClickEvent clickEvent = new InventoryClickEvent(
            player.getOpenInventory(),
            InventoryType.SlotType.CONTAINER,
            0,
            ClickType.LEFT,
            InventoryAction.PICKUP_ALL
        );
        server.getPluginManager().callEvent(clickEvent);

        assertEquals(1, clicks.get());
    }

    @Test
    @DisplayName("throws on invalid row count")
    void invalidRows() {
        assertThrows(IllegalArgumentException.class, () ->
            GuiBuilder.create(0, Component.text("Bad")));
        assertThrows(IllegalArgumentException.class, () ->
            GuiBuilder.create(7, Component.text("Bad")));
    }

    @Test
    @DisplayName("fill border adds items to edges only")
    void fillBorder() {
        Gui gui = GuiBuilder.create(3, Component.text("Border"))
            .fillBorder(new ItemStack(Material.GLASS_PANE))
            .build();

        // Top row all filled
        for (int i = 0; i < 9; i++) {
            assertNotNull(gui.getInventory().getItem(i));
        }
        // Center cell should be empty (13 = row 1, col 4)
        assertNull(gui.getInventory().getItem(13));
    }

    @Test
    @DisplayName("GuiItem getters work")
    void guiItemGetters() {
        dev.yanianz.star.gui.GuiItem item = new dev.yanianz.star.gui.GuiItem(
            new ItemStack(Material.EMERALD), e -> e.setCancelled(true));

        assertEquals(Material.EMERALD, item.getItem().getType());
        assertNotNull(item.getHandler());
    }
}
```

- [ ] **Step 2: Run tests**

Run: `./gradlew :star-gui:test --tests "*TestGuiBuilder*"`
Expected: All tests PASS

---

### Task 18: Final build verification

**Files:**
- (All project files)

- [ ] **Step 1: Full project compilation**

Run: `./gradlew compileJava`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Run all tests**

Run: `./gradlew test`
Expected: All tests PASS (BUILD SUCCESSFUL)

- [ ] **Step 3: Build aggregator JAR**

Run: `./gradlew :star-api:shadowJar`
Expected: BUILD SUCCESSFUL, JAR at `star-api/build/libs/star-api-1.0.1.jar`

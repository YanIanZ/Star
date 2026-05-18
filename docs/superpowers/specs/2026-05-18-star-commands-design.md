# Design: star-commands — Annotation + Builder Commands Framework

**Date:** 2026-05-18 | **Project:** Star

---

## 1. Architecture

```
star-commands/
  build.gradle.kts
  src/main/java/dev/yanianz/star/commands/
    Command.java              # @Command annotation
    TabComplete.java          # @TabComplete annotation  
    Arg.java                  # @Arg annotation (argument binding)
    CommandContext.java       # Execution context
    CommandManager.java       # Registration + package scanner
    CommandBuilder.java       # Fluent builder for complex commands
    CommandNode.java          # Node metadata + executor
    ArgumentParser.java       # Type-safe argument parsing
    ArgumentType.java         # Built-in parsers (int, double, player, enum, etc.)
    CooldownManager.java      # Per-player cooldown tracking
    CompositeCommand.java     # Dynamic command composition
```

## 2. API

### @Command annotation
`name`, `aliases`, `permission`, `usage`, `description`, `default` flag

### @Arg annotation
`value` (nameliteral), `optional` (default false)

### @TabComplete annotation  
`suggestions` (static), `completer` (dynamic method name)

### CommandManager
- `register(Object)` — scan annotations on object
- `scan(String package)` — package scan
- `builder(String name)` — fluent builder

### CommandBuilder
- `.aliases()` `.permission()` `.usage()` `.description()`
- `.arg(name, type)` `.optionalArg(name, type)`
- `.tabComplete(name, completer)`
- `.executor(consumer)` `.register()`

### CommandContext
- `sender()` `asPlayer()` `asConsole()`
- `get(name)` `getOrDefault(name, default)`
- `send(String)` (uses ChatColors)
- `label()` `args()` (raw access)

### ArgumentType
INTEGER, DOUBLE, STRING, BOOLEAN, PLAYER, OFFLINE_PLAYER, WORLD, MATERIAL, GAMEMODE, ENUM, CUSTOM

### CooldownManager
- `setCooldown(command, player, seconds)`
- `getRemaining(command, player)` → long (seconds left)
- `isOnCooldown(command, player)` → boolean

## 3. Dependencies
- star-common (StarLogger, ChatColors)
- Paper API

## 4. Testing
- Unit tests for ArgumentParser, CooldownManager, CommandContext
- MockBukkit integration for CommandManager registration

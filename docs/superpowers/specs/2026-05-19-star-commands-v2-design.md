# Design: star-commands v2 — Brigadier + Conditions + Middleware

**Date:** 2026-05-19 | **Project:** Star v1.9.0

## Classes (18 total)

| File | Action | Purpose |
|------|--------|---------|
| `CommandManager.java` | Rewrite | Brigadier registration with Paper API |
| `CommandBuilder.java` | Enhance | Add conditions, flags, middleware |
| `CommandNode.java` | Enhance | Condition list, middleware list, flags |
| `CommandContext.java` | Enhance | Brigadier context wrapper, getFlag() |
| `ArgumentType.java` | Keep | Brigadier argument type mapping |
| `CompositeCommand.java` | Rewrite | Brigadier literal tree builder |
| `CooldownManager.java` | Keep | Wire into CooldownCondition |
| `condition/CommandCondition.java` | New | `boolean test(CommandContext)` |
| `condition/PermissionCondition.java` | New | hasPermission wrapper |
| `condition/CooldownCondition.java` | New | CooldownManager integration |
| `condition/GameModeCondition.java` | New | Specific GameMode required |
| `condition/WorldCondition.java` | New | Specific world filter |
| `condition/PlayerOnlyCondition.java` | New | Console blocked |
| `condition/CustomCondition.java` | New | Lambda condition |
| `middleware/CommandMiddleware.java` | New | `void intercept(ctx, next)` |
| `flag/Flag.java` | New | `--flag -f` annotation |
| `flag/FlagParser.java` | New | Parse flags from args |
| `help/HelpGenerator.java` | New | Auto /help generation |

## API examples as shown above

## Dependencies: star-common, Paper API (Brigadier bundled)

## Testing: 8-10 tests covering builder, conditions, flags, middleware

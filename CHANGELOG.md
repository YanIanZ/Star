# Changelog

## [1.1.0] — 2026-05-18

### Added
- **star-gui** — Fluent GUI framework (builder, pagination, templates, animations, per-player state)
- **star-vfx** — Particle visual effects (11 shapes, 4 animated effects, fluent builder)
- **star-economy** — Unified economy abstraction (Vault provider, banks, transactions, currency formatting)
- **star-commands** — Annotation + builder command framework (9 built-in argument types, cooldowns)
- **star-world** — World utilities (block scanning, schematics, terrain queries, region operations)
- Adventure API integration in ChatColors (hex, gradient, MiniMessage, legacy component conversion)
- Component overloads in ItemStackUtil and Config for Adventure text support

### Changed
- Rebranded all remaining "dough" references to "Star"
- Improved javadoc coverage to 100% across all new modules

### Fixed
- ArgumentType silently swallowing parse exceptions (now logs at FINE level)
- DeathEffect raw type `List<?>` corrected to `List<Player>`

## [1.0.1] — 2026-04-17

### Changed
- Revamped README with badges and fork attribution
- Excluded star-protection from main build (unresolvable external dependencies)

### Fixed
- JitPack compatibility (removed Temurin vendor constraint)
- CI build failures (MockBukkit dependency alignment, test fixes)

## [1.0.0] — 2026-04-15

- Initial Star release — rebranded from Dough
- Maven → Gradle migration, Java 25 support
- Core modules: common, reflection, config, chat, data, skins, items, inventories, protection, recipes, updater, scheduling, SWM

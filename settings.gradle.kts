rootProject.name = "star"

include(
    "star-common",
    "star-reflection",
    "star-config",
    "star-chat",
    "star-items",
    "star-data",
    "star-inventories",
    "star-skins",
    // "star-protection", // Excluded: external plugin APIs not resolvable on public repos
    "star-recipes",
    "star-updater",
    "star-scheduling",
    "star-swm",
    "star-gui",
    "star-vfx",
    "star-economy",
    "star-commands",
    "star-world",
    "star-holograms",
    "star-database",
    "star-combat",
    "star-npc",
    "star-api"
)

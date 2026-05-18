<h1 align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)">
    <img alt="Star" src="https://img.shields.io/badge/%E2%AD%90-Star-FFD700?style=for-the-badge&labelColor=1a1a2e" />
  </picture>
</h1>

<p align="center">
  <b>A powerful, feature-packed library for the modern Spigot/Paper developer.</b>
</p>

<p align="center">
  <a href="https://github.com/YanIanZ/Star/actions">
    <img alt="Build Status" src="https://github.com/YanIanZ/Star/actions/workflows/build.yml/badge.svg?event=push">
  </a>
  <a href="https://jitpack.io/#YanIanZ/Star">
    <img alt="JitPack" src="https://jitpack.io/v/YanIanZ/Star.svg">
  </a>
  <a href="LICENSE">
    <img alt="License" src="https://img.shields.io/badge/license-MIT-blue.svg">
  </a>
  <a href="#requirements">
    <img alt="Java" src="https://img.shields.io/badge/Java-25%2B-%23ED8B00?logo=openjdk&logoColor=white">
  </a>
  <a href="#requirements">
    <img alt="Minecraft" src="https://img.shields.io/badge/Paper-1.18.2--1.21.11-%23324B7C?logo=minetest&logoColor=white">
  </a>
</p>

---

> **Originally forked from Dough** by [TheBusyBiscuit](https://github.com/Slimefun/dough) — rebranded, modernized, and extended with new modules & Java 25 support.

Star bundles everything you need to build plugins faster: reflection, config, chat, data, skins, items, inventories, protection, recipes, scheduling, auto-updates, and SlimeWorldManager — all under one cohesive API.

## Modules

| Module | Description |
|:-------|:------------|
| `star-common` | Base utilities, logging, version utilities |
| `star-reflection` | Reflection utilities for NMS access |
| `star-config` | Simple configuration API |
| `star-chat` | Chat input handling |
| `star-data` | Data structures, persistent data types, collections |
| `star-skins` | Player skin and custom head management |
| `star-items` | ItemStack utilities and NMS adapters |
| `star-inventories` | Inventory utilities |
| `star-protection` | Unified protection API (20+ plugin integrations) |
| `star-recipes` | Recipe management utilities |
| `star-updater` | Plugin auto-update system |
| `star-scheduling` | Task scheduling and queues |
| `star-swm` | SlimeWorldManager adapter (AdvancedSlimePaper + SourbyCraft) |
| `star-api` | Aggregator — shades all modules into one JAR |

## Installation

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("dev.yanianz:star-api:1.0.1")
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.yanianz</groupId>
        <artifactId>star-api</artifactId>
        <version>1.0.1</version>
    </dependency>
</dependencies>
```

### Shadow / Relocation

To bundle Star inside your plugin with relocation:

**Gradle (Kotlin DSL)**
```kotlin
plugins {
    id("com.gradleup.shadow") version "9.0.0"
}

shadowJar {
    relocate("dev.yanianz.star", "your.package.star")
}
```

**Maven**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.5.1</version>
    <configuration>
        <relocations>
            <relocation>
                <pattern>dev.yanianz.star</pattern>
                <shadedPattern>your.package.star</shadedPattern>
            </relocation>
        </relocations>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals><goal>shade</goal></goals>
        </execution>
    </executions>
</plugin>
```

## Requirements

| Requirement | Version |
|:------------|:--------|
| Java | **25+** |
| Paper | **1.18.2** — **1.21.11** |

## License

This project is licensed under the **MIT License** — see [LICENSE](LICENSE) for details.

## Author

**[iYanZ](https://github.com/YanIanZ)**

---

<p align="center">
  <sub>Originally forked from Dough by <a href="https://github.com/boblovespi">boblovespi</a></sub>
</p>

# Star

<p align="center">
    <a href="https://github.com/YanIanZ/Star/actions">
        <img alt="Build Status" src="https://github.com/YanIanZ/Star/actions/workflows/build.yml/badge.svg?event=push" />
    </a>
    <a href="https://jitpack.io/#YanIanZ/Star">
        <img alt="JitPack" src="https://jitpack.io/v/YanIanZ/Star.svg" />
    </a>
</p>

Star is a powerful library for the everyday Spigot/Paper developer. It is packed with useful features and APIs to speed up plugin development.

## Getting Started

### Via Gradle

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("dev.yanianz:star-api:1.0.0")
}
```

### Via Maven

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
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### Shadowing Star

To shadow Star and relocate it into your plugin:

**Gradle (Kotlin DSL):**
```kotlin
plugins {
    id("com.gradleup.shadow") version "9.0.0"
}

shadowJar {
    relocate("dev.yanianz.star", "your.package.star")
}
```

**Maven:**
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

## Modules

| Module | Description |
|--------|-------------|
| star-common | Base utilities, logging, version utilities |
| star-reflection | Reflection utilities for NMS access |
| star-config | Simple configuration API |
| star-chat | Chat input handling |
| star-data | Data structures, persistent data types, collections |
| star-skins | Player skin and custom head management |
| star-items | ItemStack utilities and NMS adapters |
| star-inventories | Inventory utilities |
| star-protection | Unified protection API (20+ plugin integrations) |
| star-recipes | Recipe management utilities |
| star-updater | Plugin auto-update system |
| star-scheduling | Task scheduling and queues |
| star-swm | SlimeWorldManager adapter (AdvancedSlimePaper + SourbyCraft) |
| star-api | Aggregator — shades all modules into one JAR |

## Requirements

- Java 25+
- Paper 1.18.2 or newer (tested up to 1.21.11)

## License

MIT License. See [LICENSE](LICENSE) for details.

## Author

iYanZ

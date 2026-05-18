# Design: star-packet + star-test

**Date:** 2026-05-19 | **Project:** Star v1.5.0

---

## 1. star-packet (8 classes)

| Class | Purpose |
|-------|---------|
| `PacketSender.java` | Send packets to players/worlds |
| `PacketListener.java` | Listen for incoming/outgoing packets |
| `PacketAdapter.java` | Paper PacketAdapter wrapper |
| `PacketType.java` | Packet type registry |
| `ProtocolVersion.java` | Protocol version detection from player |
| `ProxyDetector.java` | Detect Bungee/Velocity/Waterfall |
| `TransferHelper.java` | Server transfer (1.20.5+) |
| `BrandMessenger.java` | Plugin message channel helper |

## 2. star-test (8 classes)

| Class | Purpose |
|-------|---------|
| `TestPlugin.java` | Mock plugin wrapper for tests |
| `PlayerFactory.java` | Fluent mock player builder |
| `InventoryFactory.java` | Fluent mock inventory builder |
| `WorldFactory.java` | Mock world builder |
| `LocationFactory.java` | Quick location builders |
| `ItemFactory.java` | ItemStack preset builder |
| `SchedulerMock.java` | Mock Bukkit scheduler |
| `TestAssertions.java` | Custom assertion methods |

## 3. Dependencies
- star-common (StarLogger)
- Paper API
- star-test: MockBukkit (test scope), star-common

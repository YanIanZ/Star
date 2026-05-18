# Design: star-vfx — Visual Effects & Particles Module

**Date:** 2026-05-18
**Author:** rheninxy
**Project:** Star (dev.yanianz:star)

---

## 1. Motivation

A fluent particle/visual effect framework for Paper plugins. Covers both static shapes and animated effects — trails, explosions, death effects, block break effects. Builder pattern consistent with star-gui.

## 2. Architecture

```
star-vfx/
  build.gradle.kts
  src/main/java/dev/yanianz/star/vfx/
    ParticleBuilder.java          # Fluent entry point (shapes + effects)
    ParticleShape.java            # Shape interface (play/pause)
    ParticleEffect.java           # Effect interface (start/stop/isRunning)
    ParticleUtils.java            # Convenience static shortcuts
    shapes/
      CircleShape.java            # 2D circle
      LineShape.java              # Line between two points
      SphereShape.java            # 3D hollow sphere
      HelixShape.java             # Spiral/helix
      CubeShape.java              # 3D wireframe cube
      ConeShape.java              # 3D cone
      CylinderShape.java          # 3D cylinder
      WaveShape.java              # Sine wave
      StarShape.java              # Star polygon
      HeartShape.java             # Heart curve (parametric)
      RainbowShape.java           # Rainbow arc
    effects/
      TrailEffect.java            # Entity trail
      ExplosionEffect.java        # Burst/explosion effect
      DeathEffect.java            # On-death visual
      BlockBreakEffect.java       # Block break particles
```

## 3. Dependencies

- `star-common` — StarLogger
- Paper-provided: Adventure API

## 4. API Design

### ParticleBuilder

Fluent builder for both shapes and effects. Two static entry points:

```java
ParticleBuilder.shape(ShapeType type)    // → returns shape builder
ParticleBuilder.effect(EffectType type)  // → returns effect builder
```

**Common builder methods (shared):**
- `.particle(Particle)` — particle type
- `.count(int)` — number of particles
- `.color(Color)` — color (for REDSTONE/NOTE particles)
- `.offset(double x, double y, double z)` — random offset per particle
- `.speed(double)` — particle speed
- `.viewers(Player...)` — filter who sees it

**Shape builder methods:**
- `.at(Location)` — center location
- `.radius(double)` — circle/sphere radius
- `.length(double)` — line/length
- `.height(double)` — cone/cylinder/helix height
- `.rotations(int)` — helix rotations
- `.amplitude(double)` — wave amplitude
- `.frequency(double)` — wave frequency
- `.startAngle(double)` — star shape start angle
- `.endAngle(double)` — rainbow arc end angle
- `.points(int)` — star points (default 5)
- `.play()` — spawn particles immediately

**Effect builder methods:**
- `.follow(Entity)` — entity to follow (trails)
- `.at(Location)` — fixed location effects
- `.interval(int)` — ticks between spawns
- `.duration(int)` — total ticks to run
- `.build()` — returns ParticleEffect instance

### ParticleShape Interface

```java
public interface ParticleShape {
    void play();
    Particle getParticle();
    int getCount();
    Location getLocation();
}
```

### ParticleEffect Interface

```java
public interface ParticleEffect {
    void start();
    void stop();
    boolean isRunning();
}
```

### ShapeType / EffectType Enums

```java
public enum ShapeType {
    CIRCLE, LINE, SPHERE, HELIX, CUBE,
    CONE, CYLINDER, WAVE, STAR, HEART, RAINBOW
}

public enum EffectType {
    TRAIL, EXPLOSION, DEATH, BLOCK_BREAK
}
```

## 5. Shape Implementations

### CircleShape
Renders `count` particles evenly distributed on a 2D circle of given `radius`.

### LineShape
Renders `count` particles evenly spaced along a line from `at` to `at + length * direction`.

### SphereShape
Renders particles on a 3D sphere surface using Fibonacci sphere algorithm for uniform distribution.

### HelixShape
Renders particles along a helical path: `x = R*cos(t)`, `y = t*height/rotations`, `z = R*sin(t)`.

### CubeShape
Renders particles along 12 edges of a cube of given radius.

### ConeShape
Renders particles forming a cone from base circle to apex.

### CylinderShape
Renders particles on top/bottom circles + connecting lines.

### WaveShape
Renders `count` particles along a sine wave: `y = amplitude * sin(x * frequency)`.

### StarShape
Renders particles on a star polygon with `points` points and given inner/outer radius ratio.

### HeartShape
Renders particles on a parametric heart curve: `x = 16*sin(t)^3`, `y = 13*cos(t) - 5*cos(2t) - 2*cos(3t) - cos(4t)`.

### RainbowShape
Renders an arc of particles with cycling colors.

## 6. Effect Implementations

### TrailEffect
Follows an entity, spawning particles at the entity's location on an interval. Runs on a Bukkit scheduler.

### ExplosionEffect
Spawns a burst of particles expanding outward from a location, optionally with sphere/circle shape.

### DeathEffect
Combines multiple shapes (burst + helix rise) on entity death.

### BlockBreakEffect
Spawns particles matching the break effect of a specific block Material.

## 7. ParticleUtils

Static convenience methods:
```java
ParticleUtils.circle(Location loc, Particle p, int count, double radius)
ParticleUtils.line(Location from, Location to, Particle p, double spacing)
ParticleUtils.explosion(Location loc, Particle p, int count, double radius)
```

## 8. Error Handling

- Invalid particle types throw `IllegalArgumentException`
- Negative count/radius throw `IllegalArgumentException`
- Null locations throw `NullPointerException`

## 9. Testing

- Unit tests for shape coordinate calculations
- Visual behavior is integration-testable with MockBukkit scheduler

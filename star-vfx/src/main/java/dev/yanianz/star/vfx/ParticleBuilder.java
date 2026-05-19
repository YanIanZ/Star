package dev.yanianz.star.vfx;

import dev.yanianz.star.vfx.effects.*;
import dev.yanianz.star.vfx.shapes.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fluent builder for constructing particle shapes and effects.
 * Two entry points: {@link #shape(ShapeType)} for one-shot particle shapes and
 * {@link #effect(EffectType)} for animated effects.
 * Call {@link #play()} to spawn shapes immediately or {@link #build()} to get a schedulable {@link ParticleEffect}.
 */
public final class ParticleBuilder {

    private final ShapeType shapeType;
    private final EffectType effectType;
    private Particle particle = Particle.FLAME;
    private int count = 10;
    private Location location;
    private Entity entity;
    private double radius = 1.0;
    private double length = 1.0;
    private double height = 1.0;
    private int rotations = 3;
    private double amplitude = 1.0;
    private double frequency = 1.0;
    private double startAngle = 0;
    private double endAngle = Math.PI;
    private int points = 5;
    private int interval = 2;
    private int duration = 100;
    private Color color;
    private double offsetX = 0;
    private double offsetY = 0;
    private double offsetZ = 0;
    private double speed = 0;
    private Plugin plugin;
    private List<Player> viewers = new ArrayList<>();

    private ParticleBuilder(ShapeType type) {
        this.shapeType = type;
        this.effectType = null;
    }

    private ParticleBuilder(EffectType type) {
        this.effectType = type;
        this.shapeType = null;
    }

    /**
     * Returns a new ParticleBuilder for a one-shot particle shape of the given type.
     */
    @Nonnull
    public static ParticleBuilder shape(@Nonnull ShapeType type) {
        return new ParticleBuilder(type);
    }

    /**
     * Returns a new ParticleBuilder for an animated effect of the given type.
     */
    @Nonnull
    public static ParticleBuilder effect(@Nonnull EffectType type) {
        return new ParticleBuilder(type);
    }

    /** Sets the particle type. */
    @Nonnull public ParticleBuilder particle(@Nonnull Particle p) { this.particle = p; return this; }
    /** Sets the number of particles. */
    @Nonnull public ParticleBuilder count(int c) { this.count = c; return this; }
    /** Sets the spawn location. */
    @Nonnull public ParticleBuilder at(@Nonnull Location loc) { this.location = loc; return this; }
    /** Sets an entity to follow for trail effects. */
    @Nonnull public ParticleBuilder follow(@Nonnull Entity e) { this.entity = e; return this; }
    /** Sets the radius of the shape or effect. */
    @Nonnull public ParticleBuilder radius(double r) { this.radius = r; return this; }
    /** Sets the length for line or wave shapes. */
    @Nonnull public ParticleBuilder length(double l) { this.length = l; return this; }
    /** Sets the height for shapes that support it (helix, cone, cylinder). */
    @Nonnull public ParticleBuilder height(double h) { this.height = h; return this; }
    /** Sets the number of rotations for helix shapes. */
    @Nonnull public ParticleBuilder rotations(int r) { this.rotations = r; return this; }
    /** Sets the wave amplitude. */
    @Nonnull public ParticleBuilder amplitude(double a) { this.amplitude = a; return this; }
    /** Sets the wave frequency. */
    @Nonnull public ParticleBuilder frequency(double f) { this.frequency = f; return this; }
    /** Sets the starting angle for arc/rainbow shapes. */
    @Nonnull public ParticleBuilder startAngle(double a) { this.startAngle = a; return this; }
    /** Sets the ending angle for arc/rainbow shapes. */
    @Nonnull public ParticleBuilder endAngle(double a) { this.endAngle = a; return this; }
    /** Sets the number of points for star shapes. */
    @Nonnull public ParticleBuilder points(int p) { this.points = p; return this; }
    /** Sets the tick interval between effect iterations. */
    @Nonnull public ParticleBuilder interval(int i) { this.interval = i; return this; }
    /** Sets the effect duration in ticks. */
    @Nonnull public ParticleBuilder duration(int d) { this.duration = d; return this; }
    /** Sets the particle color (for colorable particles). */
    @Nonnull public ParticleBuilder color(@Nonnull Color c) { this.color = c; return this; }
    /** Sets the offset from the spawn location. */
    @Nonnull public ParticleBuilder offset(double x, double y, double z) { this.offsetX = x; this.offsetY = y; this.offsetZ = z; return this; }
    /** Sets the particle speed. */
    @Nonnull public ParticleBuilder speed(double s) { this.speed = s; return this; }
    /** Sets the plugin for scheduling effect tasks. */
    @Nonnull public ParticleBuilder plugin(@Nonnull Plugin plugin) { this.plugin = plugin; return this; }
    /** Sets the players who will see the particles. */
    @Nonnull public ParticleBuilder viewers(@Nonnull Player... v) { Collections.addAll(this.viewers, v); return this; }

    /** Creates and plays the configured particle shape immediately. */
    public void play() {
        if (location == null) throw new IllegalStateException("Location must be set via .at() before playing");
        createShape().play();
    }

    /**
     * Creates and returns a schedulable ParticleEffect for the configured effect.
     */
    @Nonnull
    public ParticleEffect build() {
        return createEffect();
    }

    private ParticleShape createShape() {
        return switch (shapeType) {
            case CIRCLE -> new CircleShape(particle, count, location, radius, offsetX, offsetY, offsetZ, speed, color, viewers);
            case LINE -> new LineShape(particle, count, location, length, offsetX, offsetY, offsetZ, speed, color, viewers);
            case SPHERE -> new SphereShape(particle, count, location, radius, offsetX, offsetY, offsetZ, speed, color, viewers);
            case HELIX -> new HelixShape(particle, count, location, radius, height, rotations, offsetX, offsetY, offsetZ, speed, color, viewers);
            case CUBE -> new CubeShape(particle, count, location, radius, offsetX, offsetY, offsetZ, speed, color, viewers);
            case CONE -> new ConeShape(particle, count, location, radius, height, offsetX, offsetY, offsetZ, speed, color, viewers);
            case CYLINDER -> new CylinderShape(particle, count, location, radius, height, offsetX, offsetY, offsetZ, speed, color, viewers);
            case WAVE -> new WaveShape(particle, count, location, length, amplitude, frequency, offsetX, offsetY, offsetZ, speed, color, viewers);
            case STAR -> new StarShape(particle, count, location, radius, points, startAngle, offsetX, offsetY, offsetZ, speed, color, viewers);
            case HEART -> new HeartShape(particle, count, location, radius, offsetX, offsetY, offsetZ, speed, color, viewers);
            case RAINBOW -> new RainbowShape(particle, count, location, radius, startAngle, endAngle, offsetX, offsetY, offsetZ, speed, color, viewers);
        };
    }

    private ParticleEffect createEffect() {
        if (plugin == null) throw new IllegalStateException("plugin() must be set before building an effect");
        return switch (effectType) {
            case TRAIL -> new TrailEffect(particle, count, entity, interval, duration, offsetX, offsetY, offsetZ, speed, color, viewers, plugin);
            case EXPLOSION -> new ExplosionEffect(particle, count, location, radius, interval, duration, offsetX, offsetY, offsetZ, speed, color, viewers, plugin);
            case DEATH -> new DeathEffect(particle, count, location, radius, height, offsetX, offsetY, offsetZ, speed, color, viewers, plugin);
            case BLOCK_BREAK -> new BlockBreakEffect(particle, count, location, radius, offsetX, offsetY, offsetZ, speed, color, viewers, plugin);
            case SPIRAL -> new SpiralEffect(plugin, particle, count, location, radius, height, rotations, interval, duration, offsetX, offsetY, offsetZ, speed, color, viewers);
        };
    }
}

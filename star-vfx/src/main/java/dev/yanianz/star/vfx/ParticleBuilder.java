package dev.yanianz.star.vfx;

import dev.yanianz.star.vfx.effects.*;
import dev.yanianz.star.vfx.shapes.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private List<Player> viewers = new ArrayList<>();

    private ParticleBuilder(ShapeType type) {
        this.shapeType = type;
        this.effectType = null;
    }

    private ParticleBuilder(EffectType type) {
        this.effectType = type;
        this.shapeType = null;
    }

    @Nonnull
    public static ParticleBuilder shape(@Nonnull ShapeType type) {
        return new ParticleBuilder(type);
    }

    @Nonnull
    public static ParticleBuilder effect(@Nonnull EffectType type) {
        return new ParticleBuilder(type);
    }

    @Nonnull public ParticleBuilder particle(@Nonnull Particle p) { this.particle = p; return this; }
    @Nonnull public ParticleBuilder count(int c) { this.count = c; return this; }
    @Nonnull public ParticleBuilder at(@Nonnull Location loc) { this.location = loc; return this; }
    @Nonnull public ParticleBuilder follow(@Nonnull Entity e) { this.entity = e; return this; }
    @Nonnull public ParticleBuilder radius(double r) { this.radius = r; return this; }
    @Nonnull public ParticleBuilder length(double l) { this.length = l; return this; }
    @Nonnull public ParticleBuilder height(double h) { this.height = h; return this; }
    @Nonnull public ParticleBuilder rotations(int r) { this.rotations = r; return this; }
    @Nonnull public ParticleBuilder amplitude(double a) { this.amplitude = a; return this; }
    @Nonnull public ParticleBuilder frequency(double f) { this.frequency = f; return this; }
    @Nonnull public ParticleBuilder startAngle(double a) { this.startAngle = a; return this; }
    @Nonnull public ParticleBuilder endAngle(double a) { this.endAngle = a; return this; }
    @Nonnull public ParticleBuilder points(int p) { this.points = p; return this; }
    @Nonnull public ParticleBuilder interval(int i) { this.interval = i; return this; }
    @Nonnull public ParticleBuilder duration(int d) { this.duration = d; return this; }
    @Nonnull public ParticleBuilder color(@Nonnull Color c) { this.color = c; return this; }
    @Nonnull public ParticleBuilder offset(double x, double y, double z) { this.offsetX = x; this.offsetY = y; this.offsetZ = z; return this; }
    @Nonnull public ParticleBuilder speed(double s) { this.speed = s; return this; }
    @Nonnull public ParticleBuilder viewers(@Nonnull Player... v) { Collections.addAll(this.viewers, v); return this; }

    public void play() {
        if (location == null) throw new IllegalStateException("Location must be set via .at() before playing");
        createShape().play();
    }

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
        return switch (effectType) {
            case TRAIL -> new TrailEffect(particle, count, entity, interval, duration, offsetX, offsetY, offsetZ, speed, color, viewers);
            case EXPLOSION -> new ExplosionEffect(particle, count, location, radius, interval, duration, offsetX, offsetY, offsetZ, speed, color, viewers);
            case DEATH -> new DeathEffect(particle, count, location, radius, height, offsetX, offsetY, offsetZ, speed, color, viewers);
            case BLOCK_BREAK -> new BlockBreakEffect(particle, count, location, radius, offsetX, offsetY, offsetZ, speed, color, viewers);
        };
    }
}

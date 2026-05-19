package dev.yanianz.star.vfx;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ParticleBuilder")
class TestParticleBuilder {

    private ServerMock server;
    private WorldMock world;
    private Location location;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        world = spy(new WorldMock());
        location = new Location(world, 0, 64, 0);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    private void allowParticles() {
        doNothing().when(world).spawnParticle(any(Particle.class), any(Location.class), anyInt(),
            anyDouble(), anyDouble(), anyDouble(), anyDouble());
        doNothing().when(world).spawnParticle(any(Particle.class), any(Location.class), anyInt(),
            anyDouble(), anyDouble(), anyDouble(), anyDouble(), any());
    }

    @Test
    @DisplayName("builds circle shape and plays without error")
    void circleShape() {
        allowParticles();
        assertDoesNotThrow(() ->
            ParticleBuilder.shape(ShapeType.CIRCLE)
                .at(location).particle(Particle.FLAME).count(10).radius(3.0).play()
        );
    }

    @Test
    @DisplayName("builds sphere shape")
    void sphereShape() {
        allowParticles();
        assertDoesNotThrow(() ->
            ParticleBuilder.shape(ShapeType.SPHERE)
                .at(location).particle(Particle.FLAME).count(50).radius(2.0).play()
        );
    }

    @Test
    @DisplayName("builds all shape types without error")
    void allShapes() {
        allowParticles();
        for (ShapeType type : ShapeType.values()) {
            assertDoesNotThrow(() ->
                ParticleBuilder.shape(type).at(location).particle(Particle.FLAME)
                    .count(10).radius(3.0).length(5.0).height(4.0).play()
            );
        }
    }

    @Test
    @DisplayName("throws when location not set")
    void missingLocation() {
        assertThrows(IllegalStateException.class, () ->
            ParticleBuilder.shape(ShapeType.CIRCLE).particle(Particle.FLAME).count(10).play()
        );
    }

    @Test
    @DisplayName("static utils do not throw")
    void staticUtils() {
        allowParticles();
        assertDoesNotThrow(() -> ParticleUtils.circle(location, Particle.FLAME, 10, 3.0));
        assertDoesNotThrow(() -> ParticleUtils.line(location, location.clone().add(5, 0, 0), Particle.FLAME, 1.0));
        assertDoesNotThrow(() -> ParticleUtils.sphere(location, Particle.FLAME, 20, 2.0));
    }

    @Test
    @DisplayName("effect builds without error")
    void effectBuild() {
        ParticleEffect effect = ParticleBuilder.effect(EffectType.EXPLOSION)
            .at(location).particle(Particle.FLAME).count(20).radius(3.0)
            .interval(1).duration(10)
            .plugin(MockBukkit.createMockPlugin("StarVFX"))
            .build();
        assertNotNull(effect);
        assertFalse(effect.isRunning());
    }

    @Test @DisplayName("spiral effect builds")
    void spiralEffect() {
        ParticleEffect effect = ParticleBuilder.effect(EffectType.SPIRAL)
            .at(location).particle(Particle.FLAME).count(30).radius(3.0).height(5.0).rotations(4)
            .interval(1).duration(10).plugin(org.mockbukkit.mockbukkit.MockBukkit.createMockPlugin()).build();
        assertNotNull(effect);
        assertFalse(effect.isRunning());
    }

    @Test @DisplayName("all shapes play without error")
    void allShapesPlay() {
        allowParticles();
        for (ShapeType type : ShapeType.values()) {
            assertDoesNotThrow(() -> ParticleBuilder.shape(type).at(location).particle(Particle.FLAME).count(10).play());
        }
    }

    @Test @DisplayName("all effects build without error")
    void allEffectsBuild() {
        allowParticles();
        for (EffectType type : EffectType.values()) {
            assertDoesNotThrow(() -> ParticleBuilder.effect(type).at(location).particle(Particle.FLAME).count(10).radius(3.0).plugin(org.mockbukkit.mockbukkit.MockBukkit.createMockPlugin()).build());
        }
    }

    @Test @DisplayName("particle builder with color")
    void particleWithColor() {
        allowParticles();
        assertDoesNotThrow(() -> ParticleBuilder.shape(ShapeType.CIRCLE).at(location).particle(Particle.DUST).color(Color.RED).count(10).play());
    }

    @Test @DisplayName("viewers filter")
    void viewersFilter() {
        allowParticles();
        assertDoesNotThrow(() -> ParticleBuilder.shape(ShapeType.CIRCLE).at(location).particle(Particle.FLAME).count(5).viewers().play());
    }

    @Test @DisplayName("offset and speed applied")
    void offsetAndSpeed() {
        allowParticles();
        assertDoesNotThrow(() -> ParticleBuilder.shape(ShapeType.SPHERE).at(location).particle(Particle.FLAME).count(10).offset(0.5, 0.5, 0.5).speed(0.1).play());
    }
}

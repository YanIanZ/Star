package dev.yanianz.star.vfx;

import org.bukkit.Location;
import org.bukkit.Particle;
import javax.annotation.Nonnull;

/**
 * Interface for one-shot particle shapes.
 * Implementations render particles at a location when {@link #play()} is called.
 */
public interface ParticleShape {
    void play();
    @Nonnull Particle getParticle();
    int getCount();
    @Nonnull Location getLocation();
}

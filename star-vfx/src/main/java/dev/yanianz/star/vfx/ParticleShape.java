package dev.yanianz.star.vfx;

import org.bukkit.Location;
import org.bukkit.Particle;
import javax.annotation.Nonnull;

public interface ParticleShape {
    void play();
    @Nonnull Particle getParticle();
    int getCount();
    @Nonnull Location getLocation();
}

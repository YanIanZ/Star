package dev.yanianz.star.vfx;

/**
 * Interface for animated/ongoing particle effects.
 * Use {@link #start()} to begin and {@link #stop()} to cancel.
 */
public interface ParticleEffect {
    void start();
    void stop();
    boolean isRunning();
}

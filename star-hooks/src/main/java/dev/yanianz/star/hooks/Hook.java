package dev.yanianz.star.hooks;
import javax.annotation.Nonnull;

public interface Hook {
    @Nonnull String getName();
    boolean isPresent();
    void enable();
    void disable();
}

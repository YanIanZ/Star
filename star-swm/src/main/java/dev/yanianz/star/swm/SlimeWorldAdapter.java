package dev.yanianz.star.swm;

import org.jetbrains.annotations.Nullable;

/**
 * Common interface for SlimeWorldManager adapters.
 * Implementations bridge between Star and specific SWM implementations
 * (AdvancedSlimePaper, SourbyCraft, etc.).
 */
public interface SlimeWorldAdapter {

    /**
     * Returns the name of this adapter (e.g., "aspaper", "sourbycraft").
     */
    String getName();

    /**
     * Checks if this adapter is available on the current server.
     * Implementation should check for the presence of required classes.
     */
    boolean isAvailable();

    /**
     * Returns the priority of this adapter. Higher priority adapters
     * are preferred when multiple are available.
     */
    default int getPriority() {
        return 0;
    }

    /**
     * Loads a slime world by name.
     *
     * @param worldName the name of the world to load
     * @param readOnly  whether the world should be loaded in read-only mode
     * @return a SlimeWorldInstance if loading succeeded, null otherwise
     * @throws Exception if world loading fails
     */
    @Nullable
    Object loadWorld(String worldName, boolean readOnly) throws Exception;

    /**
     * Saves a slime world.
     *
     * @param worldInstance the world instance to save
     * @throws Exception if saving fails
     */
    void saveWorld(Object worldInstance) throws Exception;

    /**
     * Checks if a world with the given name exists.
     */
    boolean worldExists(String worldName);

    /**
     * Deletes a world by name.
     */
    void deleteWorld(String worldName) throws Exception;
}

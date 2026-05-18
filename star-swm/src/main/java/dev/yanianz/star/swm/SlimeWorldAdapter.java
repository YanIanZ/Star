package dev.yanianz.star.swm;

/**
 * Common interface for manually registered SlimeWorldManager adapters.
 * For automatic detection, use {@link SlimeWorldManager#getApi()} which
 * discovers the API via ServiceLoader and reflection.
 */
public interface SlimeWorldAdapter {

    String getName();

    boolean isAvailable();

    default int getPriority() {
        return 0;
    }

    Object loadWorld(String worldName, boolean readOnly) throws Exception;

    void saveWorld(Object worldInstance) throws Exception;

    boolean worldExists(String worldName);

    void deleteWorld(String worldName) throws Exception;
}

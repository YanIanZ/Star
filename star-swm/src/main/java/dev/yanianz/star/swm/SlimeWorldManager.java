package dev.yanianz.star.swm;

import java.lang.reflect.Method;
import java.util.ServiceLoader;

import dev.yanianz.star.common.StarLogger;

/**
 * Unified SlimeWorldManager that discovers the best available SWM API
 * via ServiceLoader (primary) with reflection fallback.
 *
 * Usage:
 *   Object api = SlimeWorldManager.getApi();
 *   if (api != null) { ... } // AdvancedSlimePaperAPI or equivalent
 */
public final class SlimeWorldManager {

    private static final StarLogger LOGGER = new StarLogger("swm");
    private static volatile Object cachedApi;
    private static volatile boolean cachedAvailable;

    private static final String SOURBYCRAFT_API = "dev.iyanz.sourbycraft.swm.api.AdvancedSlimePaperAPI";
    private static final String ASPAPER_API = "com.infernalsuite.asp.api.AdvancedSlimePaperAPI";

    private SlimeWorldManager() {}

    /**
     * Returns the best available SWM API instance, or null if none found.
     * Result is cached after first detection.
     */
    public static Object getApi() {
        if (cachedApi != null || cachedAvailable) {
            return cachedApi;
        }

        // 1. Try ServiceLoader for SourbyCraft's AdvancedSlimePaperAPI
        try {
            Class<?> apiClass = Class.forName(SOURBYCRAFT_API);
            for (Object impl : ServiceLoader.load(apiClass)) {
                cachedApi = impl;
                LOGGER.info("Found SWM API via ServiceLoader: " + SOURBYCRAFT_API);
                return cachedApi;
            }
        } catch (ClassNotFoundException ignored) {}

        // 2. Try ServiceLoader for standalone AdvancedSlimePaper API
        try {
            Class<?> apiClass = Class.forName(ASPAPER_API);
            for (Object impl : ServiceLoader.load(apiClass)) {
                cachedApi = impl;
                LOGGER.info("Found SWM API via ServiceLoader: " + ASPAPER_API);
                return cachedApi;
            }
        } catch (ClassNotFoundException ignored) {}

        // 3. Fallback: reflection on SourbyCraft's static instance() method
        try {
            Class<?> apiClass = Class.forName(SOURBYCRAFT_API);
            Method m = apiClass.getMethod("instance");
            cachedApi = m.invoke(null);
            LOGGER.info("Found SWM API via reflection: " + SOURBYCRAFT_API);
            return cachedApi;
        } catch (Exception ignored) {}

        // 4. Fallback: reflection on AsPaper's static instance() method
        try {
            Class<?> apiClass = Class.forName(ASPAPER_API);
            Method m = apiClass.getMethod("instance");
            cachedApi = m.invoke(null);
            LOGGER.info("Found SWM API via reflection: " + ASPAPER_API);
            return cachedApi;
        } catch (Exception ignored) {}

        cachedAvailable = true;
        LOGGER.warning("No SlimeWorldManager API implementation found on classpath");
        return null;
    }

    /**
     * Checks if any SWM API is available.
     */
    public static boolean isAvailable() {
        return getApi() != null;
    }

    /**
     * Resets the cached API. Next call to getApi() will re-detect.
     */
    public static void reset() {
        cachedApi = null;
        cachedAvailable = false;
    }
}

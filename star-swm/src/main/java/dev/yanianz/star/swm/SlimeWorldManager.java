package dev.yanianz.star.swm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * Unified SlimeWorldManager that auto-detects the best available SWM adapter.
 *
 * Usage:
 *   SlimeWorldAdapter adapter = SlimeWorldManager.getAdapter();
 *   adapter.loadWorld("my_world", false);
 *
 * Detection order:
 * 1. Check registered adapters by priority (highest first)
 * 2. Fall back to built-in adapters (SourbyCraft, then AsPaper)
 * 3. Throw IllegalStateException if no adapter is available
 */
public final class SlimeWorldManager {

    private static final Logger LOGGER = Logger.getLogger("star:swm");
    private static SlimeWorldAdapter cachedAdapter;
    private static final List<SlimeWorldAdapter> registeredAdapters = new ArrayList<>();

    private SlimeWorldManager() {}

    /**
     * Returns the best available SlimeWorldAdapter.
     * Results are cached after first detection.
     *
     * @return the highest-priority available adapter
     * @throws IllegalStateException if no adapter is available
     */
    public static SlimeWorldAdapter getAdapter() {
        if (cachedAdapter != null) {
            return cachedAdapter;
        }

        List<SlimeWorldAdapter> candidates = new ArrayList<>();

        // Check ServiceLoader-discovered adapters first
        ServiceLoader.load(SlimeWorldAdapter.class).forEach(candidates::add);

        // Add built-in adapters
        candidates.add(new SourbyCraftSWMAdapter());
        candidates.add(new AsPaperSWMAdapter());

        // Add manually registered adapters
        candidates.addAll(registeredAdapters);

        // Filter to available, sort by priority descending
        SlimeWorldAdapter best = candidates.stream()
            .filter(SlimeWorldAdapter::isAvailable)
            .max(Comparator.comparingInt(SlimeWorldAdapter::getPriority))
            .orElse(null);

        if (best == null) {
            throw new IllegalStateException(
                "No SlimeWorldManager implementation available. " +
                "Ensure AdvancedSlimePaper or SourbyCraft is installed."
            );
        }

        cachedAdapter = best;
        LOGGER.info("Using SWM adapter: " + best.getName() + " (priority: " + best.getPriority() + ")");
        return best;
    }

    /**
     * Registers a custom adapter. Registered adapters are checked before built-in ones.
     */
    public static void registerAdapter(SlimeWorldAdapter adapter) {
        registeredAdapters.add(adapter);
        cachedAdapter = null;
    }

    /**
     * Resets the cached adapter. Next call to getAdapter() will re-detect.
     */
    public static void reset() {
        cachedAdapter = null;
    }
}

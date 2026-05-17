package dev.yanianz.star.swm;

import org.jetbrains.annotations.Nullable;

/**
 * Adapter for AdvancedSlimePaper (aspaper) SWM implementation.
 * Detects availability by checking for com.infernalsuite.asp.api.AdvancedSlimePaperAPI.
 */
public class AsPaperSWMAdapter implements SlimeWorldAdapter {

    private static final String API_CLASS = "com.infernalsuite.asp.api.AdvancedSlimePaperAPI";

    @Override
    public String getName() {
        return "aspaper";
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName(API_CLASS);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public @Nullable Object loadWorld(String worldName, boolean readOnly) throws Exception {
        throw new UnsupportedOperationException("AsPaperSWMAdapter requires AdvancedSlimePaper on classpath");
    }

    @Override
    public void saveWorld(Object worldInstance) throws Exception {
        throw new UnsupportedOperationException("AsPaperSWMAdapter requires AdvancedSlimePaper on classpath");
    }

    @Override
    public boolean worldExists(String worldName) {
        throw new UnsupportedOperationException("AsPaperSWMAdapter requires AdvancedSlimePaper on classpath");
    }

    @Override
    public void deleteWorld(String worldName) throws Exception {
        throw new UnsupportedOperationException("AsPaperSWMAdapter requires AdvancedSlimePaper on classpath");
    }
}

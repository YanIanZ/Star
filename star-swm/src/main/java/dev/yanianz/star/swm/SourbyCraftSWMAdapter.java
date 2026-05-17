package dev.yanianz.star.swm;

import org.jetbrains.annotations.Nullable;

/**
 * Adapter for SourbyCraft SWM implementation.
 * Detects availability by checking for dev.iyanz.sourbycraft.swm.api.AdvancedSlimePaperAPI.
 */
public class SourbyCraftSWMAdapter implements SlimeWorldAdapter {

    private static final String API_CLASS = "dev.iyanz.sourbycraft.swm.api.AdvancedSlimePaperAPI";

    @Override
    public String getName() {
        return "sourbycraft";
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
        return 10;
    }

    @Override
    public @Nullable Object loadWorld(String worldName, boolean readOnly) throws Exception {
        throw new UnsupportedOperationException("SourbyCraftSWMAdapter requires SourbyCraft on classpath");
    }

    @Override
    public void saveWorld(Object worldInstance) throws Exception {
        throw new UnsupportedOperationException("SourbyCraftSWMAdapter requires SourbyCraft on classpath");
    }

    @Override
    public boolean worldExists(String worldName) {
        throw new UnsupportedOperationException("SourbyCraftSWMAdapter requires SourbyCraft on classpath");
    }

    @Override
    public void deleteWorld(String worldName) throws Exception {
        throw new UnsupportedOperationException("SourbyCraftSWMAdapter requires SourbyCraft on classpath");
    }
}

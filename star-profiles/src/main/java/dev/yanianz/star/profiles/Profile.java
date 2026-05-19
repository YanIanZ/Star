package dev.yanianz.star.profiles;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class Profile {
    private final String name;
    private final ProfileData data;
    private final List<ItemStack> inventory;
    private Location location;
    private final long createdAt;
    private volatile long updatedAt;
    private final String serverId;

    Profile(String name, ProfileData data, List<ItemStack> inventory, Location location,
            long createdAt, long updatedAt, String serverId) {
        this.name = name;
        this.data = data;
        this.inventory = inventory;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.serverId = serverId;
    }

    @Nonnull public String getName() { return name; }

    @Nonnull public ProfileData getData() { return data; }

    @Nonnull public List<ItemStack> getInventory() { return inventory; }

    @Nonnull public Optional<Location> getLocation() { return Optional.ofNullable(location); }

    public void setLocation(@Nonnull Location loc) { this.location = loc; }

    public long getCreatedAt() { return createdAt; }

    public long getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    @Nonnull public String getServerId() { return serverId; }

    @Nonnull public static Builder builder(@Nonnull String name) { return new Builder(name); }

    public static final class Builder {
        private final String name;
        private final ProfileData data = new ProfileData();
        private final List<ItemStack> inventory = new ArrayList<>();
        private Location location;
        private long createdAt = System.currentTimeMillis();
        private long updatedAt = System.currentTimeMillis();
        private String serverId = "";

        Builder(String name) { this.name = name; }

        @Nonnull public Builder data(@Nonnull String key, @Nonnull String value) { data.set(key, value); return this; }

        @Nonnull public Builder inventory(@Nonnull ItemStack... items) { Collections.addAll(inventory, items); return this; }

        @Nonnull public Builder location(@Nonnull Location loc) { this.location = loc; return this; }

        @Nonnull public Builder createdAt(long createdAt) { this.createdAt = createdAt; return this; }

        @Nonnull public Builder updatedAt(long updatedAt) { this.updatedAt = updatedAt; return this; }

        @Nonnull public Builder serverId(@Nonnull String serverId) { this.serverId = serverId; return this; }

        @Nonnull public Profile build() { return new Profile(name, data, inventory, location, createdAt, updatedAt, serverId); }
    }
}

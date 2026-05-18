package dev.yanianz.star.profiles;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nonnull;
import java.util.*;

public final class Profile {
    private final String name;
    private final ProfileData data;
    private final List<ItemStack> inventory;
    private Location location;

    Profile(String name, ProfileData data, List<ItemStack> inventory, Location location) {
        this.name = name;
        this.data = data;
        this.inventory = inventory;
        this.location = location;
    }

    @Nonnull public String getName() { return name; }

    @Nonnull public ProfileData getData() { return data; }

    @Nonnull public List<ItemStack> getInventory() { return inventory; }

    @Nonnull public Optional<Location> getLocation() { return Optional.ofNullable(location); }

    public void setLocation(@Nonnull Location loc) { this.location = loc; }

    @Nonnull public static Builder builder(@Nonnull String name) { return new Builder(name); }

    public static final class Builder {
        private final String name;
        private final ProfileData data = new ProfileData();
        private final List<ItemStack> inventory = new ArrayList<>();
        private Location location;

        Builder(String name) { this.name = name; }

        @Nonnull public Builder data(@Nonnull String key, @Nonnull String value) { data.set(key, value); return this; }

        @Nonnull public Builder inventory(@Nonnull ItemStack... items) { Collections.addAll(inventory, items); return this; }

        @Nonnull public Builder location(@Nonnull Location loc) { this.location = loc; return this; }

        @Nonnull public Profile build() { return new Profile(name, data, inventory, location); }
    }
}

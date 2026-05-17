package dev.yanianz.star.updater;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import org.bukkit.plugin.Plugin;

import dev.yanianz.star.versions.Version;

public interface PluginUpdater<V extends Version> {

    @Nonnull
    Plugin getPlugin();

    @Nonnull
    File getFile();

    @Nonnull
    V getCurrentVersion();

    @Nonnull
    CompletableFuture<V> getLatestVersion();

    int getConnectionTimeout();

    void start();

}

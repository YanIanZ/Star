package dev.yanianz.star.profiles.storage;

import dev.yanianz.star.profiles.Profile;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ProfileRepository {

    @Nonnull
    CompletableFuture<Void> save(@Nonnull UUID playerUuid, @Nonnull Profile profile);

    @Nonnull
    CompletableFuture<Optional<Profile>> load(@Nonnull UUID playerUuid, @Nonnull String profileName);

    @Nonnull
    CompletableFuture<List<Profile>> loadAll(@Nonnull UUID playerUuid);

    @Nonnull
    CompletableFuture<Void> delete(@Nonnull UUID playerUuid, @Nonnull String profileName);

    @Nonnull
    CompletableFuture<Void> deleteAll(@Nonnull UUID playerUuid);
}

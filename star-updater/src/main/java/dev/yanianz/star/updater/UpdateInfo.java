package dev.yanianz.star.updater;

import java.net.URL;
import javax.annotation.Nullable;
import dev.yanianz.star.versions.Version;

record UpdateInfo(URL url, Version version, @Nullable String checksum) {
    UpdateInfo(URL url, Version version) {
        this(url, version, null);
    }
}

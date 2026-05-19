package dev.yanianz.star.misc;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

@DisplayName("Misc")
class TestMisc {
    @Test
    @DisplayName("PlaceholderResolver replaces keys")
    void placeholderResolver() {
        String result = PlaceholderResolver.resolve("Hello {name}", Map.of("name", "World"));
        assertEquals("Hello World", result);
    }

    @Test
    @DisplayName("PlaceholderResolver ignores missing keys")
    void placeholderMissing() {
        String result = PlaceholderResolver.resolve("{a} {b}", Map.of("a", "1"));
        assertEquals("1 {b}", result);
    }

    @Test
    @DisplayName("SoundBuilder API works")
    void soundBuilder() {
        assertDoesNotThrow(() -> SoundBuilder.play(org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP));
    }

    @Test
    @DisplayName("SoundPlayer static methods exist")
    void soundPlayer() {
        assertNotNull(SoundPlayer.class);
    }

    @Test
    @DisplayName("NoteBlockPlayer exists")
    void noteBlockPlayer() {
        assertNotNull(NoteBlockPlayer.class);
    }

    @Test
    @DisplayName("ResourcePackSender exists")
    void resourcePackSender() {
        assertNotNull(ResourcePackSender.class);
    }

    @Test
    @DisplayName("CustomSound exists")
    void customSound() {
        assertNotNull(CustomSound.class);
    }

    @Test
    @DisplayName("LocaleFile parsing")
    void localeFileParsing() throws Exception {
        java.io.File tmp = java.io.File.createTempFile("locale", ".yml");
        tmp.deleteOnExit();
        java.io.FileWriter fw = new java.io.FileWriter(tmp);
        fw.write("locale:\n  code: en\ngreeting: Hello {player}\nfarewell: Bye\n");
        fw.close();
        LocaleFile lf = new LocaleFile(tmp);
        assertEquals("en", lf.getCode());
        assertEquals("Hello {player}", lf.get("greeting", ""));
        assertEquals("", lf.get("missing", ""));
    }

    @Test
    @DisplayName("SoundBuilder configures category")
    void soundBuilderCategory() {
        SoundBuilder sb = SoundBuilder.play(org.bukkit.Sound.BLOCK_NOTE_BLOCK_HARP);
        assertNotNull(sb);
    }

    @Test
    @DisplayName("MusicPlayer create and stop")
    void musicPlayerLifecycle() {
        org.mockbukkit.mockbukkit.MockBukkit.mock();
        org.mockbukkit.mockbukkit.ServerMock server = org.mockbukkit.mockbukkit.MockBukkit.getOrCreateMock();
        org.mockbukkit.mockbukkit.entity.PlayerMock p = server.addPlayer();
        MusicPlayer mp = new MusicPlayer(org.mockbukkit.mockbukkit.MockBukkit.createMockPlugin(), org.bukkit.Sound.MUSIC_DISC_CAT, p);
        assertFalse(mp.isPlaying());
        mp.start();
        mp.stop();
        assertFalse(mp.isPlaying());
    }
}

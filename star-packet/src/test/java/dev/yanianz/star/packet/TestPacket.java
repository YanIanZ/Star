package dev.yanianz.star.packet;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

@DisplayName("Packet")
class TestPacket {

    @Test
    @DisplayName("PacketListener registers handlers")
    void listenerHandlers() {
        PacketListener listener = new PacketListener(null);
        listener.onReceive(PacketType.CHAT, (p, o) -> {});
        assertEquals(1, listener.getReceiveHandlers().get(PacketType.CHAT).size());
    }

    @Test
    @DisplayName("PacketAdapter registers handlers")
    void adapterHandlers() {
        PacketAdapter adapter = new PacketAdapter(null);
        adapter.onPacket(PacketType.SET_SLOT, (p, o) -> {});
        assertEquals(1, adapter.getHandlers().get(PacketType.SET_SLOT).size());
    }

    @Test
    @DisplayName("ProtocolVersion.of returns -1 for missing API")
    void protocolVersion() {
        assertEquals(-1, ProtocolVersion.of(null));
    }

    @Test
    @DisplayName("ProtocolVersion.isAtLeast")
    void isAtLeast() {
        assertFalse(ProtocolVersion.isAtLeast(null, 100));
    }

    @Test
    @DisplayName("ProxyDetector enum values")
    void proxyDetector() {
        assertNotNull(ProxyDetector.ProxyType.NONE);
        assertNotNull(ProxyDetector.ProxyType.BUNGEE);
        assertNotNull(ProxyDetector.ProxyType.VELOCITY);
    }

    @Test
    @DisplayName("PacketType enum values")
    void packetTypes() {
        assertNotNull(PacketType.CHAT);
        assertNotNull(PacketType.SPAWN_ENTITY);
        assertEquals(10, PacketType.values().length);
    }
}

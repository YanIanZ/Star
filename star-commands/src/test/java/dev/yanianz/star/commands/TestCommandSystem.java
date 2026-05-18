package dev.yanianz.star.commands;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.*;

@DisplayName("Command System")
class TestCommandSystem {

    @Test
    @DisplayName("ArgumentType parsers work")
    void argumentTypeParsers() {
        assertEquals(42, ArgumentType.INTEGER.parse("42"));
        assertEquals(3.14, ArgumentType.DOUBLE.parse("3.14"), 0.01);
        assertEquals("hello", ArgumentType.STRING.parse("hello"));
        assertTrue(ArgumentType.BOOLEAN.parse("true"));
        assertFalse(ArgumentType.BOOLEAN.parse("no"));
        assertNull(ArgumentType.INTEGER.parse("abc"));
    }

    @Test
    @DisplayName("ArgumentType enum works")
    void argumentTypeEnum() {
        ArgumentType<java.util.concurrent.TimeUnit> tuType = ArgumentType.ofEnum(java.util.concurrent.TimeUnit.class);
        assertEquals(java.util.concurrent.TimeUnit.SECONDS, tuType.parse("SECONDS"));
        assertNotNull(tuType.tabComplete());
        assertFalse(tuType.tabComplete().isEmpty());
    }

    @Test
    @DisplayName("ArgumentParser parses required args")
    void argumentParserRequired() {
        CommandContext ctx = new CommandContext(null, "test", new String[]{"42", "hello"});
        List<CommandNode.ArgDef> defs = List.of(
            new CommandNode.ArgDef("amount", ArgumentType.INTEGER, false),
            new CommandNode.ArgDef("message", ArgumentType.STRING, false)
        );
        Map<String, Object> result = ArgumentParser.parse(ctx, defs);
        assertEquals(42, result.get("amount"));
        assertEquals("hello", result.get("message"));
    }

    @Test
    @DisplayName("ArgumentParser throws on missing required arg")
    void argumentParserMissingRequired() {
        CommandContext ctx = new CommandContext(null, "test", new String[]{});
        List<CommandNode.ArgDef> defs = List.of(
            new CommandNode.ArgDef("amount", ArgumentType.INTEGER, false)
        );
        assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parse(ctx, defs));
    }

    @Test
    @DisplayName("CooldownManager tracks cooldowns")
    void cooldownManager() {
        CooldownManager cm = new CooldownManager();
        org.mockbukkit.mockbukkit.MockBukkit.mock();
        org.mockbukkit.mockbukkit.ServerMock server = org.mockbukkit.mockbukkit.MockBukkit.getOrCreateMock();
        org.mockbukkit.mockbukkit.entity.PlayerMock player = server.addPlayer();
        assertFalse(cm.isOnCooldown("fly", player));
        cm.setCooldown("fly", player, 5);
        assertTrue(cm.isOnCooldown("fly", player));
        assertTrue(cm.getRemaining("fly", player) > 0);
        cm.clear("fly");
        assertFalse(cm.isOnCooldown("fly", player));
        org.mockbukkit.mockbukkit.MockBukkit.unmock();
    }

    @Test
    @DisplayName("CommandContext convenience works")
    void commandContextBasics() {
        CommandContext ctx = new CommandContext(null, "fly", new String[]{"arg1", "arg2"});
        assertEquals("fly", ctx.label());
        assertEquals(2, ctx.argCount());
        assertEquals("arg1", ctx.arg(0));
        assertEquals("arg2", ctx.arg(1));
        assertNull(ctx.arg(5));
        ctx.set("key", "value");
        assertEquals("value", ctx.get("key"));
        assertEquals("fallback", ctx.getOrDefault("missing", "fallback"));
        assertTrue(ctx.has("key"));
        assertFalse(ctx.has("nope"));
    }

    @Test
    @DisplayName("CommandBuilder builds and validates")
    void commandBuilder() {
        CommandNode node = new CommandBuilder("test")
            .aliases("t")
            .permission("test.perm")
            .usage("/test <msg>")
            .description("A test command")
            .arg("msg", ArgumentType.STRING)
            .executor(ctx -> {})
            .build();

        assertEquals("test", node.getName());
        assertTrue(node.getAliases().contains("t"));
        assertEquals("test.perm", node.getPermission());
        assertEquals("/test <msg>", node.getUsage());
        assertEquals("A test command", node.getDescription());
        assertEquals(1, node.getArgs().size());
    }

    @Test
    @DisplayName("CommandBuilder throws without executor")
    void commandBuilderNoExecutor() {
        assertThrows(IllegalStateException.class, () ->
            new CommandBuilder("bad").arg("x", ArgumentType.STRING).build()
        );
    }
}

package dev.yanianz.star.commands.condition;

import dev.yanianz.star.commands.CommandContext;
import dev.yanianz.star.commands.CooldownManager;

public final class CooldownCondition implements CommandCondition {
    private final int seconds;
    private final CooldownManager mgr;

    public CooldownCondition(int seconds, CooldownManager mgr) {
        this.seconds = seconds;
        this.mgr = mgr;
    }

    @Override
    public boolean test(CommandContext ctx) {
        if (!ctx.isPlayer()) return true;
        if (mgr.isOnCooldown(ctx.label(), ctx.asPlayer())) return false;
        mgr.setCooldown(ctx.label(), ctx.asPlayer(), seconds);
        return true;
    }

    @Override
    public String getFailureMessage() {
        return "On cooldown.";
    }
}

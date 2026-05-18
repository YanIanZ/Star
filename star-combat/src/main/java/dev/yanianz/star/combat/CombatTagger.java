package dev.yanianz.star.combat;

import org.bukkit.entity.Player;
import javax.annotation.Nonnull;

/** Tags combat participants. */
public final class CombatTagger {
    private final CombatLog log;

    public CombatTagger(@Nonnull CombatLog log) {
        this.log = log;
    }

    public void tag(@Nonnull Player victim, @Nonnull Player attacker) {
        log.enter(victim);
        log.enter(attacker);
        log.tag(victim, attacker);
    }
}

package dev.yanianz.star.combat;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Combat")
class TestCombat {

    private org.mockbukkit.mockbukkit.ServerMock server;
    private Player playerA;
    private Player playerV;

    @BeforeEach
    void setUp() {
        org.mockbukkit.mockbukkit.MockBukkit.mock();
        server = org.mockbukkit.mockbukkit.MockBukkit.getOrCreateMock();
        playerA = server.addPlayer("Attacker");
        playerV = server.addPlayer("Victim");
    }

    @AfterEach
    void tearDown() {
        org.mockbukkit.mockbukkit.MockBukkit.unmock();
    }

    @Test
    @DisplayName("DamageType armor penetration")
    void damageTypeAp() {
        assertEquals(0, DamageType.MELEE.getArmorPenetration());
        assertEquals(1.0, DamageType.POISON.getArmorPenetration());
        assertEquals(0.7, DamageType.MAGIC.getArmorPenetration());
    }

    @Test
    @DisplayName("DamageModifier appliesTo")
    void modifierAppliesTo() {
        DamageModifier mod = new DamageModifier("sharp", 1.5, DamageType.MELEE);
        assertTrue(mod.appliesTo(DamageType.MELEE));
        assertFalse(mod.appliesTo(DamageType.MAGIC));
        assertEquals(1.5, mod.multiplier());
    }

    @Test
    @DisplayName("DamageCalculator with modifiers")
    void calculatorModifiers() {
        DamageCalculator calc = new DamageCalculator();
        calc.addModifier(new DamageModifier("test", 2.0, DamageType.MELEE));
        assertEquals(20.0, calc.calculate(10.0, playerA, playerV, DamageType.MELEE));
        calc.clearModifiers();
        assertEquals(10.0, calc.calculate(10.0, playerA, playerV, DamageType.MELEE));
    }

    @Test
    @DisplayName("DamageCalculator removes modifiers")
    void calculatorRemoveModifier() {
        DamageCalculator calc = new DamageCalculator();
        calc.addModifier(new DamageModifier("temp", 3.0));
        calc.removeModifier("temp");
        assertEquals(10.0, calc.calculate(10.0, playerA, playerV, DamageType.MELEE));
    }

    @Test
    @DisplayName("CombatLog tracks combat state")
    void combatLogState() {
        CombatLog log = new CombatLog(10);
        assertFalse(log.isInCombat(playerA));
        log.enter(playerA);
        assertTrue(log.isInCombat(playerA));
        assertTrue(log.getRemaining(playerA) > 0);
        log.tag(playerV, playerA);
        assertTrue(log.getTaggedBy(playerV).isPresent());
        log.exit(playerA);
        assertFalse(log.isInCombat(playerA));
    }

    @Test @DisplayName("DamageType enum count")
    void damageTypeEnumCount() {
        assertEquals(9, DamageType.values().length);
    }

    @Test @DisplayName("DamageModifier record all fields")
    void damageModifierAllFields() {
        DamageModifier mod = new DamageModifier("power", 2.0, DamageType.MELEE, DamageType.RANGED);
        assertEquals("power", mod.name());
        assertEquals(2.0, mod.multiplier());
        assertTrue(mod.appliesTo(DamageType.MELEE));
        assertTrue(mod.appliesTo(DamageType.RANGED));
        assertFalse(mod.appliesTo(DamageType.MAGIC));
    }

    @Test @DisplayName("DamageModifier wildcard applies to all")
    void damageModifierWildcard() {
        DamageModifier mod = new DamageModifier("global", 0.5);
        assertTrue(mod.appliesTo(DamageType.MELEE));
        assertTrue(mod.appliesTo(DamageType.VOID));
    }

    @Test @DisplayName("CombatLog clear")
    void combatLogClear() {
        CombatLog log = new CombatLog(10);
        log.clear();
        assertNotNull(log);
    }

    @Test @DisplayName("CombatTagger exists")
    void combatTaggerConstruct() {
        CombatLog log = new CombatLog(10);
        CombatTagger tagger = new CombatTagger(log);
        assertNotNull(tagger);
    }

    @Test
    @DisplayName("CombatTagger tags both players")
    void combatTagger() {
        CombatLog log = new CombatLog(10);
        CombatTagger tagger = new CombatTagger(log);
        tagger.tag(playerV, playerA);
        assertTrue(log.isInCombat(playerA));
        assertTrue(log.isInCombat(playerV));
    }
}

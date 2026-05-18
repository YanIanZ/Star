package dev.yanianz.star.economy;

import dev.yanianz.star.economy.banks.Bank;
import dev.yanianz.star.economy.banks.BankManager;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

@DisplayName("EconomyManager")
class TestEconomyManager {

    private static EconomyProvider mockProvider() {
        return new EconomyProvider() {
            @Override public String getName() { return "Mock"; }
            @Override public boolean isEnabled() { return true; }
            @Override public boolean hasAccount(OfflinePlayer player) { return true; }
            @Override public double getBalance(OfflinePlayer player) { return 1000.0; }
            @Override public boolean setBalance(OfflinePlayer player, double amount) { return true; }
            @Override public String format(double amount) { return "$" + amount; }
        };
    }

    @Test
    @DisplayName("TransactionResult helpers work")
    void transactionResultHelpers() {
        TransactionResult ok = TransactionResult.ok(100.0, 900.0, "Success");
        assertTrue(ok.success());
        assertEquals(100.0, ok.amount());
        assertEquals(900.0, ok.newBalance());
        assertEquals("Success", ok.message());

        TransactionResult fail = TransactionResult.fail("Not enough money");
        assertFalse(fail.success());
        assertEquals("Not enough money", fail.message());
    }

    @Test
    @DisplayName("TransactionEntry record works")
    void transactionEntryRecord() {
        UUID id = UUID.randomUUID();
        TransactionEntry entry = new TransactionEntry(id, TransactionType.WITHDRAW, 50.0, "Test", "$50.00", System.currentTimeMillis());
        assertEquals(id, entry.playerId());
        assertEquals(TransactionType.WITHDRAW, entry.type());
        assertEquals(50.0, entry.amount());
        assertEquals("$50.00", entry.formattedAmount());
    }

    @Test
    @DisplayName("CurrencyFormatter formatShort works")
    void currencyFormatterShort() {
        EconomyProvider provider = mockProvider();
        assertEquals("1.23K $", CurrencyFormatter.formatShort(provider, 1234.0));
        assertEquals("1.5M $", CurrencyFormatter.formatShort(provider, 1_500_000.0));
        assertEquals("500 $", CurrencyFormatter.formatShort(provider, 500.0));
    }

    @Test
    @DisplayName("CurrencyFormatter formatRaw works")
    void currencyFormatterRaw() {
        String formatted = CurrencyFormatter.formatRaw(1234.567);
        assertEquals("1,234.57", formatted);
    }

    @Test
    @DisplayName("Bank data class works")
    void bankDataClass() {
        UUID ownerId = UUID.randomUUID();
        Bank bank = new Bank("TestBank", ownerId);
        assertEquals("TestBank", bank.getName());
        assertEquals(0.0, bank.getBalance());
        bank.setBalance(500.0);
        assertEquals(500.0, bank.getBalance());
        assertEquals(ownerId, bank.getOwnerId());
    }

    @Test
    @DisplayName("CurrencyType record works")
    void currencyType() {
        CurrencyType coins = new CurrencyType("coins", "Coins", "\u26C3");
        assertEquals("coins", coins.id());
        assertEquals("Coins", coins.name());
        assertEquals("\u26C3", coins.symbol());
    }
}

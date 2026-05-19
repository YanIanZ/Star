package dev.yanianz.star.economy;

import dev.yanianz.star.economy.banks.Bank;
import dev.yanianz.star.economy.banks.BankManager;
import dev.yanianz.star.economy.providers.AbstractEconomyProvider;
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

    @Test @DisplayName("EconomyProvider default methods work")
    void economyProviderDefaults() {
        EconomyProvider provider = new AbstractEconomyProvider() {
            @Override public String getName() { return "test"; }
            @Override public boolean hasAccount(OfflinePlayer p) { return true; }
            @Override public double getBalance(OfflinePlayer p) { return 100.0; }
            @Override public boolean setBalance(OfflinePlayer p, double amount) { return true; }
        };

        assertFalse(provider.isEnabled());
        assertEquals("test", provider.getName());
        assertEquals("Dollar", provider.currencyNameSingular());
        assertEquals("Dollars", provider.currencyNamePlural());
        assertEquals("$", provider.currencySymbol());
        assertFalse(provider.hasBankSupport());
        assertTrue(provider.getBanks().isEmpty());
        assertTrue(provider.getCurrencies().isEmpty());
        assertEquals(0.0, provider.getBankBalance("any"));
    }

    @Test @DisplayName("EconomyProvider custom currency defaults")
    void economyProviderCustomCurrency() {
        EconomyProvider provider = new AbstractEconomyProvider() {
            @Override public String getName() { return "custom"; }
            @Override public boolean hasAccount(OfflinePlayer p) { return true; }
            @Override public double getBalance(OfflinePlayer p) { return 50.0; }
            @Override public boolean setBalance(OfflinePlayer p, double amount) { return true; }
        };
        CurrencyType coins = new CurrencyType("coins", "Coins", "C");
        assertEquals(50.0, provider.getBalance(null, coins));
    }

    @Test @DisplayName("BankManager delegates correctly")
    void bankManagerDelegation() {
        EconomyProvider provider = new AbstractEconomyProvider() {
            @Override public String getName() { return "banktest"; }
            @Override public boolean hasAccount(OfflinePlayer p) { return true; }
            @Override public double getBalance(OfflinePlayer p) { return 0; }
            @Override public boolean setBalance(OfflinePlayer p, double amount) { return true; }
        };
        BankManager bm = new BankManager(provider);

        TransactionResult create = bm.create("MyBank", null);
        assertFalse(create.success());
        assertEquals("Bank not supported", create.message());

        assertEquals(0.0, bm.getBalance("MyBank"));
        assertTrue(bm.getBanks().isEmpty());

        TransactionResult deposit = bm.deposit("MyBank", 100);
        assertFalse(deposit.success());
    }

    @Test @DisplayName("Bank members work")
    void bankMembers() {
        UUID ownerId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Bank bank = new Bank("Test", ownerId);
        assertTrue(bank.getMemberIds().isEmpty());
        bank.addMember(memberId);
        assertEquals(1, bank.getMemberIds().size());
        assertTrue(bank.getMemberIds().contains(memberId));
        bank.removeMember(memberId);
        assertTrue(bank.getMemberIds().isEmpty());
        assertFalse(bank.getMemberIds().contains(memberId));
    }

    @Test @DisplayName("TransactionResult ok/fail factory methods")
    void transactionResultFactories() {
        TransactionResult ok = TransactionResult.ok(500.0, 4500.0, "Deposited");
        assertTrue(ok.success());
        assertEquals(500.0, ok.amount());
        assertEquals(4500.0, ok.newBalance());
        assertEquals("Deposited", ok.message());

        TransactionResult fail = TransactionResult.fail("Insufficient funds");
        assertFalse(fail.success());
        assertEquals(0.0, fail.amount());

        TransactionResult okNoMsg = TransactionResult.ok(100.0, 900.0);
        assertEquals("", okNoMsg.message());
    }

    @Test @DisplayName("TransactionEntry all fields")
    void transactionEntryFields() {
        UUID id = UUID.randomUUID();
        long ts = System.currentTimeMillis();
        TransactionEntry entry = new TransactionEntry(id, TransactionType.DEPOSIT, 250.0, "Salary", "$250.00", ts);
        assertEquals(id, entry.playerId());
        assertEquals(TransactionType.DEPOSIT, entry.type());
        assertEquals(250.0, entry.amount());
        assertEquals("Salary", entry.reason());
        assertEquals("$250.00", entry.formattedAmount());
        assertEquals(ts, entry.timestamp());
    }

    @Test @DisplayName("CurrencyFormatter format delegates to provider")
    void currencyFormatterDelegation() {
        EconomyProvider p = new AbstractEconomyProvider() {
            @Override public String getName() { return "test"; }
            @Override public boolean hasAccount(OfflinePlayer pl) { return true; }
            @Override public double getBalance(OfflinePlayer pl) { return 0; }
            @Override public boolean setBalance(OfflinePlayer pl, double a) { return true; }
        };
        String formatted = CurrencyFormatter.format(p, 1500.50);
        assertNotNull(formatted);
    }

    @Test @DisplayName("TransactionType enum values")
    void transactionTypeEnum() {
        assertEquals(3, TransactionType.values().length);
        assertNotNull(TransactionType.DEPOSIT);
        assertNotNull(TransactionType.WITHDRAW);
        assertNotNull(TransactionType.SET);
    }

    @Test @DisplayName("Bank isOwner and isMember work")
    void bankOwnerMember() {
        org.mockbukkit.mockbukkit.MockBukkit.mock();
        org.mockbukkit.mockbukkit.ServerMock server = org.mockbukkit.mockbukkit.MockBukkit.getOrCreateMock();
        org.mockbukkit.mockbukkit.entity.PlayerMock owner = server.addPlayer("Owner");
        org.mockbukkit.mockbukkit.entity.PlayerMock member = server.addPlayer("Member");

        Bank bank = new Bank("CoolBank", owner.getUniqueId());
        bank.addMember(member.getUniqueId());
        assertTrue(bank.isOwner(owner));
        assertFalse(bank.isOwner(member));
        assertFalse(bank.isMember(owner));
        assertTrue(bank.isMember(member));
    }
}

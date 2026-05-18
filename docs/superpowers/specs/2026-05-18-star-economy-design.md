# Design: star-economy — Economy Integration Module

**Date:** 2026-05-18
**Author:** rheninxy
**Project:** Star (dev.yanianz:star)

---

## 1. Motivation

Unified economy API that abstracts multiple backends (Vault, EssentialsX, CMI) behind a single `EconomyProvider` interface. Includes bank support, transaction history, multi-currency, offline player support, and locale-aware formatting.

## 2. Architecture

```
star-economy/
  build.gradle.kts
  src/main/java/dev/yanianz/star/economy/
    EconomyProvider.java          # Provider interface
    EconomyManager.java           # Auto-detect and register providers
    CurrencyFormatter.java        # Locale-aware formatting
    TransactionResult.java        # Result type (success/fail)
    TransactionEntry.java         # Transaction record
    TransactionType.java          # WITHDRAW/DEPOSIT/SET
    EconomyPlayer.java            # Wrapper with balance cache
    CurrencyType.java             # Multi-currency support
    providers/
      AbstractEconomyProvider.java # Abstract base with defaults
      VaultEconomyProvider.java    # Vault backend
      EssentialsProvider.java      # EssentialsX backend
      CMIEconomyProvider.java      # CMI backend
    banks/
      Bank.java                    # Bank data class
      BankManager.java             # Bank operations
```

## 3. Dependencies

- `star-common` — StarLogger, Validate
- Vault API (compileOnly)
- EssentialsX API (compileOnly)
- CMI API (compileOnly, optional)
- Paper API

## 4. API Design

### EconomyProvider Interface

Core operations + bank operations + multi-currency:

```java
public interface EconomyProvider {
    String getName();
    boolean isEnabled();
    boolean hasAccount(OfflinePlayer player);
    double getBalance(OfflinePlayer player);
    TransactionResult withdraw(OfflinePlayer player, double amount);
    TransactionResult deposit(OfflinePlayer player, double amount);
    boolean canAfford(OfflinePlayer player, double amount);
    boolean setBalance(OfflinePlayer player, double amount);
    String format(double amount);
    String currencyNameSingular();
    String currencyNamePlural();
    String currencySymbol();
    boolean hasBankSupport();
    boolean createBank(String name, OfflinePlayer owner);
    double getBankBalance(String name);
    TransactionResult bankDeposit(String name, double amount);
    TransactionResult bankWithdraw(String name, double amount);
    boolean isBankOwner(String name, OfflinePlayer player);
    boolean isBankMember(String name, OfflinePlayer player);
    boolean deleteBank(String name);
    List<Bank> getBanks();
    List<CurrencyType> getCurrencies();
    double getBalance(OfflinePlayer player, CurrencyType currency);
}
```

### EconomyManager

Auto-detection of available providers:
```java
EconomyManager manager = new EconomyManager(plugin);
manager.registerProvider(new VaultEconomyProvider());
manager.registerProvider(new EssentialsProvider());
manager.registerProvider(new CMIEconomyProvider());
manager.detectAndEnable();
EconomyProvider primary = manager.getPrimaryProvider();
```

### TransactionResult

```java
public record TransactionResult(boolean success, double amount, double balance, String message) {
    public static TransactionResult ok(double amount, double balance) { ... }
    public static TransactionResult fail(String message) { ... }
}
```

### CurrencyFormatter

Locale-aware formatting using NumberFormat:
```java
CurrencyFormatter.format(provider, 1500.50) // "$1,500.50" or "1.500,50 €"
```

### BankManager

```java
BankManager banks = manager.getBankManager();
banks.create("BankName", owner);
banks.getBalance("BankName");
banks.deposit("BankName", 500.0);
banks.withdraw("BankName", 100.0);
```

## 5. Error Handling

- `EconomyProvider` methods return `TransactionResult` (never throw for balance operations)
- Invalid amounts (negative) throw `IllegalArgumentException`
- Missing provider throws `IllegalStateException` on getPrimaryProvider()

## 6. Testing

- Unit tests for CurrencyFormatter
- Integration tests with MockBukkit for EconomyManager provider detection
- Mock EconomyProvider for testing TransactionResult flows

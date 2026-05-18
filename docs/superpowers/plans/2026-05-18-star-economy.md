# star-economy Implementation Plan

> **For agentic workers:** Use subagent-driven-development.

**Goal:** Build unified economy abstraction module with Vault/EssentialsX/CMI backends, bank support, transactions, and formatting.

**Architecture:** EconomyProvider interface → provider implementations → EconomyManager for auto-detection.

**Tech Stack:** Java 25, Paper API 1.21.11, Vault API (compileOnly), JUnit 5, MockBukkit

---

### Task 1: Module scaffolding + core types

**Files:** Create `star-economy/build.gradle.kts`, `settings.gradle.kts`(+star-economy), `star-api/build.gradle.kts`(+star-economy), `TransactionType.java`, `TransactionResult.java`, `TransactionEntry.java`, `CurrencyType.java`, `EconomyProvider.java`

- [ ] Create directories: `star-economy/src/main/java/dev/yanianz/star/economy/providers/`, `star-economy/src/main/java/dev/yanianz/star/economy/banks/`
- [ ] Write `star-economy/build.gradle.kts`:
```kotlin
dependencies {
    compileOnly(project(":star-common"))
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}
```
- [ ] Register in settings.gradle.kts and star-api aggregator
- [ ] Create enums/interfaces: TransactionType (DEPOSIT/WITHDRAW/SET), TransactionResult record, TransactionEntry record, CurrencyType record, EconomyProvider interface

### Task 2: Provider implementations

**Files:** Create `AbstractEconomyProvider.java`, `VaultEconomyProvider.java`

- [ ] AbstractEconomyProvider: base class with no-ops for bank/currency methods
- [ ] VaultEconomyProvider: wraps Vault net.milkbowl.vault.economy.Economy

### Task 3: EconomyManager + CurrencyFormatter + BankManager

**Files:** Create `EconomyManager.java`, `CurrencyFormatter.java`, `Bank.java`, `BankManager.java`

- [ ] EconomyManager: register providers, detectAndEnable(), getPrimaryProvider()
- [ ] CurrencyFormatter: static format(provider, amount)
- [ ] Bank + BankManager: bank create/delete/balance/members

### Task 4: Tests + build verification

**Files:** Create `star-economy/src/test/.../TestEconomyManager.java`

- [ ] Test provider registration/detection
- [ ] Test CurrencyFormatter output
- [ ] Test TransactionResult helpers
- [ ] Full compile + test + shadowJar

---

Execute this plan directly — create all files, verify build, commit, push. No need for step-by-step approval.

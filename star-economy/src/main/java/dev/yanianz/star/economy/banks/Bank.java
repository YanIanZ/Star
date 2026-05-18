package dev.yanianz.star.economy.banks;

import org.bukkit.OfflinePlayer;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Bank {
    private final String name;
    private double balance;
    private UUID ownerId;
    private final List<UUID> memberIds = new ArrayList<>();

    public Bank(@Nonnull String name, @Nonnull UUID ownerId) {
        this.name = name;
        this.ownerId = ownerId;
    }

    @Nonnull public String getName() { return name; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    @Nonnull public UUID getOwnerId() { return ownerId; }
    @Nonnull public List<UUID> getMemberIds() { return memberIds; }
    public void addMember(@Nonnull UUID id) { if (!memberIds.contains(id)) memberIds.add(id); }
    public void removeMember(@Nonnull UUID id) { memberIds.remove(id); }
    public boolean isOwner(@Nonnull OfflinePlayer player) { return player.getUniqueId().equals(ownerId); }
    public boolean isMember(@Nonnull OfflinePlayer player) { return memberIds.contains(player.getUniqueId()); }
}

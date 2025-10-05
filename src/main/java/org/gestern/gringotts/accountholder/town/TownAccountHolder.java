package org.gestern.gringotts.accountholder.town;

import com.palmergames.bukkit.towny.object.Town;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** Gringotts AccountHolder wrapper for a Towny Town. */
public final class TownAccountHolder implements AccountHolder {

    public static final String ACCOUNT_TYPE = "town";

    private final Town town;

    public TownAccountHolder(@NotNull Town town) {
        this.town = town;
    }

    @Override
    public @NotNull String getName() {
        return town.getName();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        // No-op by default. If you want broadcast, wire TownyMessaging here.
        // Example (optional):
        // TownyMessaging.sendPrefixedTownMessage(town, message);
    }

    @Override
    public @NotNull String getType() {
        return ACCOUNT_TYPE;
    }

    @Override
    public @NotNull String getId() {
        // Use UUID string to match what Towny passes to Vault
        return town.getUUID().toString();
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        // Towns do not have a single permission-bearing owner
        return true;
    }

    public @NotNull Town getTown() { return town; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TownAccountHolder)) return false;
        TownAccountHolder that = (TownAccountHolder) o;
        return Objects.equals(town.getUUID(), that.town.getUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(town.getUUID());
    }
}

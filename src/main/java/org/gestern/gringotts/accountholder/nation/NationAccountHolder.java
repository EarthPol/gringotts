package org.gestern.gringotts.accountholder.nation;

import com.palmergames.bukkit.towny.object.Nation;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** Gringotts AccountHolder wrapper for a Towny Nation. */
public final class NationAccountHolder implements AccountHolder {

    public static final String ACCOUNT_TYPE = "nation";

    private final Nation nation;

    public NationAccountHolder(@NotNull Nation nation) {
        this.nation = nation;
    }

    @Override
    public @NotNull String getName() {
        return nation.getName();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        // No-op by default. If you want broadcast, wire TownyMessaging here.
        // Example (optional):
        // TownyMessaging.sendPrefixedNationMessage(nation, message);
    }

    @Override
    public @NotNull String getType() {
        return ACCOUNT_TYPE;
    }

    @Override
    public @NotNull String getId() {
        return nation.getUUID().toString();
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true;
    }

    public @NotNull Nation getNation() { return nation; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NationAccountHolder)) return false;
        NationAccountHolder that = (NationAccountHolder) o;
        return Objects.equals(nation.getUUID(), that.nation.getUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nation.getUUID());
    }
}

package org.gestern.gringotts.accountholder.town;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.OfflinePlayer;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class TownHolderProvider implements AccountHolderProvider {

    @Override
    public @NotNull String getType() {
        return "town";
    }

    /** Resolve by String (UUID string or town name). */
    @Override
    public @Nullable AccountHolder getAccountHolder(@NotNull String id) {
        // Try UUID first
        UUID uid = tryUUID(id);
        if (uid != null) {
            return getAccountHolder(uid);
        }
        // Fallback to name
        Town t = TownyUniverse.getInstance().getTown(id);
        return t == null ? null : new TownAccountHolder(t);
    }

    /** Resolve by UUID (Town UUID). */
    @Override
    public @Nullable AccountHolder getAccountHolder(@NotNull UUID uuid) {
        Town t = TownyUniverse.getInstance().getTown(uuid);
        return t == null ? null : new TownAccountHolder(t);
    }

    /** Not applicable for towns; always null. */
    @Override
    public @Nullable AccountHolder getAccountHolder(@NotNull OfflinePlayer player) {
        return null;
    }

    @Override
    public @NotNull Set<String> getAccountNames() {
        return TownyUniverse.getInstance().getTowns()
                .stream()
                .map(Town::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static @Nullable UUID tryUUID(String s) {
        try { return UUID.fromString(s); } catch (IllegalArgumentException e) { return null; }
    }
}

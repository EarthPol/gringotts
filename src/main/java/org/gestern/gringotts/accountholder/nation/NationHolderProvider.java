package org.gestern.gringotts.accountholder.nation;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.OfflinePlayer;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class NationHolderProvider implements AccountHolderProvider {

    @Override
    public @NotNull String getType() {
        return "nation";
    }

    /** Resolve by String (UUID string or nation name). */
    @Override
    public @Nullable AccountHolder getAccountHolder(@NotNull String id) {
        UUID uid = tryUUID(id);
        if (uid != null) {
            return getAccountHolder(uid);
        }
        Nation n = TownyUniverse.getInstance().getNation(id);
        return n == null ? null : new NationAccountHolder(n);
    }

    /** Resolve by UUID (Nation UUID). */
    @Override
    public @Nullable AccountHolder getAccountHolder(@NotNull UUID uuid) {
        Nation n = TownyUniverse.getInstance().getNation(uuid);
        return n == null ? null : new NationAccountHolder(n);
    }

    /** Not applicable for nations; always null. */
    @Override
    public @Nullable AccountHolder getAccountHolder(@NotNull OfflinePlayer player) {
        return null;
    }

    @Override
    public @NotNull Set<String> getAccountNames() {
        return TownyUniverse.getInstance().getNations()
                .stream()
                .map(Nation::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static @Nullable UUID tryUUID(String s) {
        try { return UUID.fromString(s); } catch (IllegalArgumentException e) { return null; }
    }
}

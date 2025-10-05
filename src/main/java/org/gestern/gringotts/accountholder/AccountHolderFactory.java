package org.gestern.gringotts.accountholder;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.gestern.gringotts.accountholder.nation.NationHolderProvider;
import org.gestern.gringotts.accountholder.town.TownHolderProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Resolves AccountHolder objects by id and type using registered providers.
 * Order matters: player first, then town, then nation.
 */
public class AccountHolderFactory {

    private final LinkedHashMap<String, AccountHolderProvider> accountHolderProviders = new LinkedHashMap<>();

    public AccountHolderFactory() {
        // linked HashMap maintains iteration order -> prefer player to be checked first
        accountHolderProviders.put("player", new PlayerAccountHolderProvider());

        // Town/Nation providers are added lazily once Towny is actually present & enabled.
        // (We also attempt to add them immediately if Towny is already enabled.)
        ensureTownyProvidersRegistered();
    }


    public Optional<AccountHolderProvider> getProvider(String type) {
        ensureTownyProvidersRegistered(); // make sure Towny providers exist if Towny is now enabled
        if (type == null) return Optional.empty();
        return Optional.ofNullable(this.accountHolderProviders.get(type.toLowerCase(java.util.Locale.ROOT)));
    }


    public boolean hasProvider(@Nullable String type) {
        return getProvider(type).isPresent();
    }

    /** All registered type keys in priority order (player → town → nation). */
    public @NotNull List<String> getTypes() {
        return new ArrayList<>(accountHolderProviders.keySet());
    }

    /** Resolve holder by opaque id (UUID string, name, etc.). */
    public @Nullable AccountHolder get(@NotNull String id) {
        ensureTownyProvidersRegistered();
        for (AccountHolderProvider provider : accountHolderProviders.values()) {
            AccountHolder h = provider.getAccountHolder(id);
            if (h != null) return h;
        }
        return null;
    }

    /** Resolve holder by explicit type and id. */
    public @Nullable AccountHolder get(@NotNull String type, @NotNull String id) {
        ensureTownyProvidersRegistered();
        AccountHolderProvider p = accountHolderProviders.get(type.toLowerCase(Locale.ROOT));
        return p == null ? null : p.getAccountHolder(id);
    }

    /** Reverse map to a known type key. */
    public @NotNull String getType(@NotNull AccountHolder holder) {
        ensureTownyProvidersRegistered();
        String n = holder.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        if (n.contains("town"))   return "town";
        if (n.contains("nation")) return "nation";
        return "player";
    }

    /** List all known account names for a type. */
    public @NotNull Set<String> getAccountNames(@NotNull String type) {
        ensureTownyProvidersRegistered();
        AccountHolderProvider p = accountHolderProviders.get(type.toLowerCase(Locale.ROOT));
        return p == null ? Collections.emptySet() : p.getAccountNames();
    }

    private void ensureTownyProvidersRegistered() {
        var pm = org.bukkit.Bukkit.getPluginManager();
        var towny = pm.getPlugin("Towny");
        if (towny != null && towny.isEnabled()) {
            // register once if missing
            accountHolderProviders.computeIfAbsent("town",   k -> new org.gestern.gringotts.accountholder.town.TownHolderProvider());
            accountHolderProviders.computeIfAbsent("nation", k -> new org.gestern.gringotts.accountholder.nation.NationHolderProvider());
        }
    }

    /** Player provider (implements String, UUID, OfflinePlayer forms). */
    static final class PlayerAccountHolderProvider implements AccountHolderProvider {

        @Override
        public @NotNull String getType() { return "player"; }

        @Override
        public @Nullable AccountHolder getAccountHolder(@NotNull String id) {
            UUID uuid = tryUUID(id);
            if (uuid != null) return getAccountHolder(uuid);

            OfflinePlayer player = Bukkit.getOfflinePlayer(id);
            if (player != null && (player.hasPlayedBefore() || player.getName() != null)) {
                return new PlayerAccountHolder(player);
            }
            return null;
        }

        @Override
        public @Nullable AccountHolder getAccountHolder(@NotNull UUID uuid) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (player != null && (player.hasPlayedBefore() || player.getName() != null)) {
                return new PlayerAccountHolder(player);
            }
            return null;
        }

        @Override
        public @Nullable AccountHolder getAccountHolder(@NotNull OfflinePlayer player) {
            if (player.getName() != null || player.hasPlayedBefore()) {
                return new PlayerAccountHolder(player);
            }
            return null;
        }

        @Override
        public @NotNull Set<String> getAccountNames() {
            return Stream.of(Bukkit.getOfflinePlayers())
                    .map(OfflinePlayer::getName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
        }
    }

    private static @Nullable UUID tryUUID(String s) {
        try { return UUID.fromString(s); } catch (IllegalArgumentException e) { return null; }
    }
}

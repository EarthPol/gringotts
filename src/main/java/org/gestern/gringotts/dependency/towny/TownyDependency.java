package org.gestern.gringotts.dependency.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.api.dependency.Dependency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TownyDependency implements Dependency {

    private final Plugin plugin;

    public TownyDependency(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getId() {
        return "towny";
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        Gringotts.instance.getAccountHolderFactory().registerAccountHolderProvider("town", new TownAccountHolderProvider());
        Gringotts.instance.getAccountHolderFactory().registerAccountHolderProvider("nation", new NationAccountHolderProvider());
    }

    private static final class TownAccountHolderProvider implements AccountHolderProvider {

        @Override
        public @Nullable AccountHolder getAccountHolder(@NotNull String id) {
            Town town = TownyAPI.getInstance().getTown(id);
            return town == null ? null : new GroupAccountHolder("town", town.getName(), town);
        }

        @Override
        public @Nullable AccountHolder getAccountHolder(@NotNull UUID uuid) {
            return getAccountHolder(Bukkit.getOfflinePlayer(uuid));
        }

        @Override
        public @Nullable AccountHolder getAccountHolder(@NotNull OfflinePlayer player) {
            Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());
            if (resident == null) {
                return null;
            }

            Town town = resident.getTownOrNull();
            if (town == null) {
                return null;
            }

            return getAccountHolder(town.getName());
        }

        @Override
        public @NotNull String getType() {
            return "town";
        }

        @Override
        public @NotNull Set<String> getAccountNames() {
            return TownyAPI.getInstance().getTowns().stream().map(Town::getName).collect(Collectors.toSet());
        }
    }

    private static final class NationAccountHolderProvider implements AccountHolderProvider {

        @Override
        public @Nullable AccountHolder getAccountHolder(@NotNull String id) {
            Nation nation = TownyAPI.getInstance().getNation(id);
            return nation == null ? null : new GroupAccountHolder("nation", nation.getName(), nation);
        }

        @Override
        public @Nullable AccountHolder getAccountHolder(@NotNull UUID uuid) {
            return getAccountHolder(Bukkit.getOfflinePlayer(uuid));
        }

        @Override
        public @Nullable AccountHolder getAccountHolder(@NotNull OfflinePlayer player) {
            Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());
            if (resident == null) {
                return null;
            }

            Town town = resident.getTownOrNull();
            if (town == null) {
                return null;
            }

            Nation nation = town.getNationOrNull();
            if (nation == null) {
                return null;
            }

            return getAccountHolder(nation.getName());
        }

        @Override
        public @NotNull String getType() {
            return "nation";
        }

        @Override
        public @NotNull Set<String> getAccountNames() {
            return TownyAPI.getInstance().getNations().stream().map(Nation::getName).collect(Collectors.toSet());
        }
    }

    private static final class GroupAccountHolder implements AccountHolder {

        private final String type;
        private final String name;
        private final CommandSender sender;

        private GroupAccountHolder(String type, String name, CommandSender sender) {
            this.type = type;
            this.name = name;
            this.sender = sender;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void sendMessage(String message) {
            sender.sendMessage(message);
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public String getId() {
            return name;
        }

        @Override
        public boolean hasPermission(String permission) {
            if (sender instanceof Player player) {
                return player.hasPermission(permission);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof GroupAccountHolder)) {
                return false;
            }

            GroupAccountHolder that = (GroupAccountHolder) other;
            return this.type.equals(that.type) && this.name.equals(that.name);
        }
    }
}

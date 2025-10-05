package org.gestern.gringotts.api.impl;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.OfflinePlayer;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.Util;
import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.Eco;
import org.gestern.gringotts.api.TransactionResult;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;
import static org.gestern.gringotts.Language.LANG;

/**
 * Provides the vault interface, so that the economy adapter in vault does not need to be changed.
 *
 * @author jast
 */
public class VaultConnector implements Economy {
    private final Eco eco = Gringotts.instance.getEco();

    @Override
    public boolean isEnabled() {
        return Gringotts.instance != null && Gringotts.instance.isEnabled();
    }

    @Override
    public String getName() {
        return Gringotts.instance.getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return eco.currency().getFractionalDigits();
    }

    @Override
    public String format(double amount) {
        return eco.currency().format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return eco.currency().getNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return eco.currency().getName();
    }

    @Override
    public boolean hasAccount(String accountId) {
        return eco.getAccount(accountId).exists();
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return eco.player(offlinePlayer.getUniqueId()).exists();
    }

    @Override
    public double getBalance(String accountId) { // TODO optimize
        return eco.getAccount(accountId).balance();
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return eco.player(offlinePlayer.getUniqueId()).balance();
    }

    @Override
    public boolean has(String accountId, double amount) {
        return eco.getAccount(accountId).has(amount);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        return eco.account(offlinePlayer.getUniqueId().toString()).has(amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String accountId, double amount) {
        getLogger().info("[Vault] #1 withdrawPlayer player=" + accountId + " amount=" + amount);
        return withdrawPlayer(eco.getAccount(accountId), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        getLogger().info("[Vault] #2 withdrawPlayer player=" + offlinePlayer.getName() + " amount=" + amount);
        Account account = eco.player(offlinePlayer.getUniqueId());
        return withdrawPlayer(account, amount);
    }

    private EconomyResponse withdrawPlayer(Account account, double amount) {
        TransactionResult removed = account.remove(amount);
        getLogger().info("[Vault] #3 withdrawPlayer player=" + account.id() + " amount=" + amount);
        switch (removed) {
            case SUCCESS:
                return new EconomyResponse(amount, account.balance(), ResponseType.SUCCESS, null);
            case INSUFFICIENT_FUNDS:
                return new EconomyResponse(0, account.balance(), ResponseType.FAILURE, LANG
                        .plugin_vault_insufficientFunds);
            case ERROR:
            default:
                return new EconomyResponse(0, account.balance(), ResponseType.FAILURE, LANG.plugin_vault_error);
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Account account = eco.getAccount(playerName);
        getLogger().info("[Vault] #1 depositPlayer player=" + account.id() + " amount=" + amount);
        return depositPlayer(account, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        Account account = eco.player(offlinePlayer.getUniqueId());
        getLogger().info("[Vault] #2 depositPlayer player=" + account.id() + " amount=" + amount);
        return depositPlayer(account, amount);
    }

    private EconomyResponse depositPlayer(Account account, double amount) {
        TransactionResult added = account.add(amount);

        getLogger().info("[Vault] #3 depositPlayer player=" + account.id() + " amount=" + amount);

        switch (added) {
            case SUCCESS:
                return new EconomyResponse(amount, account.balance(), ResponseType.SUCCESS, null);
            case INSUFFICIENT_SPACE:
                return new EconomyResponse(0, account.balance(), ResponseType.FAILURE, LANG
                        .plugin_vault_insufficientSpace);
            case ERROR:
            default:
                return new EconomyResponse(0, account.balance(), ResponseType.FAILURE, LANG.plugin_vault_error);
        }
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, LANG.plugin_vault_notImplemented);
        //        BankAccount bank = eco.bank(name).addOwner(player);
        //        if (bank.exists())
        //        	return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to create bank!");
        //        else
        //        	return new EconomyResponse(0, 0, ResponseType.SUCCESS, "Created bank " + name);
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, LANG.plugin_vault_notImplemented);
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, LANG.plugin_vault_notImplemented);
        //    	Account deleted = eco.bank(name).delete();
        //    	if (deleted.exists())
        //    		return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to delete bank account!");
        //    	else
        //    		return new EconomyResponse(0, 0, ResponseType.SUCCESS, "Deleted bank account (or it didn't
        // exist)");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, LANG.plugin_vault_notImplemented);
        //    	double balance = eco.bank(name).balance();
        //        return new EconomyResponse(0, balance,
        //        		ResponseType.SUCCESS, "Balance of bank "+ name +": "+ balance);
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, LANG.plugin_vault_notImplemented);
        //    	BankAccount bank = eco.bank(name);
        //    	double balance = bank.balance();
        //    	if (bank.has(amount))
        //    		return new EconomyResponse(0, balance, ResponseType.SUCCESS, "Bank " + name + " has at least " +
        // amount );
        //    	else
        //    		return new EconomyResponse(0, balance, ResponseType.FAILURE, "Bank " + name + " does not have at
        // least " + amount );
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Negative amount");
        }

        try {
            // Resolve the bank account by the name Towny passes in
            Account bank = eco.getAccount(name); // or: BankAccount bank = eco.bank(name);
            if (bank == null || !bank.exists()) {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Unknown bank: " + name);
            }

            // Attempt the withdrawal
            TransactionResult result = bank.remove(amount);

            switch (result) {
                case SUCCESS:
                    // amount withdrawn, return the new bank balance
                    return new EconomyResponse(amount, bank.balance(), EconomyResponse.ResponseType.SUCCESS, null);

                case INSUFFICIENT_FUNDS:
                    return new EconomyResponse(0, bank.balance(), EconomyResponse.ResponseType.FAILURE, "INSUFFICIENT_FUNDS");

                case UNSUPPORTED:
                    return new EconomyResponse(0, bank.balance(), EconomyResponse.ResponseType.NOT_IMPLEMENTED, "UNSUPPORTED");

                case INSUFFICIENT_SPACE:
                    // Rare for withdrawals, but handle generically
                    return new EconomyResponse(0, bank.balance(), EconomyResponse.ResponseType.FAILURE, "INSUFFICIENT_SPACE");

                default:
                    return new EconomyResponse(0, bank.balance(), EconomyResponse.ResponseType.FAILURE, "ERROR");
            }
        } catch (Exception e) {
            getLogger().warning("[VaultConnector] bankWithdraw error for '" + name + "': " + e.getMessage());
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Exception");
        }
    }


    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Negative amount");
        }

        try {
            // Resolve the target bank account. Use whichever your API provides:
            // Option A (if present): BankAccount bank = eco.bank(name);
            // Option B: resolve generic account id:
            Account bank = eco.getAccount(name);
            if (bank == null || !bank.exists()) {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Unknown bank: " + name);
            }

            // Perform the deposit using your economy API
            TransactionResult result = bank.add(amount);

            switch (result) {
                case SUCCESS:
                    return new EconomyResponse(amount, bank.balance(), EconomyResponse.ResponseType.SUCCESS, null);

                case INSUFFICIENT_SPACE:
                    // tell Towny the real cause; it may still show a generic message, but this helps logging
                    return new EconomyResponse(0, bank.balance(), EconomyResponse.ResponseType.FAILURE, "INSUFFICIENT_SPACE");

                case UNSUPPORTED:
                    return new EconomyResponse(0, bank.balance(), EconomyResponse.ResponseType.NOT_IMPLEMENTED, "UNSUPPORTED");

                case INSUFFICIENT_FUNDS:
                    // rare for deposits, but handle anyway
                    return new EconomyResponse(0, bank.balance(), EconomyResponse.ResponseType.FAILURE, "INSUFFICIENT_FUNDS");

                default:
                    return new EconomyResponse(0, bank.balance(), EconomyResponse.ResponseType.FAILURE, "ERROR");
            }
        } catch (Exception e) {
            getLogger().warning("[VaultConnector] bankDeposit error for '" + name + "': " + e.getMessage());
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Exception");
        }
    }


    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, LANG.plugin_vault_notImplemented);
        //        return new EconomyResponse(0, 0, eco.bank(name).isOwner(playerName)? ResponseType.SUCCESS :
        // FAILURE, "");
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, LANG.plugin_vault_notImplemented);
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, LANG.plugin_vault_notImplemented);
        //    	return new EconomyResponse(0, 0, eco.bank(name).isMember(playerName)? ResponseType.SUCCESS : FAILURE,
        // "");
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, LANG.plugin_vault_notImplemented);
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
        //        return new ArrayList<String>(eco.getBanks());
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return hasAccount(offlinePlayer);
    }


    @Override
    public boolean createPlayerAccount(String playerName, String world) {
        return hasAccount(playerName); // TODO multiworld support
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return hasAccount(offlinePlayer); // TODO multiworld support
    }


    @Override
    public EconomyResponse depositPlayer(String player, String world, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
        return depositPlayer(offlinePlayer, amount);
    }


    @Override
    public double getBalance(String player, String world) {
        return getBalance(player);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String world) {
        return getBalance(offlinePlayer); // TODO multiworld-support
    }


    @Override
    public boolean has(String player, String world, double amount) {
        return has(player, amount); // TODO multiworld-support
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String world, double amount) {
        return has(offlinePlayer, amount); // TODO multiworld-support
    }


    @Override
    public boolean hasAccount(String player, String world) {
        return hasAccount(player);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String world) {
        return hasAccount(offlinePlayer);
    }


    @Override
    public EconomyResponse withdrawPlayer(String player, String world, double amount) {
        return withdrawPlayer(player, amount); // TODO multiworld-support
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
        return withdrawPlayer(offlinePlayer, amount); // TODO multiworld-support
    }
}

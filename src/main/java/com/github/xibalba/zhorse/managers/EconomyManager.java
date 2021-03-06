package com.github.xibalba.zhorse.managers;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.github.xibalba.zhorse.ZHorse;
import com.github.xibalba.zhorse.enums.KeyWordEnum;
import com.github.xibalba.zhorse.enums.LocaleEnum;
import com.github.xibalba.zhorse.utils.MessageConfig;

import net.milkbowl.vault.economy.Economy;

public class EconomyManager {
	
	private ZHorse zh;
	private Economy econ;
	private boolean noEcon = false;
	
	public EconomyManager(ZHorse zh) {
		this.zh = zh;
		RegisteredServiceProvider<Economy> rsp = zh.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
        	econ = rsp.getProvider();
        }
        if (econ == null) {
        	zh.getLogger().warning("No economy plugin found ! Transactions disabled.");
        	noEcon = true;
        }
	}
	
	public boolean canAffordCommand(Player p, String command) {
		return canAffordCommand(p, command, false);
	}
	
	public boolean canAffordCommand(Player p, String command, boolean hideConsole) {
		if (noEcon) return true;
		
		if (p != null) {
			int amount = zh.getCM().getCommandCost(command);
			if (isCommandFree(p, command) || canAffordPayment(p, amount, hideConsole)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean canAffordPayment(Player p, int amount) {
		return canAffordPayment(p, amount, false);
	}
	
	public boolean canAffordPayment(Player p, int amount, boolean hideConsole) {
		if (noEcon) return true;
		
		if (p != null) {
			if (econ.has(zh.getServer().getOfflinePlayer(p.getUniqueId()), amount)) {
				return true;
			}
			if (!hideConsole) {
				String currencySymbol = zh.getMM().getMessage(p, new MessageConfig(LocaleEnum.CURRENCY_SYMBOL), true);
				zh.getMM().sendMessage(p, new MessageConfig(LocaleEnum.NOT_ENOUGH_MONEY) {{ setAmount(amount); setCurrencySymbol(currencySymbol); }});
			}
		}
		return false;
	}
	
	public boolean isCommandFree(UUID playerUUID, String command) {
		if (noEcon) return true;
		
		if (isPlayerOnline(playerUUID)) {
			Player p = zh.getServer().getPlayer(playerUUID);
			return isCommandFree(p, command);
		}
		else {
			return false;
		}
	}
	
	public boolean isCommandFree(Player p, String command) {
		if (noEcon) return true;
		
		int cost = zh.getCM().getCommandCost(command);
		if (cost == 0 || zh.getPM().has(p, KeyWordEnum.ZH_PREFIX.getValue() + command + KeyWordEnum.FREE_SUFFIX.getValue())) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void payCommand(Player p, String command) {
		if (noEcon) return;
		
		int amount = zh.getCM().getCommandCost(command);
		if (!isCommandFree(p, command)) {
			econ.withdrawPlayer(zh.getServer().getOfflinePlayer(p.getUniqueId()), amount);
			String currencySymbol = zh.getMM().getMessage(p, new MessageConfig(LocaleEnum.CURRENCY_SYMBOL), true);
			zh.getMM().sendMessage(p, new MessageConfig(LocaleEnum.COMMAND_PAID) {{ setAmount(amount); setCurrencySymbol(currencySymbol); }});
		}
	}
	
	public void payPlayer(Player payer, UUID receiverUUID, int amount) {
		if (noEcon) return;
		
		econ.withdrawPlayer(payer, amount);
		econ.depositPlayer(zh.getServer().getOfflinePlayer(receiverUUID), amount);
	}
	
	private boolean isPlayerOnline(UUID playerUUID) {
		return zh.getServer().getOfflinePlayer(playerUUID).isOnline();
	}

}

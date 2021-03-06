package com.github.xibalba.zhorse.utils;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.xibalba.zhorse.ZHorse;
import com.github.xibalba.zhorse.database.PendingMessageRecord;
import com.github.xibalba.zhorse.database.PlayerRecord;
import com.github.xibalba.zhorse.managers.MessageManager;

public class PlayerJoin {
	
	public PlayerJoin(ZHorse zh, Player player) {
		UUID playerUUID = player.getUniqueId();
		String playerName = player.getName();
		if (!zh.getDM().isPlayerRegistered(player.getUniqueId())) {
			String language = zh.getCM().getDefaultLanguage();
			int favorite = zh.getDM().getDefaultFavoriteHorseID();
			boolean displayExactStats = zh.getCM().shouldUseExactStats();
			PlayerRecord playerRecord = new PlayerRecord(playerUUID.toString(), playerName, language, favorite, displayExactStats);
			Bukkit.getScheduler().runTaskAsynchronously(zh, new Runnable() {

				@Override
				public void run() {
					zh.getDM().registerPlayer(playerRecord);
				}
				
			});
		}
		else {
			if (!playerName.equalsIgnoreCase(zh.getDM().getPlayerName(playerUUID))) {
				Bukkit.getScheduler().runTaskAsynchronously(zh, new Runnable() {
					
					@Override
					public void run() {
						zh.getDM().updatePlayerName(playerUUID, playerName);
					}
					
				});
			}
			
			Bukkit.getScheduler().runTaskAsynchronously(zh, new Runnable() {

				@Override
				public void run() {
					List<PendingMessageRecord> messageRecordList = zh.getDM().getPendingMessageRecordList(player.getUniqueId());
					zh.getDM().removePendingMessages(player.getUniqueId());
					Bukkit.getScheduler().scheduleSyncDelayedTask(zh, new Runnable() {

						@Override
						public void run() {
							for (PendingMessageRecord messageRecord : messageRecordList) {
								String message = messageRecord.getMessage();
								Date date = messageRecord.getDate();
								zh.getMM().sendMessage(player, message + " " + ChatColor.RESET + MessageManager.DATE_FORMAT_TIMESTAMP.format(date));
							}
						}
						
					}, 5 * 20L);
				}
				
			});
		}
	}
}

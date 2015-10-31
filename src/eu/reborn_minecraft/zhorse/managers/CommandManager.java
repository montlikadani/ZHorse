package eu.reborn_minecraft.zhorse.managers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.CommandEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class CommandManager implements CommandExecutor {
	private ZHorse zh;
	private List<CommandEnum> commandList;
	private List<CommandEnum> settingsCommandList;
	
	public CommandManager(ZHorse zh) {
		this.zh = zh;
		commandList = new ArrayList<CommandEnum>();
		settingsCommandList = new ArrayList<CommandEnum>();
		commandList.add(CommandEnum.claim);
		commandList.add(CommandEnum.free);
		commandList.add(CommandEnum.give);
		commandList.add(CommandEnum.heal);
		commandList.add(CommandEnum.help);
		commandList.add(CommandEnum.here);
	    commandList.add(CommandEnum.info);
		commandList.add(CommandEnum.kill);
		commandList.add(CommandEnum.list);
		commandList.add(CommandEnum.lock);
		commandList.add(CommandEnum.rename);
		commandList.add(CommandEnum.protect);
		commandList.add(CommandEnum.reload);
		commandList.add(CommandEnum.settings);
		commandList.add(CommandEnum.share);
		commandList.add(CommandEnum.tame);
		commandList.add(CommandEnum.tp);
		settingsCommandList.add(CommandEnum.favorite);
		settingsCommandList.add(CommandEnum.language);
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (a.length == 0) {
			a = new String[1];
			a[0] = CommandEnum.help.getName();
		}
		String command = a[0].toLowerCase();
		boolean commandValid = false;
		for (CommandEnum commandEnum : commandList) {
			if (command.equalsIgnoreCase(commandEnum.getName())) {
				commandValid = true;
				try {
					Class.forName(commandEnum.getClassPath()).getConstructor(ZHorse.class, CommandSender.class, String[].class).newInstance(new Object[] {zh, s, a});
				} catch (ClassNotFoundException |
						NoSuchMethodException | 
						SecurityException |
						InstantiationException |
						IllegalAccessException |
						IllegalArgumentException |
						InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		if (!commandValid && !zh.getCM().isConsoleMuted()) {
			zh.getMM().sendMessageValue(s, LocaleEnum.unknownCommand, command);
		}
		return true;
	}
	
	public List<CommandEnum> getCommandList() {
		return commandList;
	}
	
	public List<String> getCommandNameList() {
		List<String> commandNameList = new ArrayList<String>();
		for (CommandEnum command : commandList) {
			commandNameList.add(command.getName());
		}
		return commandNameList;
	}
	
	public List<CommandEnum> getSettingsCommandList() {
		return settingsCommandList;
	}
	
	public List<String> getSettingsCommandNameList() {
		List<String> settingsCommandNameList = new ArrayList<String>();
		for (CommandEnum command : settingsCommandList) {
			settingsCommandNameList.add(command.getName());
		}
		return settingsCommandNameList;
	}
	
}

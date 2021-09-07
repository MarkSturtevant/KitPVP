package com.marks.kitpvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.marks.kitpvp.game.Game;

public class CommandKitPVP implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (args.length == 0)
			return false;
		
		if (args[0].toLowerCase().equals("queue")) {
			Game.queueup();
			return true;
		}
		if (args[0].toLowerCase().equals("start")) {
			if (Game.start())
				return true;
			else return false;
		}
		if (args.length == 1)
			return false;
		if (args[0].toLowerCase().equals("setarena")) {
			Game.setArena(Integer.valueOf(args[1]));
			return true;
		}
		if (args[0].toLowerCase().equals("setgametype")) {
			if (!Game.setGameType(Integer.valueOf(args[1])))
				sender.sendMessage("Invalid number of players!");
			return true;
		}
		
		return false;
		
	}

}

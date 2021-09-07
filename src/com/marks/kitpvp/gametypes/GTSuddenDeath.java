package com.marks.kitpvp.gametypes;

import org.bukkit.ChatColor;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class GTSuddenDeath extends GTOneLife {

	@Override
	public String name() {
		return "Sudden Death";
	}
	
	public static boolean isRightPlayers(int amt) {
		return amt >= 2;
	}
	
	@Override
	public void onStart() {
		int incr = 0;
		for (GamePlayer gp : Game.players)
			gp.teamID = ++incr;
		Game.suddenDeathCountdown = 200;
		Game.broadcastMessage(ChatColor.RED + "Sudden Death starts in 10 seconds!");
	}
	
}

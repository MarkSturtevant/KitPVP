package com.marks.kitpvp.gametypes;

import org.bukkit.ChatColor;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class GTFiveKills extends GameType{

	@Override
	public String name() {
		return "First To Five Kills";
	}
	
	public static boolean isRightPlayers(int amt) {
		return amt >= 3;
	}

	@Override
	public void onStart() {
		int incr = 0;
		for (GamePlayer gp : Game.players)
			gp.teamID = ++incr;
	}

	@Override
	public void removePlayer(GamePlayer gp) {
		if (Game.players.size() == 1)
			endGame();
	}

	@Override
	public void onDeath(GamePlayer gp) {
		for (GamePlayer gp2 : Game.players) {
			if (gp2.kills() == 4)
				Game.broadcastMessage(ChatColor.DARK_RED + gp2.player().getDisplayName() + ChatColor.WHITE + " has 4 kills!");
			else if (gp2.kills() == 5) {
				endGame();
				break;
			}
		}
	}

	@Override
	public boolean onRespawn(GamePlayer gp) {
		return true;
	}

	@Override
	public void endGame() {
		GamePlayer winner = null;
		for (GamePlayer gp : Game.players)
			if (gp.kills() >= 5)
				winner = gp;
		if (winner == null)
			Game.broadcastMessage(ChatColor.BOLD + "Draw Game!");
		else Game.broadcastMessage(ChatColor.BOLD + "" + ChatColor.GREEN + winner.player().getDisplayName() + ChatColor.WHITE + " is the winner!");
		Game.end();
	}

}

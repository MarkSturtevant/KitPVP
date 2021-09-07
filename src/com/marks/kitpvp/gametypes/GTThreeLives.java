package com.marks.kitpvp.gametypes;

import org.bukkit.ChatColor;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class GTThreeLives extends GameType {
	
	int remainingPlayers;
	
	public static boolean isRightPlayers(int amt) {
		return amt >= 2;
	}
	
	@Override
	public String name() {
		return "Three Lives Only";
	}
	
	@Override
	public void onStart() {
		remainingPlayers = Game.players.size();
		int incr = 0;
		for (GamePlayer gp : Game.players)
			gp.teamID = ++incr;
	}

	@Override
	public void removePlayer(GamePlayer gp) {
		if (gp.deaths() < 3 && --remainingPlayers <= 1)
			endGame();
	}

	@Override
	public void onDeath(GamePlayer gp) {
		if (gp.deaths() == 3 && --remainingPlayers <= 1)
			endGame();
	}

	@Override
	public boolean onRespawn(GamePlayer gp) {
		if (gp.deaths() < 3)
			return true;
		return false;
	}

	@Override
	public void endGame() {
		GamePlayer winner = null;
		for (GamePlayer gp : Game.players)
			if (gp.deaths() < 3)
				winner = gp;
		if (winner == null)
			Game.broadcastMessage(ChatColor.BOLD + "Draw Game!");
		else Game.broadcastMessage(ChatColor.BOLD + "" + ChatColor.GREEN + winner.player().getDisplayName() + ChatColor.WHITE + " is the winner!");
		Game.end();
	}

}

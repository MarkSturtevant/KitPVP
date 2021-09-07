package com.marks.kitpvp.gametypes;

import org.bukkit.ChatColor;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class GTOneLife extends GameType {

	int remainingPlayers;
	
	public GTOneLife() {
		remainingPlayers = Game.players.size();
	}
	
	public static boolean isRightPlayers(int amt) {
		return amt >= 2;
	}
	
	@Override
	public String name() {
		return "One Life Only";
	}
	
	@Override
	public void onStart() {
		int incr = 0;
		for (GamePlayer gp : Game.players)
			gp.teamID = ++incr;
	}

	@Override
	public void removePlayer(GamePlayer gp) {
		if (gp.deaths() == 0 && --remainingPlayers == 1)
			endGame();
	}

	@Override
	public void onDeath(GamePlayer gp) {
		if (gp.deaths() == 1 && --remainingPlayers == 1)
			endGame();
	}

	@Override
	public boolean onRespawn(GamePlayer gp) {
		return false;
	}

	@Override
	public void endGame() {
		GamePlayer winner = null;
		for (GamePlayer gp : Game.players)
			if (gp.deaths() == 0)
				winner = gp;
		if (winner == null)
			Game.broadcastMessage(ChatColor.BOLD + "Draw Game!");
		else Game.broadcastMessage(ChatColor.BOLD + "" + ChatColor.GREEN + winner.player().getDisplayName() + ChatColor.WHITE + " is the winner!");
		Game.end();
	}

}

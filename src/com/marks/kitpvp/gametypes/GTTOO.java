package com.marks.kitpvp.gametypes;

import java.util.Random;

import org.bukkit.ChatColor;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class GTTOO extends GameType {
	
	int remainingT2Players;

	@Override
	public String name() {
		return "3:1:1";
	}
	
	public static boolean isRightPlayers(int amt) {
		return amt == 3;
	}

	@Override
	public void onStart() {
		GamePlayer gp1 = Game.players.get(new Random().nextInt(3));
		gp1.teamID = 1;
		gp1.player().sendMessage("You are battling solo in this 1v2 match!  You have 3 lives.");
		for (GamePlayer gp2 : Game.players)
			if (gp2.teamID == -1) {
				gp2.teamID = 2;
				gp2.player().sendMessage("You are battling with a teammate in this 2v1 match!  You have 1 life.");
			}
		remainingT2Players = 2;
	}
	
	@Override
	public void removePlayer(GamePlayer gp) {
		if (gp.teamID == 1 || (gp.teamID == 2 && gp.deaths() == 0 && --remainingT2Players == 0))
			endGame();
	}

	@Override
	public void onDeath(GamePlayer gp) {
		if ((gp.teamID == 1 && gp.deaths() == 3) || (gp.teamID == 2 && gp.deaths() == 1 && --remainingT2Players == 0))
			endGame();
	}

	@Override
	public boolean onRespawn(GamePlayer gp) {
		if (gp.teamID == 1 && gp.deaths() < 3)
			return true;
		return false;
	}

	@Override
	public void endGame() {
		GamePlayer gp1 = null;
		for (GamePlayer gp : Game.players)
			if (gp.teamID == 1)
				gp1 = gp;
		if (gp1.deaths() >= 3) {
			GamePlayer gp2 = null, gp3 = null;
			for (GamePlayer gp : Game.players)
				if (gp.teamID == 2) {
					if (gp2 == null)
						gp2 = gp;
					else gp3 = gp;
				}
			Game.broadcastMessage(ChatColor.BOLD + "" + ChatColor.GREEN + gp2.player().getDisplayName() + ChatColor.WHITE + " and " +
					ChatColor.GREEN + gp3.player().getDisplayName() + ChatColor.WHITE + " are the winners!");
		} else {
			Game.broadcastMessage(ChatColor.BOLD + "" + ChatColor.GREEN + gp1.player().getDisplayName() + ChatColor.WHITE + " is the winner!");
		}
		Game.end();
	}

}

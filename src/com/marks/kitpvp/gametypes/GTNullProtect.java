package com.marks.kitpvp.gametypes;

import org.bukkit.ChatColor;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;
import com.marks.kitpvp.kits.KitNull;

public class GTNullProtect extends GameType {

	@Override
	public String name() {
		return "Protect the Null";
	}
	
	public static boolean isRightPlayers(int amt) {
		return amt == 4;
	}

	@Override
	public void onStart() {
		Game.players.get(0).teamID = 1;
		Game.players.get(0).player().sendMessage("Protect your teammate!");
		Game.players.get(1).teamID = 1;
		Game.players.get(1).setKit("Null");
		Game.players.get(1).player().sendMessage("You are a null!  If you die, your team loses.");
		Game.players.get(2).teamID = 2;
		Game.players.get(2).player().sendMessage("Protect your teammate!");
		Game.players.get(3).teamID = 2;
		Game.players.get(3).setKit("Null");
		Game.players.get(3).player().sendMessage("You are a null!  If you die, your team loses.");
	}

	@Override
	public void removePlayer(GamePlayer gp) {
		if (gp.kit() instanceof KitNull)
			endGame();
	}

	@Override
	public void onDeath(GamePlayer gp) {
		if (gp.kit() instanceof KitNull)
			endGame();
	}

	@Override
	public boolean onRespawn(GamePlayer gp) {
		return true;
	}

	@Override
	public void endGame() {
		int losingTeam = 0;
		for (GamePlayer gp : Game.players)
			if (gp.kit() instanceof KitNull && gp.deaths() == 1)
				losingTeam = gp.teamID;
		if (losingTeam == 0)
			Game.broadcastMessage(ChatColor.BOLD + "Draw Game!");
		else {
			GamePlayer gp1 = null, gp2 = null;
			for (GamePlayer gp : Game.players) {
				if (gp.teamID != losingTeam) {
					if (gp1 == null)
						gp1 = gp;
					else gp2 = gp;
				}
			}
			Game.broadcastMessage(ChatColor.BOLD + "" + ChatColor.GREEN + gp1.player().getDisplayName() + ChatColor.WHITE + " and " +
					ChatColor.GREEN + gp2.player().getDisplayName() + ChatColor.WHITE + " are the winners!");
		}
		Game.end();
	}

}

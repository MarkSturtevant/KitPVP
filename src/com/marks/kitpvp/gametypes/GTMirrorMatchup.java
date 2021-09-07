package com.marks.kitpvp.gametypes;

import java.util.Random;

import org.bukkit.ChatColor;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class GTMirrorMatchup extends GameType {
	
	int remainingPlayers;
	Random rand;
	
	public static boolean isRightPlayers(int amt) {
		return amt >= 2;
	}

	@Override
	public String name() {
		return "Mirror Matchup";
	}

	@Override
	public void onStart() {
		rand = new Random();
		remainingPlayers = Game.players.size();
		String kit = getKitString();
		int incr = 0;
		for (GamePlayer gp : Game.players) {
			gp.teamID = ++incr;
			gp.setKit(kit);
		}
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
	
	private String getKitString() {
		switch(rand.nextInt(15)) {
		case 0:
			return "Solar Power Enthusiast";
		case 1:
			return "Ghoul";
		case 2:
			return "Neo-Archer";
		case 3:
			return "CHAAAARGE!";
		case 4:
			return "Wildman";
		case 5:
			return "Ultimate Defensive";
		case 6:
			return "Tearjerker";
		case 7:
			return "Speed Slicer";
		case 8:
			return "Big Bomber";
		case 9:
			return "Bullet Berserker";
		case 10:
			return "Electro Bowman";
		case 11:
			return "Astronaut";
		case 12:
			return "Shotgun Cycler";
		case 13:
			return "King";
		case 14:
			return "Sector Queen";
		}
		return "";
	}

}

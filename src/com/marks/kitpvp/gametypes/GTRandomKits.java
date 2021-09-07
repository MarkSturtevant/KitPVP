package com.marks.kitpvp.gametypes;

import java.util.Random;

import org.bukkit.ChatColor;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class GTRandomKits extends GameType {
	
	int remainingPlayers;
	Random rand;

	@Override
	public String name() {
		return "Random Kits!";
	}
	
	public GTRandomKits() {
		remainingPlayers = Game.players.size();
	}
	
	public static boolean isRightPlayers(int amt) {
		return amt >= 2;
	}

	@Override
	public void onStart() {
		rand = new Random();
		int incr = 0;
		for (GamePlayer gp : Game.players) {
			gp.teamID = ++incr;
			gp.setKit(getKitString());
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
		if (gp.deaths() < 3) {
			gp.setKit(getKitString());
			return true;
		}
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
		switch(rand.nextInt(16)) {
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
			return "Alien";
		case 10:
			return "Bullet Berserker";
		case 11:
			return "Electro Bowman";
		case 12:
			return "Astronaut";
		case 13:
			return "Shotgun Cycler";
		case 14:
			return "King";
		case 15:
			return "Sector Queen";
		}
		return "";
	}

}

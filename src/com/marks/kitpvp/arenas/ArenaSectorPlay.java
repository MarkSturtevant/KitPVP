package com.marks.kitpvp.arenas;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class ArenaSectorPlay extends Arena {
	
	final int[][] sectorBounds = new int[][] {{460, 139, 480, 157}, {423, 157, 461, 198}, {479, 157, 518, 198}, {479, 100, 518, 140}, {421, 98, 460, 137}};
	final int[] spawnPoints = new int[] {471, 111, 147, 445, 110, 174, 489, 109, 173, 499, 109, 114, 449, 109, 114};
	// for sectorBounds, indexes 0 = green, 1 = brown, 2 = white, 3 = yellow, 4 = blue
	int sectorIndex;
	int restrictionType;
	int timer;
	int counter;
	Random rand;

	public ArenaSectorPlay(World w) {
		super(w);
	}
	
	@Override
	public String getName() {
		return "Sector Play";
	}

	@Override
	public int[] getSpawnPoints() {
		int[] spawnpoints = new int[counter > 0 && counter % 3 == 0 ? 3 : 12];
		if (spawnpoints.length == 3) {
			for (int i = 0; i < 3; i++)
				spawnpoints[i] = spawnPoints[sectorIndex * 3 + i];
		} else {
			int arrayCounter = -1;
			for (int i = 0; i < 15; i++) {
				if (i / 3 == sectorIndex)
					continue;
				spawnpoints[++arrayCounter] = spawnPoints[i];
			}
		}
		return spawnpoints;
	}

	@Override
	public void onStartUp() {
		sectorIndex = 0;
		restrictionType = 0;
		timer = 600;
		counter = 0;
		rand = new Random();
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onTick() {
		if (--timer == 200) {
			restrictionType = 0;
			counter++;
			if (Game.suddenDeath && counter % 3 == 0)
				sectorIndex = 0;
			else sectorIndex = rand.nextInt(4) + 1;
			if (counter % 3 == 0)
				Game.broadcastMessage("Go to the " + getSectorName() + " sector!");
			else Game.broadcastMessage("Evacuate the " + getSectorName() + " sector!");
		}
		else if (timer == 0) {
			restrictionType = counter % 3 == 0 ? 2 : 1;
			timer = 600;
		}
		if (restrictionType == 1)
			for (GamePlayer gp : Game.players) {
				double pX = gp.player().getLocation().getX();
				double pZ = gp.player().getLocation().getZ();
				if (pX > sectorBounds[sectorIndex][0] && pZ > sectorBounds[sectorIndex][1] && pX < sectorBounds[sectorIndex][2] && pZ < sectorBounds[sectorIndex][3])
					gp.player().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 3), true);
			}
		else if (restrictionType == 2)
			for (GamePlayer gp : Game.players) {
				double pX = gp.player().getLocation().getX();
				double pZ = gp.player().getLocation().getZ();
				if (pX < sectorBounds[sectorIndex][0] || pZ < sectorBounds[sectorIndex][1] || pX > sectorBounds[sectorIndex][2] || pZ > sectorBounds[sectorIndex][3])
					gp.player().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 3), true);
			}
	}
	
	private String getSectorName() {
		switch(sectorIndex) {
		case 0:
			return ChatColor.GREEN + "Green" + ChatColor.WHITE;
		case 1:
			return ChatColor.GOLD + "Brown" + ChatColor.WHITE;
		case 2:
			return ChatColor.GRAY + "White" + ChatColor.WHITE;
		case 3:
			return ChatColor.YELLOW + "Yellow" + ChatColor.WHITE;
		case 4:
			return ChatColor.AQUA + "Blue" + ChatColor.WHITE;
		}
		return "Null";
	}

	@Override
	public void onSuddenDeath() {
		timer = 201;
		counter = 8;
	}

}

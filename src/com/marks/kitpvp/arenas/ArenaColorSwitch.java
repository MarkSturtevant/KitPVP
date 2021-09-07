package com.marks.kitpvp.arenas;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class ArenaColorSwitch extends Arena {
	
	int level;
	int timer;
	Random rand;

	public ArenaColorSwitch(World w) {
		super(w);
	}
	
	@Override
	public String getName() {
		return "Color Switch";
	}

	@Override
	public int[] getSpawnPoints() {
		if (Game.suddenDeath)
			return new int[] {599, 31 * level + 76, 230, 599, 31 * level + 76, 206, 623, 31 * level + 76, 206, 623, 31 * level + 76, 230};
		return new int[] {605, 31 * level + 77, 194, 635, 31 * level + 77, 212, 618, 31 * level + 77, 242, 587, 31 * level + 77, 224};
	}

	@Override
	public void onStartUp() {
		level = 0;
		timer = 0;
		rand = new Random();
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onTick() {
		if (++timer == 500)
			Game.broadcastMessage(ChatColor.ITALIC + "Colors switching in 5 seconds!");
		else if (timer == 600) {
			int newFloor = getNewFloor();
			for (GamePlayer gp : Game.players) {
				Location l = gp.player().getLocation();
				Location newLoc = new Location(l.getWorld(), l.getX(), l.getY() - (31 * (level - newFloor)), l.getZ());
				newLoc.setDirection(l.getDirection());
				gp.player().teleport(newLoc);
			}
			level = newFloor;
			timer = 0;
		}
		if (Game.suddenDeath)
			for (GamePlayer gp : Game.players) {
				Location loc = gp.player().getLocation();
				if (Math.sqrt(Math.pow(loc.getX() - 611, 2) + Math.pow(loc.getZ() - 218, 2)) > 20.5 || loc.getY() > 31.0 * level + 90.0)
					gp.player().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 1), true);
			}
	}
	private int getNewFloor() {
		int newFloor = rand.nextInt(4);
		return newFloor == level ? getNewFloor() : newFloor;
	}

	@Override
	public void onSuddenDeath() {
		Game.broadcastMessage(ChatColor.GOLD + "Stay in the inner circle!");
	}

}

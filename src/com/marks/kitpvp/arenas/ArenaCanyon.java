package com.marks.kitpvp.arenas;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class ArenaCanyon extends Arena {
	
	Villager marcus;
	int timer, curMLoc;
	int[] pHyd;
	final int[] mLocs = new int[] {283, 110, 257, 330, 112, 281, 297, 133, 248, 286, 132, 291, 300, 156, 291, 318, 154, 250};
	Random rand;

	public ArenaCanyon(World w) {
		super(w);
		rand = new Random();
		curMLoc = -1;
	}
	
	@Override
	public String getName() {
		return "Canyon";
	}

	@Override
	public int[] getSpawnPoints() {
		return new int[] {282, 157, 247, 321, 156, 289, 278, 126, 253, 318, 128, 251, 265, 115, 290};
	}

	@Override
	public void onStartUp() {
		marcus = (Villager) world.spawnEntity(new Location(world, 268, 181, 237), EntityType.VILLAGER);
		marcus.setGlowing(true);
		marcus.setInvulnerable(true);
		marcus.setCustomName("Marcus");
		marcus.setCustomNameVisible(true);
		marcus.setAI(false);
		timer = 601;
		pHyd = new int[Game.players.size()];
		for (int i = 0; i < pHyd.length; i++)
			pHyd[i] = 1;
	}

	@Override
	public void onEnd() {
		marcus.remove();
		for (int x = 271; x < 276; x++)
			for (int z = 249; z < 253; z++)
				if (world.getBlockAt(x, 156, z).getType().equals(Material.AIR))
					world.getBlockAt(x, 156, z).setType(Material.STATIONARY_WATER);
	}

	@Override
	public void onTick() {
		if (--timer == 0) {
			for (int i = 0; i < pHyd.length; i++) {
				GamePlayer gp = Game.players.get(i);
				if (pHyd[i] == 0) {
					pHyd[i]++;
					gp.player().setFireTicks(Game.suddenDeath ? 480 : 240);
					gp.player().sendMessage(ChatColor.RED + "You are overheating!  Get water from Marcus to avoid this situation.");
				} else if (pHyd[i] == 1)
					gp.player().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You are starting to dehydrate!  Find water from Marcus or face the sun's wrath.");
				pHyd[i]--;
			}
			timer = 600;
		} else if (timer % 300 == 0) {
			curMLoc = getRand();
			marcus.teleport(new Location(world, mLocs[curMLoc * 3], mLocs[curMLoc * 3 + 1], mLocs[curMLoc * 3 + 2]));
		}
		for (int i = 0; i < pHyd.length; i++)
			if (Game.players.get(i).player().getLocation().distance(marcus.getLocation()) < 5 && pHyd[i] < 2) {
				Game.players.get(i).player().sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "You have recieved water from Marcus!");
				pHyd[i] = 2;
			}
	}
	
	private int getRand() {
		int randInt = rand.nextInt(6);
		return randInt == curMLoc ? getRand() : randInt;
	}

	@Override
	public void onSuddenDeath() {
		Game.broadcastMessage(ChatColor.GOLD + "Sun's wrath now burns for twice as long!  Everyone has been hydrated.");
		for (int i = 0; i < pHyd.length; i++)
			pHyd[i] = 2;
		for (GamePlayer gp : Game.players)
			gp.player().setFireTicks(0);
		for (int x = 271; x < 276; x++)
			for (int z = 249; z < 253; z++)
				if (world.getBlockAt(x, 156, z).getType().equals(Material.STATIONARY_WATER))
					world.getBlockAt(x, 156, z).setType(Material.AIR);
	}

}

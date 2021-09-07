package com.marks.kitpvp.arenas;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class ArenaAscend extends Arena {
	
	int spawnLevel;
	int timer;
	Random rand;

	public ArenaAscend(World w) {
		super(w);
	}

	@Override
	public int[] getSpawnPoints() {
		switch (spawnLevel > 1 && spawnLevel < 21 ? spawnLevel + 1 : spawnLevel) {
		case 1:
			return new int[] { 734, 5, -101, 749, 5, -116, 764, 5, -101, 749, 5, -86 };
		case 2:
			return new int[] { 726, 12, -78, 726, 12, -124, 772, 12, -124, 772, 12, -78 };
		case 3:
			return new int[] { 739, 23, -85, 765, 23, -91, 759, 23, -117, 733, 23, -111 };
		case 4:
			return new int[] { 736, 32, -114, 762, 32, -114, 762, 32, -88, 736, 32, -88 };
		case 5:
			return new int[] { 721, 43, -97, 745, 43, -129, 777, 43, -105, 753, 43, -73 };
		case 6:
			return new int[] { 764, 52, -78, 726, 52, -86, 734, 52, -124, 772, 52, -116 };
		case 7:
			return new int[] { 782, 62, -104, 746, 62, -134, 716, 62, -98, 752, 62, -68 };
		case 8:
			return new int[] { 735, 78, -102, 750, 78, -115, 763, 78, -100, 748, 78, -87 };
		case 9:
			return new int[] { 749, 82, -82, 730, 82, -101, 749, 82, -120, 768, 82, -101 };
		case 10:
			return new int[] { 774, 92, -91, 760, 92, -126, 724, 92, -112, 738, 92, -76 };
		case 11:
			return new int[] { 731, 102, -119, 767, 102, -119, 767, 102, -83, 731, 102, -83 };
		case 12:
			return new int[] { 720, 112, -99, 746, 112, -127, 778, 112, -101, 752, 112, -75 };
		case 13:
			return new int[] { 749, 122, -119, 768, 122, -101, 749, 122, -82, 730, 122, -101, 740, 128, -106 };
		case 14:
			return new int[] { 743, 132, -96, 744, 132, -107, 755, 132, -106, 754, 132, -95 };
		case 15:
			return new int[] { 735, 142, -115, 763, 142, -115, 763, 142, -87, 735, 142, -87 };
		case 16:
			return new int[] { 743, 152, -95, 743, 152, -107, 755, 152, -107, 755, 152, -95 };
		case 17:
			return new int[] { 762, 163, -122, 728, 163, -114, 736, 163, -80, 770, 163, -88 };
		case 18:
			return new int[] { 770, 172, -96, 744, 172, -80, 728, 172, -106, 754, 172, -122 };
		case 19:
			return new int[] { 749, 185, -117, 765, 185, -101, 749, 185, -85, 733, 185, -101 };
		case 20:
			return new int[] { 731, 192, -83, 731, 192, -119, 767, 192, -119, 767, 192, -83 };
		case 21:
			return new int[] { 756, 204, -78, 739, 204, -78, 726, 204, -94, 726, 204, -111, 742, 204, -124, 759, 204,
					-124, 772, 204, -108, 772, 204, -91 };
		}
		System.out.println("No spawn points generated! - Ascend");
		return null;
	}

	@Override
	public void onStartUp() {
		spawnLevel = 1;
		timer = 300;
		rand = new Random();
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onTick() {
		for (GamePlayer gp : Game.players) {
			Location l = gp.player().getLocation();
			if (world.getBlockAt(l.getBlockX(), l.getBlockY() - 2, l.getBlockZ()).getType().equals(Material.DIAMOND_BLOCK)) {
				for (int i = 1; i < 255; i++)
					if (world.getBlockAt(l.getBlockX(), l.getBlockY() + i, l.getBlockZ()).getType().equals(Material.EMERALD_BLOCK)) {
						gp.player().teleport(new Location(world, l.getBlockX(), l.getBlockY() + i + 1, l.getBlockZ()));
						break;
					}
			}
			if (l.getBlockY() < 10 * spawnLevel - 10 || l.getBlockY() > 10 * spawnLevel + 11) {
				gp.player().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 1), true);
				world.spawnParticle(Particle.SMOKE_NORMAL, gp.player().getLocation(), 5, 1.1, 2.2, 1.1);
			}
		}
		if (timer > 0 && --timer == 0) {
			if (++spawnLevel < 21)
				timer = 300;
			else timer = 699;
		}
		else if (timer == 100)
			Game.broadcastMessage(ChatColor.RED + "Ascending in 5 seconds!");
		if (spawnLevel == 21 && timer % 10 == 0) {
			timer = 699;
			double rad = rand.nextDouble() * 30.0;
			double ang = rand.nextDouble() * Math.PI * 2;
			world.strikeLightning(new Location(world, 749 + (rad * Math.sin(ang)), 205, -101 + (rad * Math.cos(ang))));
		}
	}

	@Override
	public String getName() {
		return "Ascend";
	}

	@Override
	public void onSuddenDeath() {
		spawnLevel = 21;
		timer = 699;
		int[] tps = getSpawnPoints();
		for (GamePlayer gp : Game.players)
			if (gp.player().getLocation().getY() < 200.0) {
				int random = rand.nextInt(8);
				gp.player().teleport(new Location(world, tps[3 * random], tps[3 * random + 1], tps[3 * random + 2]));
			}
	}

}

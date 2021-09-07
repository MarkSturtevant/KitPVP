package com.marks.kitpvp.arenas;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;
import com.marks.kitpvp.game.GameUtil;

public class ArenaGreenVolcano extends Arena {
	
	private int timer;
	Random rand;

	public ArenaGreenVolcano(World w) {
		super(w);
	}
	
	@Override
	public String getName() {
		return "Green Volcano";
	}

	@Override
	public int[] getSpawnPoints() {
		if (Game.suddenDeath)
			return new int[] {545, 135, 29, 534, 136, -45, 633, 136, -12, 587, 136, 36};
		return new int[] {570, 136, -67, 526, 136, -13, 555, 136, 34, 632, 136, -8};
	}

	@Override
	public void onStartUp() {
		timer = 600;
		rand = new Random();
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onTick() {
		if (--timer % 16 == 0) {
			double ang = rand.nextDouble() * 2 * Math.PI;
			int dist = rand.nextInt(53) + 14;
			GameUtil.summonCreeper(world, (int) (Math.cos(ang) * dist + 579), 220, (int) (Math.sin(ang) * dist - 15), new Vector(0.0, -1.0, 0.0), true, true, 160, 3);
		}
		if (timer == 200) {
			for (GamePlayer gp : Game.players)
				gp.player().playSound(gp.player().getLocation(), Sound.ENTITY_WOLF_HOWL, 10, 1);
		}
		if (timer < 100 && timer % 6 == 0 && !(Game.suddenDeath && timer % 12 == 0)) {
			for (GamePlayer gp : Game.players) {
				if (gp.player().getGameMode().equals(GameMode.SPECTATOR))
					continue;
				double pX = gp.player().getLocation().getX();
				double pZ = gp.player().getLocation().getZ();
				if (rand.nextInt(7) == 0) {
					GameUtil.summonCreeper(world, (int) pX, 220, (int) pZ, new Vector(0.0, -1.0, 0.0), true, true, 160, 3);
				} else if (rand.nextInt(7) == 0) {
					double pVX = gp.player().getVelocity().getX();
					double pVZ = gp.player().getVelocity().getZ();
					double theta = Math.atan(pVZ / pVX) + (pVX < 0 ? Math.PI : 0);
					GameUtil.summonCreeper(world, (int) (Math.cos(theta) * 20 + pX), 220, (int) (Math.sin(theta) * 20 + pZ), new Vector(0.0, -1.0, 0.0), true, true, 160, 3);
				} else {
					double ang = rand.nextDouble() * 2 * Math.PI;
					int dist = rand.nextInt(15) + 6;
					GameUtil.summonCreeper(world, (int) (Math.cos(ang) * dist + pX), 220, (int) (Math.sin(ang) * dist + pZ), new Vector(0.0, -1.0, 0.0), true, true, 160, 3);
				}
			}
		}
		if (timer == 0)
			timer = 600;
		if (Game.suddenDeath) {
			for (GamePlayer gp : Game.players) {
				Location loc = gp.player().getLocation();
				if (world.getHighestBlockAt(loc.getBlockX(), loc.getBlockZ()).getLocation().getBlockY() > loc.getBlockY())
					gp.player().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 2), true);
			}
		}
	}

	@Override
	public void onSuddenDeath() {
		Game.broadcastMessage(ChatColor.GOLD + "Taking cover now damages you!");
	}
}

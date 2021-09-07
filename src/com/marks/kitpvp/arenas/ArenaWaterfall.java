package com.marks.kitpvp.arenas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class ArenaWaterfall extends Arena {
	
	List<Player> inflight;
	Random rand;

	public ArenaWaterfall(World w) {
		super(w);
	}

	@Override
	public int[] getSpawnPoints() {
		if (Game.suddenDeath)
			return new int[] {776, 111, 163, 783, 111, 91, 802, 111, 132, 798, 111, 107};
		return new int[] {772, 199, 83, 777, 201, 109, 776, 199, 150, 771, 194, 177};
	}

	@Override
	public void onStartUp() {
		inflight = new ArrayList<>();
		rand = new Random();
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onTick() {
		for (GamePlayer gp : Game.players) {
			if (gp.player().getGameMode().equals(GameMode.SPECTATOR))
				continue;
			Location pLoc = gp.player().getLocation();
			if (world.getBlockAt(pLoc.getBlockX(), pLoc.getBlockY() - 1, pLoc.getBlockZ()).getType().equals(Material.MELON_BLOCK)) {
				gp.player().setVelocity(new Vector(-5.0, 1.0, 0.0));
				if (!inflight.contains(gp.player()))
					inflight.add(gp.player());
				gp.player().playSound(pLoc, Sound.ENTITY_GENERIC_EXPLODE, 10, 1);
				world.spawnParticle(Particle.EXPLOSION_HUGE, pLoc, 1);
			}
			if (Game.suddenDeath && pLoc.getX() < 775.0)
				gp.player().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 3), true);
		}
		for (int i = 0; i < inflight.size(); i++) {
			Player p = inflight.get(i);
			if (p.getFallDistance() > 2.0)
				p.setFallDistance(0.0f);
			if (p.isOnGround()) {
				inflight.remove(p);
				i--;
			}
		}
		
	}

	@Override
	public String getName() {
		return "Waterfall";
	}

	@Override
	public void onSuddenDeath() {
		Game.broadcastMessage(ChatColor.GOLD + "Stay in the cave!");
		for (GamePlayer gp : Game.players) {
			if (gp.player().getLocation().getX() < 775.0 || gp.player().getLocation().getY() > 133.0) {
				int random = rand.nextInt(4);
				int[] tps = getSpawnPoints();
				gp.player().teleport(new Location(world, tps[random * 3], tps[random * 3 + 1], tps[random * 3 + 2]));
			}
		}
	}

}

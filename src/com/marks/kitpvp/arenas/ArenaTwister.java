package com.marks.kitpvp.arenas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;
import com.marks.kitpvp.game.GameUtil;

public class ArenaTwister extends Arena {
	
	private final int[] spawnSets = new int[] {931, 198, 2, 981, 198, -1, 994, 199, 31, 986, 198, 59, 949, 199, 77, 914, 199, 45};
	private Random rand;
	private Twister twister;

	public ArenaTwister(World w) {
		super(w);
	}

	@Override
	public int[] getSpawnPoints() {
		List<Integer> spawnpoints = new ArrayList<>();
		for (int i = 0; i < spawnSets.length; i += 3) {
			if (twister.curLoc.distance(new Location(world, spawnSets[i], spawnSets[i + 1], spawnSets[i + 2])) > 20) {
				spawnpoints.add(spawnSets[i]);
				spawnpoints.add(spawnSets[i + 1]);
				spawnpoints.add(spawnSets[i + 2]);
			}
		}
		return spawnpoints.stream().mapToInt(i -> i).toArray();
	}

	@Override
	public void onStartUp() {
		rand = new Random();
		twister = new Twister();
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onTick() {
		twister.onTick();
	}

	@Override
	public void onSuddenDeath() {
		Game.broadcastMessage(ChatColor.GOLD + "The twister moves much more quickly!");
		twister.setSuddenDeath();
	}

	@Override
	public String getName() {
		return "Twister";
	}
	
	private class Twister {
		
		private static final int NUM_VILLAGERS = 9;
		
		private int curPhase, pPhase;
		private Location curLoc, target;
		private double rotation, speed;
		
		Twister() {
			pPhase = 3;
			curPhase = 0;
			rotation = 0;
			speed = 4.3;
			curLoc = new Location(world, 952, 198, 33);
			selectNewTarget();
		}
		
		private void selectNewTarget() {
			if (++curPhase == pPhase) {
				List<GamePlayer> a = GameUtil.getAlivePlayers();
				target = a.get(rand.nextInt(a.size())).player().getLocation();
				target.setY(198);
				curPhase = 0;
			}
			else target = new Location(world, rand.nextInt(71) + 918, 198, rand.nextInt(66) + 4);
		}
		
		private void setSuddenDeath() {
			pPhase = 1;
			curPhase = 0;
			speed = 6.5;
		}
		
		private void onTick() {
			rotation += Math.PI / 10.0;
			if (rotation > Math.PI * 2)
				rotation -= Math.PI * 2;
			double dist = curLoc.distance(target);
			curLoc.setX(curLoc.getX() + (target.getX() - curLoc.getX()) / dist * speed / 20);
			curLoc.setZ(curLoc.getZ() + (target.getZ() - curLoc.getZ()) / dist * speed / 20);
			if (dist < 3)
				selectNewTarget();
			for (GamePlayer gp : GameUtil.getAlivePlayers()) {
				Location pLoc = gp.player().getLocation();
				if (Math.sqrt(Math.pow(pLoc.getZ() - curLoc.getZ(), 2) + Math.pow(pLoc.getX() - curLoc.getX(), 2)) < 8.5) {
					gp.player().setVelocity(new Vector(0.0, 0.3, 0.0));
					gp.damage(2, null);
				}
			}
			for (int i = 0; i < NUM_VILLAGERS; i++) {
				world.spawnParticle(Particle.EXPLOSION_LARGE, new Location(world, curLoc.getX() + i * Math.sin(rotation), 198 + i * 2, curLoc.getZ() + i * Math.cos(rotation)), 1);
				world.spawnParticle(Particle.SMOKE_LARGE, new Location(world, curLoc.getX() + i * Math.sin(rotation), 198 + i * 2, curLoc.getZ() + i * Math.cos(rotation)), 4);
			}
		}
		
	}

}

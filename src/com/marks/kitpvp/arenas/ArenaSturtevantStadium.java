package com.marks.kitpvp.arenas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;
import com.marks.kitpvp.game.GameUtil;

public class ArenaSturtevantStadium extends Arena {

	private static final int MAX_TAR = 4;
	private int timer;
	private int timerMax;
	private Random rand;
	private List<ParametricTNT> activeTNT;
	private Location[] targets;
	
	public ArenaSturtevantStadium(World w) {
		super(w);
	}

	@Override
	public int[] getSpawnPoints() {
		return new int[] {647, 109, 337, 647, 109, 377, 607, 109, 378, 607, 109, 336};
	}

	@Override
	public void onStartUp() {
		timer = 0;
		timerMax = 120;
		rand = new Random();
		activeTNT = new ArrayList<>();
		targets = new Location[] {null, null, null, null};
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnd() {
		for (int i = 0; i < activeTNT.size(); )
			activeTNT.get(i).murder();
		Game.broadcastMessage(ChatColor.GREEN + "Resetting Sturtevant Stadium, expect lag!");
		for (int x = 582; x < 673; x++)
			for (int z = 338; z < 377; z++)
				if (world.getBlockAt(x, 91, z).getType() == Material.SPONGE)
					for (int y = 92; y < 101; y++) {
						Block b1 = world.getBlockAt(x, y, z);
						Block b2 = world.getBlockAt(x, y + 13, z);
						b2.setData(b1.getData());
						b2.setType(b1.getType());
					}
		Game.broadcastMessage(ChatColor.DARK_GREEN + "Reset Finished.");
	}

	@Override
	public void onTick() {
		for (GamePlayer gp : Game.players) {
			Location cur = gp.player().getLocation();
			cur.setY(cur.getY() - 1.0);
			if (world.getBlockAt(cur).getType() == Material.OBSIDIAN)
				gp.player().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 2), true);
		}
		for (int i = 0; i < activeTNT.size(); i++)
			if (!activeTNT.get(i).onTick())
				i--;
		if (++timer > timerMax) {
			timer = 0;
			if (targets[0] != null)
				activeTNT.add(new ParametricTNT(targets[0]));
			for (int i = 0; i < MAX_TAR - 1; i++)
				targets[i] = targets[i + 1];
			targets[MAX_TAR - 1] = null;
			List<GamePlayer> a = GameUtil.getAlivePlayers();
			int startingPos = rand.nextInt(a.size());
			for (int i = startingPos + 1; i != startingPos; i++) {
				if (i == a.size())
					i = 0;
				Location cur = a.get(i).player().getLocation();
				cur.setY(91);
				if (world.getBlockAt(cur).getType() == Material.SPONGE) {
					targets[MAX_TAR - 1] = a.get(i).player().getLocation();
					break;
				}
			}
		}
	}

	@Override
	public void onSuddenDeath() {
		Game.broadcastMessage(ChatColor.GOLD + "TNT now gets launched 3 times as frequent!");
		timerMax = 40;
	}

	@Override
	public String getName() {
		return "Sturtevant Stadium";
	}
	
	final double yacc = -0.025, yvel = 1.0;
	final double[] posXCannons = new double[] {585.5, 118.0, 361.5, 585.5, 118.0, 353.5};
	final double[] negXCannons = new double[] {669.5, 118.0, 361.5, 669.5, 118.0, 353.5};
	
	private class ParametricTNT {
		
		TNTPrimed tnt;
		double xvel, zvel, time;
		double timer;
		
		public ParametricTNT(Location loc) {
			timer = 0;
			double yc = 118 - loc.getBlockY();
			time = (-1 * yvel - Math.sqrt(Math.pow(yvel, 2) - (4 * yacc * yc))) / (2 * yacc);
			int bunch = rand.nextInt(2);
			double[] cannonCoords = Arrays.copyOfRange(loc.getX() < 627 ? negXCannons : posXCannons, bunch, bunch + 3);
			tnt = (TNTPrimed) world.spawnEntity(new Location(world, cannonCoords[0],  cannonCoords[1], cannonCoords[2]), EntityType.PRIMED_TNT);
			tnt.setFuseTicks((int) Math.ceil(time + 80));
			tnt.setGravity(false);
			xvel = (loc.getX() - cannonCoords[0]) / time;
			zvel = (loc.getZ() - cannonCoords[2]) / time;
		}
		
		private boolean onTick() {
			if (++timer < time) {
				Location cur = tnt.getLocation();
				tnt.teleport(new Location(world, cur.getX() + xvel, yacc * timer * timer + yvel * timer + 118, cur.getZ() + zvel));
				if (world.getBlockAt(tnt.getLocation()).getType().isSolid())
					timer = time;
			}
			if (timer >= time)
				tnt.setGravity(true);
			if (tnt.isDead()) {
				murder();
				return false;
			}
			
			return true;
		}
		
		private void murder() {
			if (!tnt.isDead())
				tnt.remove();
			activeTNT.remove(this);
		}
		
	}

}

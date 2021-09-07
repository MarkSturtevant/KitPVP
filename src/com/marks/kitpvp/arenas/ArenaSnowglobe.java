package com.marks.kitpvp.arenas;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;
import com.marks.kitpvp.game.GameUtil;
import com.marks.kitpvp.Main;

public class ArenaSnowglobe extends Arena {
	
	final int[] beaconPoints = new int[] {453, 112, -103, 448, 112, -157, 379, 112, -164, 381, 112, -92};
	Location currentBeaconLoc;
	int timer;
	Random rand;

	public ArenaSnowglobe(World w) {
		super(w);
	}
	
	@Override
	public String getName() {
		return "Snowglobe";
	}

	@Override
	public int[] getSpawnPoints() {
		return new int[] {418, 112, -196, 472, 112, -140, 417, 112, -81, 358, 112, -140};
	}

	@Override
	public void onStartUp() {
		rand = new Random();
		int random = rand.nextInt(4);
		currentBeaconLoc = new Location(world, beaconPoints[random * 3], beaconPoints[random * 3 + 1], beaconPoints[random * 3 + 2]);
		world.getBlockAt(currentBeaconLoc.getBlockX(), currentBeaconLoc.getBlockY() - 1, currentBeaconLoc.getBlockZ()).setType(Material.BEACON);
		timer = 500;
	}

	@Override
	public void onEnd() {
		if (currentBeaconLoc != null)
			world.getBlockAt(currentBeaconLoc.getBlockX(), currentBeaconLoc.getBlockY() - 1, currentBeaconLoc.getBlockZ()).setType(Material.AIR);
	}

	@Override
	public void onTick() {
		if (--timer > 0) {
			if (timer % 7 == 0)
				spawnFirework(currentBeaconLoc.getBlockX(), currentBeaconLoc.getBlockY() + (int) (timer / 5), currentBeaconLoc.getBlockZ());
		} else {
			for (GamePlayer gp : Game.players)
				if (GameUtil.checkLocations(gp.player().getLocation(), currentBeaconLoc)) {
					GameUtil.changePlayerHealth(gp, 3);
					if (Game.suddenDeath)
						for (GamePlayer gp2 : Game.players)
							if (!gp2.equals(gp))
								GameUtil.changePlayerHealth(gp2, -10);
					world.getBlockAt(currentBeaconLoc.getBlockX(), currentBeaconLoc.getBlockY() - 1, currentBeaconLoc.getBlockZ()).setType(Material.AIR);
					newPos();
					timer = 500;
					break;
				}
		}
	}
	
	private void newPos() {
		int random = rand.nextInt(4);
		if (currentBeaconLoc.equals(new Location(world, beaconPoints[random * 3], beaconPoints[random * 3 + 1], beaconPoints[random * 3 + 2]))) {
			newPos();
			return;
		}
		currentBeaconLoc = new Location(world, beaconPoints[random * 3], beaconPoints[random * 3 + 1], beaconPoints[random * 3 + 2]);
		world.getBlockAt(currentBeaconLoc.getBlockX(), currentBeaconLoc.getBlockY() - 1, currentBeaconLoc.getBlockZ()).setType(Material.BEACON);
	}
	
	private void spawnFirework(int x, int y, int z) {
		Firework fw = (Firework) world.spawnEntity(new Location(world, x, y, z), EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
		if (Game.suddenDeath)
			fwm.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(0, 0, 0), Color.fromRGB(50, 50, 50)).with(Type.BURST).build());
		else fwm.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(240, 155, 255), Color.fromRGB(219, 197, 211)).with(Type.BURST).build());
		fw.setFireworkMeta(fwm);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
			fw.detonate();
		}, 2L);
	}

	@Override
	public void onSuddenDeath() {
		Game.broadcastMessage(ChatColor.BLACK + "Beacons now remove hearts from all opponents!");
		world.getBlockAt(currentBeaconLoc.getBlockX(), currentBeaconLoc.getBlockY() - 1, currentBeaconLoc.getBlockZ()).setType(Material.AIR);
		newPos();
		timer = 500;
	}

}

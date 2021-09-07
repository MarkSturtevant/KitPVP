package com.marks.kitpvp.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftCreeper;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class GameUtil {

	public static boolean checkLocations(Location l1, Location l2) {
		if (l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ())
			return true;
		return false;
	}
	
	public static void summonCreeper(World w, int x, int y, int z, Vector velocity, boolean invulnerable, boolean ignited, int fuse, int explosionradius) {
		Creeper c = (Creeper) w.spawnEntity(new Location(w, x, y, z), EntityType.CREEPER);
		c.setVelocity(velocity);
		c.setInvulnerable(invulnerable);
		net.minecraft.server.v1_12_R1.EntityCreeper nms = ((CraftCreeper) c).getHandle();
		NBTTagCompound nbt = new NBTTagCompound();
		nms.c(nbt);
		nbt.setBoolean("ignited", ignited);
		nbt.setInt("ExplosionRadius", explosionradius);
		nbt.setInt("Fuse", fuse);
		nms.a(nbt);
	}
	
	@SuppressWarnings("deprecation")
	public static void changePlayerHealth(GamePlayer gp, double amount) {
		if (gp.player().getHealthScale() + amount <= 0) {
			gp.player().setHealth(0.0);
		} else {
			double newHealth = gp.player().getHealthScale() + amount;
			gp.player().setHealthScale(newHealth);
			gp.player().setMaxHealth(newHealth);
		}
	}
	
	public static GamePlayer getFromName(String name) {
		for (Player p : Bukkit.getOnlinePlayers())
			if (p.getDisplayName().equals(name))
				return Game.getGamePlayer(p);
		return null;
	}
	
	public static List<GamePlayer> getAlivePlayers() {
		List<GamePlayer> activePlayers = new ArrayList<>();
		for (GamePlayer gp : Game.players)
			if (gp.player().getGameMode() == GameMode.ADVENTURE)
				activePlayers.add(gp);
		return activePlayers;
	}
	
}

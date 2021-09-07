package com.marks.kitpvp.kits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class KitBigBomber extends Kit {
	
	List<Location> mines;
	
	private final ItemStack sword = KitUtils.makeWeapon(Material.WOOD_SWORD, (short) 0, 1, false, "Butter Knife", 4, 1.0, "Great for toast!");
	private final ItemStack mommamine = KitUtils.makeItem(Material.STONE_PLATE, (short) 0, 1, true, "Momma Mine", "An era of grand destruction!  One shots everything.");
	private final ItemStack micromine = KitUtils.makeItem(Material.IRON_PLATE, (short) 0, 5, true, "Micro Mine", "Place down a mini mine!");
	private final ItemStack macromine = KitUtils.makeItem(Material.GOLD_PLATE, (short) 0, 2, true, "Macro Mine", "Place down a devastating mine!");
	private final ItemStack grenades = KitUtils.makeItem(Material.SNOW_BALL, (short) 0, 4, true, "Precision Grenade", "Only explodes if it hits a player.  Be precise!");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Great Bomber's Boots", Color.fromRGB(198, 180, 165), false),
			KitUtils.makeArmor(1, "Steel Pants", Color.fromRGB(216, 216, 216), false),
			KitUtils.makeArmor(2, "Redstone Shirt", Color.fromRGB(188, 0, 0), false),
			KitUtils.makeArmor(3, "Telekeneninium Helmet", Color.fromRGB(62, 5, 89), false)
	};

	public KitBigBomber(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Big Bomber");
		mines = new ArrayList<>();
	}

	@Override
	public double getArmorPerc() {
		return 0.7;
	}

	@Override
	public double getKnockBackRes() {
		return 0.8;
	}

	@Override
	public void setInventory() {
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, sword);
		pi.setItem(1, micromine);
		pi.setItem(2, macromine);
		pi.setItem(3, mommamine);
		pi.setItem(4, grenades);
		gp.player().setWalkSpeed(0.20f);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
		if (isRightClick) {
			PlayerInventory pi = gp.player().getInventory();
			int selSlot = pi.getHeldItemSlot();
			ItemStack selItem = pi.getItemInMainHand();
			if (selItem == null)
				return;
			float dir = 0.0f;
			switch(selItem.getType()) {
			case IRON_PLATE:
				break;
			case GOLD_PLATE:
				dir = 1.0f;
				break;
			case STONE_PLATE:
				for (GamePlayer gp2 : Game.players)
					if (gp2.teamID != gp.teamID && gp2.player().getLocation().distance(gp.player().getLocation()) < 10.0) {
						gp.player().sendMessage(ChatColor.RED + "A player is too close for you to setup the momma mine! (< 10 blocks)");
						return;
					}
				dir = 2.0f;
				break;
			default:
				return;
			}
			Location loc = gp.player().getTargetBlock((Set<Material>) null, 5).getLocation();
			if (gp.player().getWorld().getBlockAt(loc).getType().equals(Material.AIR))
				return;
			loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
			for (Location mine : mines)
				if (loc.distance(mine) < 3.0) {
					gp.player().sendMessage(ChatColor.RED + "Your mine is too close to another mine!");
					return;
				}
			loc.setYaw(dir);
			selItem.setAmount(selItem.getAmount() - 1);
			pi.setItem(selSlot, selItem);
			mines.add(loc);
		}
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		if (gp.player().getInventory().getHeldItemSlot() == 0) {
			dealDamage(damage, pHit);
			dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage / 4.0 * 1.6), pHit);
		}
		else dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage / 1.0 * 1.3), pHit);
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
		GamePlayer gpHit = null;
		if (pHit instanceof Player && (gpHit = Game.getGamePlayer((Player) pHit)) != null)
			gpHit.damage(0, gp);
		createArtificialExplosion(6.0, 1.0, pHit.getLocation(), 0.5);
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
	}

	@Override
	public void onTick() {
		for (GamePlayer gp2 : Game.players)
			for (Location loc : mines)
				if (gp2.teamID != gp.teamID && gp2.player().getLocation().distance(loc) < 1.4) {
					mines.remove(loc);
					gp2.damage(0, gp);
					switch((int) loc.getYaw()) {
					case 0:
						createArtificialExplosion(7.0, 2, loc, 2.0);
						break;
					case 1:
						createArtificialExplosion(16.0, 4, loc, 7.0);
						break;
					case 2:
						createArtificialExplosion(10000.0, 2, loc, 100.0);
						break;
					}
					break;
				}
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(5)) {
		case 0:
			return killed + " had an explosive finish as a result of " + killer;
		case 1:
			return killed + " was blasted into a million pieces by " + killer;
		case 2:
			return killed + " took the brave move of stepping on " + killer + "'s mine";
		case 3:
			return killed + " learned how " + killer + "'s mines work";
		case 4:
			return killed + " got caught in " + killer + "'s shockwave";
		}
		
		return "";
	}
	
	private void createArtificialExplosion(double damage, double radius, Location loc, double maxKnockback) {
		loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
		loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 10.0f, 1.0f);
		for (GamePlayer gp2 : Game.players) {
			double distance = gp2.player().getLocation().distance(loc);
			if (distance > radius)
				continue;
			double fract = 1.0 - (distance / radius);
			gp2.damage(damage * fract, gp);
			System.out.println("Bomb: x" + (gp2.player().getLocation().getX() - loc.getX()) + " z" + (gp2.player().getLocation().getZ() - loc.getZ()) + " p" + (maxKnockback * fract));
			gp2.knockback(GamePlayer.getKnockbackVector(new Vector(gp2.player().getLocation().getX() - loc.getX(), 0.0, gp2.player().getLocation().getZ() - loc.getZ()), maxKnockback * fract));
		}
	}

}

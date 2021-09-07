package com.marks.kitpvp.kits;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import com.marks.kitpvp.Main;
import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class KitBulletBerserker extends Kit {
	
	int countdown, hcountdown;
	boolean charging;
	Random rand;
	
	private final ItemStack axe = KitUtils.makeWeapon(Material.IRON_AXE, (short) 0, 1, false, "Viking Axe", 5, 0.7, "Hit 'em like the vikings did!");
	private final ItemStack charge = KitUtils.makeItem(Material.WOOD, (short) 0, 1, true, "Activate Berserk", "It's quite the experience!");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Viking Boots", Color.fromRGB(112, 63, 0), false),
			KitUtils.makeArmor(1, "Viking Leggings", Color.fromRGB(112, 63, 0), false),
			KitUtils.makeArmor(2, "Viking Chestplate", Color.fromRGB(112, 63, 0), false),
			KitUtils.makeArmor(3, "Viking Helmet", Color.fromRGB(255, 200, 0), false)
	};

	public KitBulletBerserker(GamePlayer gp) {
		super(gp);
		rand = new Random();
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Bullet Berserker");
	}

	@Override
	public double getArmorPerc() {
		if (charging)
			return 0.0;
		return 0.8;
	}

	@Override
	public double getKnockBackRes() {
		if (charging)
			return 0.0;
		return 0.9;
	}

	@Override
	public void setInventory() {
		countdown = 0;
		charging = false;
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, axe);
		pi.setItem(1, charge);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
		if (isRightClick) {
			PlayerInventory pi = gp.player().getInventory();
			int selSlot = pi.getHeldItemSlot();
			ItemStack selItem = pi.getItemInMainHand();
			if (selSlot == 1 && selItem != null && selItem.getAmount() >= 1) {
				pi.setItem(selSlot, null);
				charging = true;
				countdown = 100;
				hcountdown = 3;
			}
		}
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		dealDamage(damage, pHit);
		dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage / 4.0 * 2.0), pHit);
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
	}

	@Override
	public void onTick() {
		if (charging && --countdown > 0) {
			if (countdown % 4 == 0) {
				Location loc = gp.player().getLocation();
				Vector v = loc.getDirection();
				v.setX(v.getX() + (rand.nextDouble() * 0.4) - 0.2);
				v.setY(v.getY() + (rand.nextDouble() * 0.4) - 0.2);
				v.setZ(v.getZ() + (rand.nextDouble() * 0.4) - 0.2);
				gp.player().setVelocity(v.multiply(1.4));
				gp.player().setFallDistance(0.0f);
				gp.player().getLocation().setDirection(v);
				gp.player().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
				for (GamePlayer gp2 : Game.players) {
					gp2.player().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 10, 1);
					if (!gp2.equals(gp) && loc.distance(gp2.player().getLocation()) < 4) {
						gp2.damage(7.0, gp);
						if (--hcountdown == 0) {
							charging = false;
							countdown = 600;
						}
					}
				}
				Block toB = null;
				if (Math.abs(loc.getDirection().getY()) > 0.7)
					toB = gp.player().getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + (loc.getDirection().getY() < 0 ? -1 : 2), loc.getBlockZ());
				else if (Math.abs(loc.getDirection().getX()) > 0.5)
					toB = gp.player().getWorld().getBlockAt(loc.getBlockX() + (loc.getDirection().getX() > 0 ? 1 : -1), loc.getBlockY(), loc.getBlockZ());
				else
					toB = gp.player().getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + (loc.getDirection().getZ() > 0 ? 1 : -1));
				if (!toB.getType().equals(Material.AIR)) {
					charging = false;
					countdown = 600;
				}
				Location pLoc = gp.player().getLocation();
				Firework fw = (Firework) pLoc.getWorld().spawnEntity(new Location(pLoc.getWorld(), pLoc.getX() + rand.nextInt(11) - 5, pLoc.getY() + rand.nextInt(11) - 5, pLoc.getZ() + rand.nextInt(11) - 5), EntityType.FIREWORK);
				FireworkMeta fwm = fw.getFireworkMeta();
				fwm.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(153, 68, 0), Color.fromRGB(255, 114, 0)).with(Type.CREEPER).build());
				fw.setFireworkMeta(fwm);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
					fw.detonate();
				}, 2L);
				if (countdown == 0)
					charging = false;
			}
		}
		else if (countdown > 0 && --countdown == 0) {
			gp.player().getInventory().setItem(1, charge);
		}
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(4)) {
		case 0:
			return killed + " took " + killer + "'s axe to the face";
		case 1:
			return killed + " disrespected " + killer + "'s traditions";
		case 2:
			return killed + " succumbed to " + killer + " flying axe assault";
		case 3:
			return killed + " got berserked by " + killer;
		}
		
		return "";
	}

}

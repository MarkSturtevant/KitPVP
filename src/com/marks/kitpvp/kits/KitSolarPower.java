package com.marks.kitpvp.kits;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.ChatColor;

import com.marks.kitpvp.game.GamePlayer;

public class KitSolarPower extends Kit {
	
	int cooldown;
	boolean powered, charging;
	
	private final ItemStack solarOff = KitUtils.makeItem(Material.DAYLIGHT_DETECTOR, (short) 0, 1, false, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Discharged Solar Panel", "Deals no Damage");
	private final ItemStack solarOn = KitUtils.makeItem(Material.DAYLIGHT_DETECTOR, (short) 0, 1, true, ChatColor.YELLOW + "" + ChatColor.BOLD + "Charged Solar Panel", "Unleashes Massive Damage;", "ZAAAAAP!");
	private final ItemStack lever = KitUtils.makeItem(Material.LEVER, (short) 0, 1, true, ChatColor.AQUA + "On Switch", "Powers up your solar panel!");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Electroplated Boots", Color.fromRGB(10, 24, 94), true),
			KitUtils.makeArmor(1, "Electroplated Leggings", Color.fromRGB(34, 77, 145), true),
			KitUtils.makeArmor(2, "Electroplated Chestplate", Color.fromRGB(75, 120, 193), true),
			KitUtils.makeArmor(3, "Solar Panel", Color.fromRGB(234, 234, 234), true)
	};
	
	public KitSolarPower(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Solar Power Enthusiast");
	}

	@Override
	public double getArmorPerc() {
		return 0.65;
	}
	
	@Override
	public double getKnockBackRes() {
		return 0.9;
	}

	@Override
	public void setInventory() {
		cooldown = 0;
		powered = false;
		charging = false;
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, solarOff);
		pi.setItem(1, lever);
		gp.player().setWalkSpeed(0.20f);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
		PlayerInventory pi = gp.player().getInventory();
		int slot = gp.player().getInventory().getHeldItemSlot();
		if (isRightClick) {
			if (pi.getItem(slot) != null && pi.getItem(slot).equals(lever)) {
				pi.remove(lever);
				gp.player().setWalkSpeed(0.20f);
				charging = true;
				cooldown = 70;
				gp.player().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 90, 3));
			}
		} else {
			if (powered) {
				cooldown -= 50;
				gp.player().getWorld().spawnParticle(Particle.SMOKE_LARGE, gp.player().getLocation(), 30, 1.4, 1.2, 1.4);
			}
		}
	}

	@Override
	public void onTick() {
		if (powered) {
			gp.player().getWorld().spawnParticle(Particle.HEART, gp.player().getLocation(), 2, 1.2, 0.6, 1.2);
			if (--cooldown <= 0) {
				cooldown = 280;
				powered = false;
				gp.player().setWalkSpeed(0.16f);
				gp.player().getInventory().setItem(0, solarOff);
			}
		}
		else if (cooldown > 0 && --cooldown == 0) {
			if (charging) {
				if (gp.player().hasPotionEffect(PotionEffectType.GLOWING))
					gp.player().removePotionEffect(PotionEffectType.GLOWING);
				charging = false;
				powered = true;
				gp.player().getInventory().setItem(0, solarOn);
				cooldown = 200;
			} else {
				gp.player().getInventory().setItem(1, lever);
			}
		}
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		if (gp.player().getInventory().getHeldItemSlot() == 0 && powered) {
			dealDamage(30, pHit);
			cooldown = 0;
			powered = false;
			gp.player().getInventory().setItem(0, solarOff);
			gp.player().getInventory().setItem(1, lever);
		}
		else {
			dealDamage(damage, pHit);
			dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage), pHit);
		}
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(5)) {
		case 0:
			return killed + " got slapped by a solar panel by " + killer;
		case 1:
			return killed + " discovered the power of solar panels because of " + killer;
		case 2:
			return killed + " recieved 10,000 Volts of electricity by " + killer;
		case 3:
			return killed + " got too close to " + killer + "'s unstable solar panel";
		case 4:
			return killed + " was taught a lesson about solar power by " + killer;
		}
		
		return "";
	}

}

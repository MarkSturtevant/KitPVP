package com.marks.kitpvp.kits;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.marks.kitpvp.game.GamePlayer;

public class KitSpeedSlicer extends Kit {
	
	private final ItemStack sword = KitUtils.makeWeapon(Material.WOOD_SWORD, (short) 0, 1, false, "Wooden Knife", 4, 0.1, "Attacks fast!  But a bit buttery...");
	private final ItemStack pearls = KitUtils.makeItem(Material.ENDER_PEARL, (short) 0, 5, true, "Trender Pearl", "Teleporting seems to be the fad today.");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Speed Suit", Color.fromRGB(128, 64, 64), false),
			KitUtils.makeArmor(1, "Speed Suit", Color.fromRGB(128, 64, 64), false),
			KitUtils.makeArmor(2, "Speed Suit", Color.fromRGB(128, 64, 64), false),
			KitUtils.makeArmor(3, "Speed Suit", Color.fromRGB(128, 64, 64), false)
	};

	public KitSpeedSlicer(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Speed Slicer");
	}

	@Override
	public double getArmorPerc() {
		return 0.9;
	}

	@Override
	public double getKnockBackRes() {
		return 1.1;
	}

	@Override
	public void setInventory() {
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, sword);
		pi.setItem(1, pearls);
		gp.player().setWalkSpeed(0.22f);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		dealDamage(damage, pHit);
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
	}

	@Override
	public void onTick() {
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(5)) {
		case 0:
			return killed + " couldn't handle " + killer + "'s speed";
		case 1:
			return killed + " became " + killer + "'s pupil";
		case 2:
			return killed + " was sliced into a million pieces by " + killer;
		case 3:
			return killed + " got sliced like deli turkey at " + killer + "'s poultry place";
		case 4:
			return killed + " didn't follow " + killer + "'s new trend";
		}
		
		return "";
	}

}

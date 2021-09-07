package com.marks.kitpvp.kits;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.marks.kitpvp.game.GamePlayer;

public class KitSectorQueen extends Kit {
	
	private final ItemStack shovel = KitUtils.makeWeapon(Material.IRON_SPADE, (short) 0, 1, true, "Playtime", 5, 1.2, "Throws snowballs!");
	private final ItemStack jumps = KitUtils.makeItem(Material.FEATHER, (short) 0, 12, true, "Helicopter Feathers", "Right click to recover!");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Lightweight boots", Color.fromRGB(255, 255, 255), true),
			KitUtils.makeArmor(1, "Snow Leggings", Color.fromRGB(255, 255, 255), true),
			KitUtils.makeArmor(2, "Ray Chestplate", Color.fromRGB(255, 255, 255), true),
			KitUtils.makeArmor(3, "Feather Cap", Color.fromRGB(255, 255, 255), true)
	};
	
	int timer;

	public KitSectorQueen(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Sector Queen");
		timer = 0;
	}

	@Override
	public double getArmorPerc() {
		return 1.2;
	}

	@Override
	public double getKnockBackRes() {
		return 0.0;
	}

	@Override
	public void setInventory() {
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, shovel);
		pi.setItem(1, jumps);
		timer = 0;
		gp.player().setWalkSpeed(0.15f);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
		if (!isRightClick)
			return;
		PlayerInventory pi = gp.player().getInventory();
		int selSlot = pi.getHeldItemSlot();
		if (selSlot == 0) {
			Snowball sn = (Snowball) gp.player().launchProjectile(Snowball.class, gp.player().getLocation().getDirection());
			sn.setShooter(gp.player());
		}
		else if (selSlot == 1) {
			ItemStack selItem = pi.getItem(selSlot);
			if (selItem != null && selItem.getType() == Material.FEATHER) {
				selItem.setAmount(selItem.getAmount() - 1);
				pi.setItem(selSlot, selItem);
				gp.player().setVelocity(gp.player().getLocation().getDirection().multiply(2.0));
			}
		}
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
		dealDamage(3.4, pHit);
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
	}

	@Override
	public void onTick() {
		if (!gp.player().isOnGround())
			gp.player().setFallDistance(0.1f);
		if (++timer >= 400) {
			timer = 0;
			PlayerInventory pi = gp.player().getInventory();
			ItemStack is = pi.getItem(1);
			ItemStack newIs = new ItemStack(jumps);
			newIs.setAmount(is == null ? 1 : is.getAmount() + 1);
			pi.setItem(1, newIs);
		}
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(5)) {
		case 0:
			return killed + " tried to play devil's advocate with " + killer;
		case 1:
			return killed + " was lept over by " + killer;
		case 2:
			return killed + " took a few too many snowballs to the face by " + killer;
		case 3:
			return killed + " could not endure " + killer + "'s gracefulness";
		case 4:
			return killed + " wore black to " + killer + "'s wedding";
		}
		
		return "";
	}

}

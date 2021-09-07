package com.marks.kitpvp.kits;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.marks.kitpvp.game.GamePlayer;

public class KitTearjerker extends Kit {
	
	int cooldown;
	
	private final double HOOK_MULT = 1.5;
	
	private final ItemStack fishOn = KitUtils.makeItem(Material.CARROT_STICK, (short) 0, 1, false, ChatColor.GRAY + "Tearjerker", "Yikes! In the Yard");
	private final ItemStack fishOff = KitUtils.makeItem(Material.FISHING_ROD, (short) 0, 1, false, "Expended Tearjerker", "Deals no damage; wait!!!", "DO NOT right click this rod!", "Doing so will reset your cooldown.");
	private final ItemStack sword = KitUtils.makeWeapon(Material.WOOD_SWORD, (short) 0, 1, false, "The Finisher", 3, 0.8, "It never dealt that much damage.");
	
	private final ItemStack pot1 = KitUtils.makePotion(Material.SPLASH_POTION, 2, new PotionEffect(PotionEffectType.SLOW, 500, 1), Color.fromRGB(163, 163, 163), "Iron Ball", "How heavy!");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Mystery Boots", Color.fromRGB(0, 0, 0), true),
			KitUtils.makeArmor(1, "Blood Leggings", Color.fromRGB(99, 27, 27), true),
			KitUtils.makeArmor(2, "Steel Chestplate", Color.fromRGB(229, 229, 229), true),
			KitUtils.makeArmor(3, "Crybaby Helmet", Color.fromRGB(222, 248, 249), true)
	};

	public KitTearjerker(GamePlayer gp) {
		super(gp);
	}

	@Override
	public double getArmorPerc() {
		return 0.5;
	}

	@Override
	public double getKnockBackRes() {
		return 0.8;
	}

	@Override
	public void setInventory() {
		cooldown = 0;
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, fishOn);
		pi.setItem(1, sword);
		pi.setItem(3, pot1);
		gp.player().setWalkSpeed(0.19f);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
		if (!isRightClick && cooldown == 0 && gp.player().getInventory().getHeldItemSlot() == 0) {
			gp.player().getInventory().setItem(0, fishOff);
			cooldown = 80;
		}
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		int slot = gp.player().getInventory().getHeldItemSlot();
		if (slot == 0 && cooldown == 0) {
			dealDamage(10, pHit);
			dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), 1), pHit);
		}
		else if (slot == 1) {
			dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage / 3.0), pHit);
			dealDamage(damage, pHit);
		}
	}
	
	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
		Location pLoc = gp.player().getLocation(), hLoc = pHit.getLocation();
		double x = pLoc.getX() - hLoc.getX(), y = pLoc.getY() - hLoc.getY(), z = pLoc.getZ() - hLoc.getZ(),
				hyp = Math.sqrt(x * x + y * y + z * z);
		x /= hyp * HOOK_MULT;
		y /= hyp * HOOK_MULT + 0.2;
		z /= hyp * HOOK_MULT;
		
		pHit.setVelocity(new Vector(x, y, z));
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
		gp.player().sendMessage("DEBUG MSG: Launched Projectile.");
		p.setVelocity(p.getVelocity().multiply(1.3));
	}

	@Override
	public void onTick() {
		if (cooldown > 0 && --cooldown == 0)
			gp.player().getInventory().setItem(0, fishOn);
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(5)) {
		case 0:
			return killed + "'s tears were unleashed by " + killer;
		case 1:
			return killed + " died at the hook of " + killer + "'s hostile fishing rod";
		case 2:
			return killed + " took " + killer + "'s carrot bait";
		case 3:
			return killed + " couldn't stop " + killer + "'s unstoppable debuffs";
		case 4:
			return killed + "'s soul and dreams were destroyed by " + killer;
		}
		
		return "";
	}

}

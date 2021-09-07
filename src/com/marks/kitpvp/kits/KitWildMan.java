package com.marks.kitpvp.kits;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.ChatColor;

import com.marks.kitpvp.game.GamePlayer;

public class KitWildMan extends Kit {
	
	private final ItemStack pot3 = KitUtils.makePotion(Material.SPLASH_POTION, 1, new PotionEffect(PotionEffectType.HARM, 1, 2), Color.fromRGB(0, 0, 0), "Tier III Toxin", "Deals 6 hearts of damage! :o");
	private final ItemStack pot2 = KitUtils.makePotion(Material.SPLASH_POTION, 13, new PotionEffect(PotionEffectType.HARM, 1, 1), Color.fromRGB(198, 0, 0), "Tier II Toxin", "Deals 4 hearts of damage.");
	private final ItemStack pot1 = KitUtils.makePotion(Material.SPLASH_POTION, 55, new PotionEffect(PotionEffectType.HARM, 1, 0), Color.fromRGB(185, 0, 226), "Tier I Toxin", "Deals 2 hearts of damage.");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Uggs", Color.fromRGB(185, 164, 193), true),
			null,
			null,
			KitUtils.makeArmor(3, "Baseball Cap", Color.fromRGB(122, 83, 0), false)
	};

	public KitWildMan(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Wildman");
	}

	@Override
	public double getArmorPerc() {
		return 1.2;
	}

	@Override
	public double getKnockBackRes() {
		return 1.0;
	}

	@Override
	public void setInventory() {
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, pot1);
		pi.setItem(1, pot2);
		pi.setItem(2, pot3);
		gp.player().setWalkSpeed(0.22f);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		dealDamage(damage, pHit);
		dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage), pHit);
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
		p.setVelocity(p.getVelocity().multiply(1.5));
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
			return killed + " melted into gravy by " + killer;
		case 1:
			return killed + " dissolved from " + killer + "'s hazardous chemicals";
		case 2:
			return killed + " ran to the center of " + killer + "'s harming potion";
		case 3:
			return killed + " wasn't wild enough for " + killer;
		case 4:
			return killed + " didn't bring a potion to " + killer + "'s potion fight";
		}
		
		return "";
	}

}

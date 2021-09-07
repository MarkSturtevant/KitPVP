package com.marks.kitpvp.kits;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;
import com.marks.kitpvp.game.GameUtil;

public class KitAlien extends Kit {
	
	int timer;
	
	private final ItemStack twig = KitUtils.makeWeapon(Material.STICK, (short) 0, 1, false, "Twig", 2, 1.1, "Very weak.");
	private final ItemStack reddeath = KitUtils.makeItem(Material.INK_SACK, (short) 1, 1, true, "Red Death", "Others shall lose their hearts!");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Alienis Boots", Color.fromRGB(222, 56, 237), true),
			KitUtils.makeArmor(1, "Alienis Leggings", Color.fromRGB(222, 56, 237), true),
			KitUtils.makeArmor(2, "Alienis Chestplate", Color.fromRGB(222, 56, 237), true),
			KitUtils.makeArmor(3, "Alienis Helmet", Color.fromRGB(222, 56, 237), true)
	};

	public KitAlien(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Alien");
	}

	@Override
	public double getArmorPerc() {
		return 0.9;
	}

	@Override
	public double getKnockBackRes() {
		return 1.0;
	}

	@Override
	public void setInventory() {
		timer = 0;
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, twig);
		pi.setItem(1, reddeath);
		gp.player().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 9999999, 2, false, false), true);
		gp.player().setWalkSpeed(0.17f);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
		if (isRightClick && timer == 0) {
			PlayerInventory pi = gp.player().getInventory();
			if (pi.getHeldItemSlot() == 1) {
				pi.setItem(1, null);
				timer = 400;
				for (GamePlayer gp2 : Game.players)
					if (gp2.teamID != gp.teamID) {
						gp2.damage(0, gp);
						gp2.player().playSound(gp.player().getLocation(), Sound.ENTITY_ENDERMEN_DEATH, 10, 1);
						if (gp2.kit() instanceof KitAlien)
							GameUtil.changePlayerHealth(gp2, -6);
						else GameUtil.changePlayerHealth(gp2, -4);
					}
			}
		}

	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		dealDamage(damage, pHit);
		dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage / 1.2), pHit);
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
	}

	@Override
	public void onTick() {
		if (timer > 0 && --timer == 0) {
			gp.player().getInventory().setItem(1, reddeath);
		}
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(5)) {
		case 0:
			return killed + " couldn't figure out " + killer + "'s fourth dimension";
		case 1:
			return killed + " died to the superior species of " + killer;
		case 2:
			return killed + " became infected with the red death from " + killer;
		case 3:
			return killed + " slowly dissipated as a result of " + killer;
		case 4:
			return killed + " forgot to track down " + killer;
		}
		
		return "";
	}

}

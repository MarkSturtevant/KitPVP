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

import com.marks.kitpvp.game.GamePlayer;
import com.marks.kitpvp.game.GameUtil;

public class KitGhoul extends Kit {
	
	private int countdown;
	private boolean invisible;
	
	private final ItemStack club = KitUtils.makeWeapon(Material.BONE, (short) 0, 1, true, "Bone Club", 7, 0.8, "Let's get spooky!");
	private final ItemStack invis = KitUtils.makeItem(Material.STRING, (short) 0, 1, true, "Invisibility", "Right-Click to go invisible!");
	private final ItemStack spook = KitUtils.makeItem(Material.JACK_O_LANTERN, (short) 0, 1, true, "Spook", "Jump-Scare your opponent!");
	
	private final ItemStack[] armor = new ItemStack[] {
			null,
			null,
			KitUtils.makeArmor(2, "Soulless Piece", Color.fromRGB(0, 0, 0), true),
			KitUtils.makeItem(Material.SKULL_ITEM, (short) 1, 1, true, "Ghoul Head", "Spooooooooooooky!")
	};

	public KitGhoul(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Ghoul");
	}

	@Override
	public double getArmorPerc() {
		return 0.8;
	}

	@Override
	public double getKnockBackRes() {
		return 1.2;
	}

	@Override
	public void setInventory() {
		countdown = 0;
		invisible = false;
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, club);
		pi.setItem(1, invis);
		pi.setItem(2, spook);
		gp.player().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, 2, false, false), true);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
		PlayerInventory pi = gp.player().getInventory();
		int selSlot = pi.getHeldItemSlot();
		ItemStack selItem = pi.getItemInMainHand();
		if (!isRightClick)
			return;
		if (countdown == 0) {
			if (selSlot == 1 && selItem != null && selItem.getAmount() >= 1) {
				selItem.setAmount(selItem.getAmount() - 1);
				pi.setItem(selSlot, selItem);
				pi.setArmorContents(new ItemStack[] {null, null, null, null});
				countdown = 500;
				invisible = true;
				gp.player().setWalkSpeed(0.26f);
			}
		}
		if (selSlot == 2 && selItem != null && selItem.getAmount() >= 1) {
			selItem.setAmount(selItem.getAmount() - 1);
			pi.setItem(selSlot, selItem);
			for (GamePlayer gp2 : GameUtil.getAlivePlayers())
				if (gp2.player().getLocation().distance(gp.player().getLocation()) < 7 && gp2 != gp) {
					gp2.damage(6.0, gp);
					gp2.player().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 2));
					gp2.player().playSound(gp2.player().getLocation(), Sound.ENTITY_ENDERMEN_HURT, 10.0f, 1.0f);
				}
		}
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		dealDamage(damage, pHit);
		dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage / 7.0 * 2.0), pHit);
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
	}

	@Override
	public void onTick() {
		if (countdown > 0 && --countdown == 0) {
			if (invisible) {
				gp.player().getInventory().setArmorContents(armor);
				gp.player().setWalkSpeed(0.23f);
				gp.player().sendMessage(ChatColor.ITALIC + "" + ChatColor.GRAY + "Your invisibility phase has ended.");
				invisible = false;
				countdown = 400;
			} else {
				gp.player().getInventory().setItem(1, invis);
			}
		}
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(5)) {
		case 0:
			return killed + " was clubbed to death by " + killer;
		case 1:
			return killed + " was horrifically spooked by " + killer;
		case 2:
			return killed + " fainted from " + killer + "'s surprise party";
		case 3:
			return killed + " couldn't handle " + killer + "'s scary costume";
		case 4:
			return killed + " fell victim to " + killer + "'s invisibility tricks";
		}
		
		return "";
	}

}

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
import org.bukkit.util.Vector;

import com.marks.kitpvp.game.GamePlayer;

public class KitElectroBowman extends Kit {
	
	private int countdown;

	private final ItemStack bow = KitUtils.makeItem(Material.BOW, (short) 0, 1, true, "Electric Bow", "Electrickery!");
	private final ItemStack arrow = KitUtils.makeItem(Material.ARROW, (short) 0, 1, true, "Conductive Arrow", "Brings out the ZAP!");
	private final ItemStack electroBoost = KitUtils.makeItem(Material.PRISMARINE_CRYSTALS, (short) 0, 1, true, ChatColor.AQUA + "Electro-Boost", "Zip around fast!");
	private final ItemStack zap = KitUtils.makeItem(Material.INK_SACK, (short) 12, 1, true, ChatColor.AQUA + "Mega Zap", "Paralyzes your opponent!");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Aquamarine Boots", Color.fromRGB(93, 186, 239), true),
			KitUtils.makeArmor(1, "Aquamarine Leggings", Color.fromRGB(33, 91, 239), true),
			KitUtils.makeArmor(2, "Aquamarine Chestplate", Color.fromRGB(2, 23, 214), true),
			KitUtils.makeArmor(3, "Lightning Rod", Color.fromRGB(61, 0, 81), true)
	};
	
	public KitElectroBowman(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Electro Bowman");
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
		countdown = 0;
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, bow);
		pi.setItem(1, electroBoost);
		pi.setItem(2, zap);
		pi.setItem(17, arrow);
		gp.player().setWalkSpeed(0.22f);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
		if (isRightClick) {
			PlayerInventory pi = gp.player().getInventory();
			int selSlot = pi.getHeldItemSlot();
			ItemStack selItem = pi.getItemInMainHand();
			if (selSlot == 1 && selItem != null && selItem.getAmount() >= 1) {
				pi.remove(selItem);
				gp.player().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
				gp.player().playSound(gp.player().getLocation(), Sound.ITEM_BUCKET_FILL, 10, 1);
			}
		}
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		dealDamage(damage, pHit);
		dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), 2), pHit);
		PlayerInventory pi = gp.player().getInventory();
		int selSlot = pi.getHeldItemSlot();
		ItemStack selItem = pi.getItemInMainHand();
		if (selSlot == 2 && selItem != null && selItem.getAmount() >= 1) {
			pi.remove(selItem);
			countdown = 600;
			pHit.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 110, 129), true);
			pHit.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 110, 99), true);
			pHit.getWorld().strikeLightningEffect(pHit.getLocation());
		}
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
		if (p.getCustomName() == null)
			return;
		double powPerc = Double.valueOf(p.getCustomName()) / 3.0;
		dealDamage(8.1 * powPerc, pHit);
		pHit.setVelocity(new Vector(0, 0.12, 0));
		pHit.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 13, 1), false);
		pHit.getWorld().strikeLightningEffect(pHit.getLocation());
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
		p.setCustomName(String.valueOf(p.getVelocity().length()));
		if (p.getVelocity().length() < 1.51)
			p.remove();
	}

	@Override
	public void onTick() {
		if (countdown > 0 && --countdown == 0) {
			gp.player().getInventory().setItem(2, zap);
		}
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(5)) {
		case 0:
			return killed + " met their end by " + killer + " with a great ZAP!";
		case 1:
			return killed + " got electrocuted by " + killer;
		case 2:
			return killed + " was literally struck down by " + killer;
		case 3:
			return killed + " touched the tip of " + killer + "'s electric arrow";
		case 4:
			return killed + " couldn't handle " + killer + "'s immense electron flow";
		}
		
		return "";
	}

}

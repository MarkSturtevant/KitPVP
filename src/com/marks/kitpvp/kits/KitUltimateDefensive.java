package com.marks.kitpvp.kits;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import com.marks.kitpvp.game.GamePlayer;

public class KitUltimateDefensive extends Kit {

	private final ItemStack hoeboiler = KitUtils.makeWeapon(Material.WOOD_HOE, (short) 0, 1, false, "Hoeboiler", 1, 0.5, "This gardening tool does more", "than just till dirt!");
	private final ItemStack shield = KitUtils.makeItem(Material.SHIELD, (short) 0, 1, true, "Magical Shield", "Only has 3 uses!");
	private final ItemStack bow = KitUtils.makeItem(Material.BOW, (short) 0, 1, true, "Cowardly Bow", "It shoots stuff", "(Pretty Cowardly imo)");
	private final ItemStack arrow = KitUtils.makeItem(Material.ARROW, (short) 0, 1, false, "Spikey Arrow", "Don't Touch It!");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Defensive Boots", Color.fromRGB(178, 178, 178), false),
			KitUtils.makeArmor(1, "Defensive Leggings", Color.fromRGB(178, 178, 178), false),
			KitUtils.makeArmor(2, "Defensive Chestplate", Color.fromRGB(178, 178, 178), false),
			KitUtils.makeArmor(3, "Defensive Helmet", Color.fromRGB(178, 178, 178), false)
	};
	
	public KitUltimateDefensive(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Ultimate Defensive");
		ItemMeta im = shield.getItemMeta();
		im.setUnbreakable(false);
		shield.setItemMeta(im);
	}

	@Override
	public double getArmorPerc() {
		HumanEntity he = (HumanEntity) gp.player();
		if (he.isBlocking()) {
			ItemStack shield = gp.player().getInventory().getItemInMainHand();
			if (!shield.getType().equals(Material.SHIELD))
				return 0.75;
			short durability = shield.getDurability();
			if ((durability += 113) == 339)
				gp.player().getInventory().setItemInMainHand(null);
			else {
				shield.setDurability(durability);
				gp.player().getInventory().setItemInMainHand(shield);
			}
			return 0.0;
		}
		return 0.75;
	}

	@Override
	public double getKnockBackRes() {
		HumanEntity he = (HumanEntity) gp.player();
		if (he.isBlocking())
			return 0.0;
		return 1.0;
	}

	@Override
	public void setInventory() {
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, hoeboiler);
		pi.setItem(1, shield);
		pi.setItem(2, bow);
		pi.setItem(17, arrow);
		gp.player().setWalkSpeed(0.20f);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		dealDamage(damage, pHit);
		if (gp.player().getInventory().getHeldItemSlot() == 0)
			dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage * 13.0), pHit);
		else dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage), pHit);
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
		if (p.getCustomName() == null)
			return;
		double powPerc = Double.valueOf(p.getCustomName()) / 4.0;
		dealDamage(8.0 * powPerc, pHit);
		dealKnockback(GamePlayer.getKnockbackVector(p.getVelocity(), 3.1 * powPerc), pHit);
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
		p.setCustomName(String.valueOf(p.getVelocity().length()));
		if (p.getVelocity().length() < 1.51)
			p.remove();
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
			return killed + " was unable to overwhelm " + killer + "'s defense";
		case 1:
			return killed + " became " + killer + "'s new quiver";
		case 2:
			return killed + " figured out " + killer + "'s point";
		case 3:
			return killed + " took an arrow to the knee by " + killer;
		case 4:
			return killed + "'s best attack efforts were minimized by " + killer;
		}
		
		return "";
	}

}

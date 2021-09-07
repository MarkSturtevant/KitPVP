package com.marks.kitpvp.kits;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.ChatColor;

import com.marks.kitpvp.game.GamePlayer;

public class KitNeoArcher extends Kit {
	
	private final ItemStack sword = KitUtils.makeWeapon(Material.IRON_SWORD, (short) 0, 1, false, "Glorious Sword", 7, 0.7, "Start a revolution!");
	private final ItemStack bow = KitUtils.makeItem(Material.BOW, (short) 0, 1, true, "Ancient Bow", "Only use it for good causes!");
	private final ItemStack arrow = KitUtils.makeItem(Material.ARROW, (short) 0, 1, false, "Ancient Arrow", "I think it's broken?");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Helmet Scraps", Color.fromRGB(109, 40, 0), false),
			KitUtils.makeArmor(1, "Deteriorated Tunic", Color.fromRGB(109, 40, 0), false),
			KitUtils.makeArmor(2, "Pants with Holes", Color.fromRGB(109, 40, 0), false),
			KitUtils.makeArmor(3, "Run-Down War Boots", Color.fromRGB(109, 40, 0), false)
	};

	public KitNeoArcher(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Neo Archer");
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
		pi.setItem(1, bow);
		pi.setItem(17, arrow);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		dealDamage(damage, pHit);
		if (gp.player().getInventory().getHeldItemSlot() == 0)
			dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage / 7.0 * 2.0), pHit);
		else dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage), pHit);
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
		if (p.getCustomName() == null)
			return;
		double powPerc = Double.valueOf(p.getCustomName()) / 3.0;
		dealDamage(6.1 * powPerc, pHit);
		dealKnockback(GamePlayer.getKnockbackVector(p.getVelocity(), 2.0 * powPerc), pHit);
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
		switch(new Random().nextInt(4)) {
		case 0:
			return killed + " was ended during " + killer + "'s revolution";
		case 1:
			return killed + " fell due to " + killer + "'s sword and bow combo";
		case 2:
			return killed + " was shot and stabbed by " + killer;
		case 3:
			return killed + " died to " + killer + "'s traditional Minecraft weapons";
		}
		
		return "";
	}

}

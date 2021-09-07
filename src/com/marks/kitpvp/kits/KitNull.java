package com.marks.kitpvp.kits;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

import com.marks.kitpvp.game.GamePlayer;

public class KitNull extends Kit {
	
	public KitNull(GamePlayer p) {
		super(p);
		p.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Null");
	}
	
	@Override
	public double getArmorPerc() {
		return 1.0;
	}
	
	@Override
	public double getKnockBackRes() {
		return 1.0;
	}

	@Override
	public void setInventory() {
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
	}

	@Override
	public void onHitEvent(LivingEntity eHit, double damage) {
		dealDamage(damage, eHit);
		dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage), eHit);
	}

	@Override
	public void onTick() {
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		return ChatColor.RED + nameKilled + ChatColor.WHITE + " somehow died to " + ChatColor.AQUA + gp.player().getDisplayName() + 
				ChatColor.WHITE + ", who literally had nothing";
	}

}

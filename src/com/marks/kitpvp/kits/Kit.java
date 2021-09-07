package com.marks.kitpvp.kits;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public abstract class Kit {
	
	protected GamePlayer gp;
	
	public Kit(GamePlayer gp) {
		this.gp = gp;
	}
	
	public abstract double getArmorPerc();
	public abstract double getKnockBackRes();
	public abstract void setInventory();
	public abstract void onClickEvent(boolean isRightClick);
	public abstract void onHitEvent(LivingEntity pHit, double damage);
	public abstract void onProjectileHitEvent(LivingEntity pHit, Projectile p);
	public abstract void onProjectileLaunchEvent(Projectile p);
	public abstract void onTick();
	public abstract String createDeathMessage(String nameKilled);
	
	protected void dealDamage(double amt, LivingEntity le) {
		if (le instanceof Player) {
			GamePlayer gp2 = Game.getGamePlayer((Player) le);
			if (gp2 == null)
				le.damage(amt);
			else gp2.damage(amt, gp);
		}
		else le.damage(amt);
	}
	
	protected void dealKnockback(Vector dir, LivingEntity le) {
		if (le instanceof Player) {
			GamePlayer gp2 = Game.getGamePlayer((Player) le);
			if (gp2 == null)
				le.setVelocity(le.getVelocity().add(dir));
			else gp2.knockback(dir);
		}
		else le.setVelocity(le.getVelocity().add(dir));
	}
}

package com.marks.kitpvp.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class EventProjectiles implements Listener {

	@EventHandler
	public void projectileLaunch(ProjectileLaunchEvent e) {
		if (!Game.getInGame() || !(e.getEntity().getShooter() instanceof Player))
			return;
		GamePlayer gp = Game.getGamePlayer((Player) e.getEntity().getShooter());
		if (gp != null)
			gp.kit().onProjectileLaunchEvent(e.getEntity());
	}
	
	@EventHandler
	public void projectileHit(ProjectileHitEvent e) {
		if (!Game.getInGame() || !(e.getEntity().getShooter() instanceof Player))
			return;
		ProjectileSource shooter = e.getEntity().getShooter();
		Entity hitEntity = e.getHitEntity();
		GamePlayer pShooter = Game.getGamePlayer((Player) shooter);
		if (pShooter == null)
			return;
		if (hitEntity instanceof LivingEntity) {
			pShooter.kit().onProjectileHitEvent((LivingEntity) hitEntity, e.getEntity());
		}
		e.getEntity().remove();
	}
	
	@EventHandler
	public void potionHit(PotionSplashEvent e) {
		if (!Game.getInGame() || !(e.getPotion().getShooter() instanceof Player))
			return;
		GamePlayer thrower = Game.getGamePlayer((Player) e.getPotion().getShooter());
		boolean isHarming = false; double damage = 0.0;
		for (PotionEffect pe : e.getPotion().getEffects())
			if (pe.getType().equals(PotionEffectType.HARM)) {
				isHarming = true;
				damage = 4 * pe.getAmplifier() + 4;
			}
		if (thrower == null || !isHarming)
			return;
		for (LivingEntity le : e.getAffectedEntities()) {
			if (le instanceof Player) {
				GamePlayer hitgp = Game.getGamePlayer((Player) le);
				if (hitgp != null) {
					hitgp.damage(e.getIntensity(le) * damage / hitgp.kit().getArmorPerc(), thrower);
					if (!hitgp.equals(thrower))
						hitgp.knockback(GamePlayer.getKnockbackVector(new Vector(le.getLocation().getX() - e.getPotion().getLocation().getX(), 0.2, le.getLocation().getZ() - e.getPotion().getLocation().getZ()), 2.5 * e.getIntensity(le)));
				}
			} else {
				le.damage(e.getIntensity(le) * damage);
				le.setVelocity(GamePlayer.getKnockbackVector(new Vector(le.getLocation().getX() - e.getPotion().getLocation().getX(), 0.2, le.getLocation().getZ() - e.getPotion().getLocation().getZ()), 2.5 * e.getIntensity(le)));
			}
		}
		e.setCancelled(true);
	}
	
}

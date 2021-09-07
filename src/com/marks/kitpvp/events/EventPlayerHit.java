package com.marks.kitpvp.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;
import com.marks.kitpvp.game.GameUtil;

public class EventPlayerHit implements Listener {

	@EventHandler
	public void entityDamage(EntityDamageByEntityEvent e) {
		if (!Game.getInGame())
			return;
		Entity damager = e.getDamager();
		Entity damaged = e.getEntity();
		if (damager instanceof Player) {
			GamePlayer pHitter = Game.getGamePlayer((Player) damager);
			if (!(damaged instanceof LivingEntity))
				return;
			e.setCancelled(true);
			pHitter.kit().onHitEvent((LivingEntity) damaged, e.getDamage());
			pHitter.kit().onClickEvent(false);
		} else if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player)
			e.setCancelled(true);
		else if (damaged instanceof Player) {
			GamePlayer pHit = Game.getGamePlayer((Player) damaged);
			if (pHit == null)
				return;
			e.setCancelled(true);
			if (damager instanceof Zombie && damager.getCustomName() != null)
				pHit.damage(e.getDamage(), GameUtil.getFromName(damager.getCustomName()));
			pHit.damage(e.getDamage(), null);
		}
			
	}
	
}

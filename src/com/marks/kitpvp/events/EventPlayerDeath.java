package com.marks.kitpvp.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.marks.kitpvp.Main;
import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class EventPlayerDeath implements Listener {

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if (!Game.getInGame())
			return;
		GamePlayer gp = Game.getGamePlayer(e.getPlayer());
		if (gp != null)
			Main.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
				@Override
				public void run() {
					Game.onRespawn(gp);
				}
			}, 1);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (Game.getInQueue())
			Game.removePlayer(e.getEntity());
		if (!Game.getInGame())
			return;
		GamePlayer gp = Game.getGamePlayer(e.getEntity());
		if (gp != null) {
			Game.onDeath(gp);
			e.setDeathMessage(null);
		}
	}
	
}

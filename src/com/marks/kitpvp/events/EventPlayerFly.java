package com.marks.kitpvp.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class EventPlayerFly implements Listener {
	
	@EventHandler
	public void onPlayerFly(PlayerToggleFlightEvent e) {
		if (Game.getInGame()) {
			Player p = e.getPlayer();
			GamePlayer gp = Game.getGamePlayer(p);
			if (gp == null)
				return;
			e.setCancelled(true);
			gp.tryToDoubleJump();
		}
	}
}

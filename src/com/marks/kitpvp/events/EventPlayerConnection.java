package com.marks.kitpvp.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.marks.kitpvp.game.Game;

public class EventPlayerConnection implements Listener {
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (Game.getInGame() || Game.getInQueue())
			Game.removePlayer(e.getPlayer());
	}
}

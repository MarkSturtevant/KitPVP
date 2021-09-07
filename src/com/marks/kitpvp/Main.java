package com.marks.kitpvp;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.marks.kitpvp.commands.CommandKitPVP;
import com.marks.kitpvp.events.EventPlayerClick;
import com.marks.kitpvp.events.EventPlayerConnection;
import com.marks.kitpvp.events.EventPlayerDeath;
import com.marks.kitpvp.events.EventPlayerFly;
import com.marks.kitpvp.events.EventPlayerHit;
import com.marks.kitpvp.events.EventProjectiles;
import com.marks.kitpvp.game.Game;

public class Main extends JavaPlugin {
	
	private static Plugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		Game.init();
		registerCommands();
		registerEvents();
	}
	
	@Override
	public void onDisable() {
		if (Game.getInGame())
			Game.end();
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
	private void registerCommands() {
		this.getCommand("kitpvp").setExecutor(new CommandKitPVP());
	}
	
	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new EventPlayerConnection(), this);
		getServer().getPluginManager().registerEvents(new EventPlayerFly(), this);
		getServer().getPluginManager().registerEvents(new EventPlayerDeath(), this);
		getServer().getPluginManager().registerEvents(new EventPlayerHit(), this);
		getServer().getPluginManager().registerEvents(new EventPlayerClick(), this);
		getServer().getPluginManager().registerEvents(new EventProjectiles(), this);
	}

}

package com.marks.kitpvp.arenas;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;
import com.marks.kitpvp.game.GameUtil;

public class ArenaSoscienskeStation extends Arena {

	public ArenaSoscienskeStation(World w) {
		super(w);
	}
	
	@Override
	public String getName() {
		return "Soscienske Station";
	}

	@Override
	public int[] getSpawnPoints() {
		if (Game.suddenDeath)
			return new int[] {942, 130, 29};
		return new int[] {947, 130, 36, 953, 130, 41, 946, 130, 46, 941, 130, 41, 942, 130, 29};
	}

	@Override
	public void onStartUp() {
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onTick() {
	}

	@Override
	public void onSuddenDeath() {
		Game.broadcastMessage(ChatColor.GREEN + "Good luck.");
		for (GamePlayer gp : GameUtil.getAlivePlayers())
			gp.player().teleport(new Location(world, 942, 130, 29));
	}

}

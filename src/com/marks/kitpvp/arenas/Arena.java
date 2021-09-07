package com.marks.kitpvp.arenas;

import org.bukkit.World;

public abstract class Arena {
	
	protected World world;
	
	public Arena(World w) {
		this.world = w;
	}

	public abstract int[] getSpawnPoints();
	public abstract void onStartUp();
	public abstract void onEnd();
	public abstract void onTick();
	public abstract void onSuddenDeath();
	public abstract String getName();
	
}

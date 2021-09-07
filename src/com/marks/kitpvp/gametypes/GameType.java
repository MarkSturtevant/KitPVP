package com.marks.kitpvp.gametypes;

import com.marks.kitpvp.game.GamePlayer;

public abstract class GameType {
	
	public abstract String name();
	
	public abstract void onStart();
	public abstract void removePlayer(GamePlayer gp);
	public abstract void onDeath(GamePlayer gp);
	public abstract boolean onRespawn(GamePlayer gp);
	public abstract void endGame();
	
}

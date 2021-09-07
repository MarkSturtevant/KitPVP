package com.marks.kitpvp.arenas;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class ArenaFactory extends Arena {
	
	private int timer;
	Random rand;
	
	public ArenaFactory(World w) {
		super(w);
	}
	
	@Override
	public String getName() {
		return "Factory";
	}

	@Override
	public int[] getSpawnPoints() {
		if (Game.suddenDeath)
			return new int[] {382, 101, 19, 432, 107, 12, 427, 101, 11, 397, 101, 2};
		return new int[] {432, 102, 11, 382, 102, 19, 417, 124, 18, 393, 124, 12, 382, 102, -1};
	}

	@Override
	public void onStartUp() {
		timer = 0;
		rand = new Random();
	}

	@Override
	public void onEnd() {}

	@Override
	public void onTick() {
		timer = ++timer % 10;
		if (timer == 0) {
			if (Game.suddenDeath) {
				if (rand.nextInt(4) == 1) {
					for (GamePlayer gp : Game.players)
						if (gp.player().getLocation().getY() > 110.0)
							world.strikeLightning(gp.player().getLocation());
				}
				else world.strikeLightning(new Location(world, rand.nextInt(15) + 400, 124, rand.nextInt(6) + 2));
			}
			else world.strikeLightning(new Location(world, rand.nextInt(3) + 431, 107, rand.nextInt(20) + 2));
		}
	}

	@Override
	public void onSuddenDeath() {
		Game.broadcastMessage(ChatColor.AQUA + "The \"contained\" lighting has escaped, and is striking the rooftop!");
	}

}

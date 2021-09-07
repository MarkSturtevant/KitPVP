package com.marks.kitpvp.arenas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;
import com.marks.kitpvp.game.GameUtil;

public class ArenaPyramid extends Arena {
	
	List<GamePlayer> gold, diamond, emerald;
	int[] pRep;
	int timer;
	Random rand;

	public ArenaPyramid(World w) {
		super(w);
	}

	@Override
	public int[] getSpawnPoints() {
		return new int[] {438, 125, 289, 484, 114, 333, 453, 116, 296, 492, 125, 289};
	}

	@Override
	public void onStartUp() {
		gold = new ArrayList<>();
		diamond = new ArrayList<>();
		emerald = new ArrayList<>();
		pRep = new int[Game.players.size()];
		for (int i = 0; i < Game.players.size(); i++)
			pRep[i] = 10;
		timer = 600;
		rand = new Random();
	}

	@Override
	public void onEnd() {
	}

	@Override
	public void onTick() {
		if (--timer == 0) {
			for (int i = 0; i < pRep.length; i++) {
				GamePlayer gp = Game.players.get(i);
				if (gp.player().getGameMode().equals(GameMode.SPECTATOR))
					continue;
				if (pRep[i] <= -40) {
					gp.player().sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Ivan is upset from your huge lack of gifts!");
					switch(rand.nextInt(4)) {
					case 0:
						GameUtil.changePlayerHealth(gp, -4);
						break;
					case 1:
						gp.player().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 2), true);
						gp.player().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 2), true);
						break;
					case 2:
						gp.player().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 4), true);
						break;
					case 3:
						if (gp.player().hasPotionEffect(PotionEffectType.REGENERATION))
							gp.player().removePotionEffect(PotionEffectType.REGENERATION);
					}
				} else if (pRep[i] < 0) {
					gp.player().sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "Ivan is upset from your lack of gifts!");
					switch(rand.nextInt(3)) {
					case 0:
						gp.player().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 2), true);
						break;
					case 1:
						gp.player().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 1), true);
						break;
					case 2:
						gp.player().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 0), true);
						break;
					}
				} else if (pRep[i] > 0) {
					gp.player().sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "Ivan is pleased from your gift giving!");
					switch(rand.nextInt(pRep[i] >= 70 ? 4 : 2)) {
					case 0:
						gp.player().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 0), true);
						break;
					case 1:
						gp.player().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 440, 1), true);
						break;
					case 2:
						GameUtil.changePlayerHealth(gp, 4);
						break;
					case 3:
						gp.player().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999999, 1), true);
					}
				}
				pRep[i] -= 10;
			}
			timer = 600;
		} else if (timer == 200) {
			for (int i = 0; i < pRep.length; i++) {
				GamePlayer gp = Game.players.get(i);
				if (gp.player().getGameMode().equals(GameMode.SPECTATOR))
					continue;
				ChatColor color = ChatColor.YELLOW;
				if (pRep[i] <= -40)
					color = ChatColor.DARK_RED;
				else if (pRep[i] < 0)
					color = ChatColor.RED;
				else if (pRep[i] > 0)
					color = ChatColor.GREEN;
				gp.player().sendMessage(ChatColor.BOLD + "Your reputation is " + color + pRep[i]);
			}
		}
		for (int i = 0; i < pRep.length; i++) {
			GamePlayer gp = Game.players.get(i);
			if (gp.player().getGameMode().equals(GameMode.SPECTATOR))
				continue;
			Location pLoc = gp.player().getLocation();
			switch(world.getBlockAt(pLoc.getBlockX(), pLoc.getBlockY() - 1, pLoc.getBlockZ()).getType()) {
			case REDSTONE_BLOCK:
				if (gold.contains(gp)) {
					gold.remove(gp);
					gp.player().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Ivan appreciates your gold! " + ChatColor.YELLOW + "+ 10 Reputation");
					pRep[i] += 10;
				}
				if (diamond.contains(gp)) {
					diamond.remove(gp);
					gp.player().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Ivan appreciates your diamond! " + ChatColor.AQUA + "+ 20 Reputation");
					pRep[i] += 20;
				}
				if (emerald.contains(gp)) {
					emerald.remove(gp);
					gp.player().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Ivan appreciates your emerald! " + ChatColor.GREEN + "+ 20 Reputation");
					pRep[i] += 20;
				}
				break;
			case GOLD_BLOCK:
				if (!gold.contains(gp)) {
					gold.add(gp);
					gp.player().sendMessage("Gold Treasure Aquired!");
				}
				break;
			case DIAMOND_BLOCK:
				if (!diamond.contains(gp)) {
					diamond.add(gp);
					gp.player().sendMessage("Diamond Treasure Aquired!");
				}
				break;
			case EMERALD_BLOCK:
				if (!emerald.contains(gp)) {
					emerald.add(gp);
					gp.player().sendMessage("Emerald Treasure Aquired!");
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public String getName() {
		return "Pyramid";
	}

	@Override
	public void onSuddenDeath() {
		Game.broadcastMessage(ChatColor.GOLD + "Everyone's reputation is now " + ChatColor.RED + "-10000");
		for (int i = 0; i < pRep.length; i++)
			pRep[i] = -10000;
	}

}

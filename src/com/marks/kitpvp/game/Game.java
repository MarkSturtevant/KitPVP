package com.marks.kitpvp.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.ChatColor;

import com.marks.kitpvp.Main;
import com.marks.kitpvp.arenas.Arena;
import com.marks.kitpvp.arenas.ArenaAscend;
import com.marks.kitpvp.arenas.ArenaCanyon;
import com.marks.kitpvp.arenas.ArenaColorSwitch;
import com.marks.kitpvp.arenas.ArenaFactory;
import com.marks.kitpvp.arenas.ArenaGreenVolcano;
import com.marks.kitpvp.arenas.ArenaPyramid;
import com.marks.kitpvp.arenas.ArenaSectorPlay;
import com.marks.kitpvp.arenas.ArenaSnowglobe;
import com.marks.kitpvp.arenas.ArenaSoscienskeStation;
import com.marks.kitpvp.arenas.ArenaSturtevantStadium;
import com.marks.kitpvp.arenas.ArenaTwister;
import com.marks.kitpvp.arenas.ArenaWaterfall;
import com.marks.kitpvp.gametypes.GTFiveKills;
import com.marks.kitpvp.gametypes.GTKnockback;
import com.marks.kitpvp.gametypes.GTLastUnderThreeLives;
import com.marks.kitpvp.gametypes.GTMirrorMatchup;
import com.marks.kitpvp.gametypes.GTNullProtect;
import com.marks.kitpvp.gametypes.GTOneLife;
import com.marks.kitpvp.gametypes.GTRandomKits;
import com.marks.kitpvp.gametypes.GTSuddenDeath;
import com.marks.kitpvp.gametypes.GTTOO;
import com.marks.kitpvp.gametypes.GTThreeLives;
import com.marks.kitpvp.gametypes.GameType;
import com.marks.kitpvp.kits.KitNull;

public class Game {

	private static int gameTaskId;
	private static boolean onGame;
	private static boolean onQueue;
	public static List<GamePlayer> players;
	public static boolean superKnockback;
	public static boolean suddenDeath;
	private static Arena arena;
	private static GameType gameType;
	private static Random rand;
	private static int remainingPlayers;
	public static int suddenDeathCountdown;
	
	public static void init() {
		onGame = onQueue = false;
		players = new ArrayList<>();
		arena = null;
		gameType = null;
		rand = new Random();
	}
	
	public static boolean start() {
		if (onGame || players.size() <= 1)
			return false;
		onQueue = false;
		onGame = true;
		arena.onStartUp();
		gameType.onStart();
		respawnRespectingTeams();
		gameTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
			arena.onTick();
			for (GamePlayer gp : players)
				gp.act();
			if (suddenDeathCountdown > 0) {
				if (--suddenDeathCountdown == 0) {
					Game.broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "SUDDEN DEATH >> Something with the arena has changed!");
					suddenDeath = true;
					arena.onSuddenDeath();
				} else if (suddenDeathCountdown == 300)
					Game.broadcastMessage(ChatColor.RED + "Sudden Death begins in 15 seconds!");
			}
		}, 0L, 1L);
		return true;
	}
	
	public static void queueup() {
		onQueue = true;
		arena = null;
		superKnockback = false;
		suddenDeath = false;
		suddenDeathCountdown = 6000;
		gameType = null;
		players.clear();
		for (Player p : Bukkit.getOnlinePlayers()) {
			GamePlayer gp = new GamePlayer(p);
			players.add(gp);
			p.teleport(new Location(p.getWorld(), 302, 107, 71));
			gp.resetPlayer();
			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 2));
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 6));
		}
		Collections.shuffle(players);
		remainingPlayers = players.size();
	}
	
	public static void onKitSelect(GamePlayer gp, String kitName) {
		if (!onQueue)
			return;
		//this method assumes Player p is already a GamePlayer.
		if (!(gp.kit() instanceof KitNull))
			return;
		gp.setKit(kitName);
		gp.player().teleport(new Location(gp.player().getWorld(), 302, 107, 71));
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
			if (gp.player().hasPotionEffect(PotionEffectType.INVISIBILITY))
				gp.player().removePotionEffect(PotionEffectType.INVISIBILITY);
		}, 2L);
		if (--remainingPlayers == 0) {
			int randInt = rand.nextInt(players.size());
			players.get(randInt).player().teleport(new Location(gp.player().getWorld(), 302, 97, 71));
			players.get(randInt == 0 ? players.size() - 1 : randInt - 1).player().teleport(new Location(gp.player().getWorld(), 302, 92, 71));
		}
	}
	
	public static void end() {
		Bukkit.getScheduler().cancelTask(gameTaskId);
		arena.onEnd();
		onGame = false;
		for (GamePlayer gp : players) {
			gp.resetPlayer();
			gp.player().setGameMode(GameMode.ADVENTURE);
			gp.player().teleport(new Location(gp.player().getWorld(), 0, 106, 0));
		}
		players.clear();
	}
	
	public static void onDeath(GamePlayer gp) {
		gp.deaths++;
		String deathMsg;
		GamePlayer cause = gp.getLastDamageCause();
		if (cause != null) {
			cause.kills++;
			deathMsg = cause.kit().createDeathMessage(gp.player().getDisplayName());
		} else deathMsg = ChatColor.RED + gp.player().getDisplayName() + ChatColor.WHITE + " suffered a tragic death";
		Game.broadcastMessage(deathMsg);
		gameType.onDeath(gp);
	}
	
	public static void onRespawn(GamePlayer gp) {
		if (gameType.onRespawn(gp))
			respawnPlayer(gp);
		else {
			gp.player().setGameMode(GameMode.SPECTATOR);
			for (GamePlayer gp2 : players)
				if (!gp2.equals(gp)) {
					gp.player().teleport(gp2.player());
					break;
				}
		}
	}
	
	public static void respawnPlayer(GamePlayer gp) {
		gp.resetPlayer();
		gp.kit().setInventory();
		int[] spawnpoints = arena.getSpawnPoints();
		int mult = rand.nextInt(spawnpoints.length / 3);
		gp.player().teleport(new Location(gp.player().getWorld(), spawnpoints[3 * mult] + 0.5, spawnpoints[3 * mult + 1] + 0.5, spawnpoints[3 * mult + 2] + 0.5));
	}
	
	private static void respawnRespectingTeams() {
		int[] spawnpoints = arena.getSpawnPoints();
		int mult = rand.nextInt(spawnpoints.length / 3);
		for (GamePlayer gp : Game.players) {
			gp.resetPlayer();
			gp.kit().setInventory();
			int pMult = mult + gp.teamID - 1 - (spawnpoints.length / 3 * ((int) (mult + gp.teamID - 1) / (spawnpoints.length / 3)));
			gp.player().teleport(new Location(gp.player().getWorld(), spawnpoints[3 * pMult], spawnpoints[3 * pMult + 1], spawnpoints[3 * pMult + 2]));
		}
	}
	
	public static void removePlayer(Player p) {
		GamePlayer gp = getGamePlayer(p);
		if (gp == null)
			return;
		players.remove(gp);
		if (onQueue) {
			for (GamePlayer gps : players) {
				gps.resetPlayer();
				gps.player().teleport(new Location(gps.player().getWorld(), 0, 106, 0));
				gps.player().sendMessage("Someone left, so the game was canceled.");
			}
			players.clear();
		} else gameType.removePlayer(gp);
	}
	
	public static GamePlayer getGamePlayer(Player p) {
		for (GamePlayer pl : players)
			if (p.equals(pl.player()))
				return pl;
		return null;
	}
	
	public static boolean getInGame() {
		return onGame;
	}
	public static boolean getInQueue() {
		return onQueue;
	}
	
	public static void setArena(int id) {
		switch(id) {
		case 0:
			arena = new ArenaFactory(players.get(0).player().getWorld());
			break;
		case 1:
			arena = new ArenaColorSwitch(players.get(0).player().getWorld());
			break;
		case 2:
			arena = new ArenaSnowglobe(players.get(0).player().getWorld());
			break;
		case 3:
			arena = new ArenaSectorPlay(players.get(0).player().getWorld());
			break;
		case 4:
			arena = new ArenaGreenVolcano(players.get(0).player().getWorld());
			break;
		case 5:
			arena = new ArenaCanyon(players.get(0).player().getWorld());
			break;
		case 6:
			arena = new ArenaPyramid(players.get(0).player().getWorld());
			break;
		case 7:
			arena = new ArenaWaterfall(players.get(0).player().getWorld());
			break;
		case 8:
			arena = new ArenaAscend(players.get(0).player().getWorld());
			break;
		case 9:
			arena = new ArenaSturtevantStadium(players.get(0).player().getWorld());
			break;
		case 10:
			arena = new ArenaTwister(players.get(0).player().getWorld());
			break;
		case 11:
			//arena = new ArenaTwister(players.get(0).player().getWorld());
			break;
		case 12:
			arena = new ArenaSoscienskeStation(players.get(0).player().getWorld());
			break;
		case -1:
			setArena(rand.nextInt(11));
			break;
		default:
			return;
		}
		broadcastMessage("The arena for this match is " + ChatColor.GREEN + arena.getName());
		if (gameType != null)
			Game.start();
	}
	public static boolean setGameType(int id) {
		int size = players.size();
		switch(id) {
		case 0:
			if (GTThreeLives.isRightPlayers(size))
				gameType = new GTThreeLives();
			else return false;
			break;
		case 1:
			if (GTOneLife.isRightPlayers(size))
				gameType = new GTOneLife();
			else return false;
			break;
		case 2:
			if (GTKnockback.isRightPlayers(size)) {
				gameType = new GTKnockback();
				superKnockback = true;
			} else return false;
			break;
		case 3:
			if (GTRandomKits.isRightPlayers(size)) {
				gameType = new GTRandomKits();
				superKnockback = true;
			} else return false;
			break;
		case 4:
			if (GTMirrorMatchup.isRightPlayers(size)) {
				gameType = new GTMirrorMatchup();
			} else return false;
			break;
		case 5:
			if (GTSuddenDeath.isRightPlayers(size)) {
				gameType = new GTSuddenDeath();
			} else return false;
			break;
		case 6:
			if (GTLastUnderThreeLives.isRightPlayers(size))
				gameType = new GTLastUnderThreeLives();
			else return false;
			break;
		case 7:
			if (GTTOO.isRightPlayers(size))
				gameType = new GTTOO();
			else return false;
			break;
		case 8:
			if (GTFiveKills.isRightPlayers(size))
				gameType = new GTFiveKills();
			else return false;
			break;
		case 9:
			if (GTNullProtect.isRightPlayers(size))
				gameType = new GTNullProtect();
			else return false;
			break;
		default:
			return false;
		}
		broadcastMessage("The game type for this match is " + ChatColor.GOLD + gameType.name());
		if (arena != null)
			Game.start();
		return true;
	}
	
	public static void broadcastMessage(String msg) {
		for (GamePlayer gp : players)
			gp.player().sendMessage(msg);
	}
	
}

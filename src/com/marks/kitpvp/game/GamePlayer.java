package com.marks.kitpvp.game;

import org.bukkit.GameMode;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.marks.kitpvp.kits.Kit;
import com.marks.kitpvp.kits.KitAlien;
import com.marks.kitpvp.kits.KitAstronaut;
import com.marks.kitpvp.kits.KitBigBomber;
import com.marks.kitpvp.kits.KitBulletBerserker;
import com.marks.kitpvp.kits.KitCharge;
import com.marks.kitpvp.kits.KitElectroBowman;
import com.marks.kitpvp.kits.KitGhoul;
import com.marks.kitpvp.kits.KitKing;
import com.marks.kitpvp.kits.KitNeoArcher;
import com.marks.kitpvp.kits.KitNull;
import com.marks.kitpvp.kits.KitSectorQueen;
import com.marks.kitpvp.kits.KitShotgunCycler;
import com.marks.kitpvp.kits.KitSolarPower;
import com.marks.kitpvp.kits.KitSpeedSlicer;
import com.marks.kitpvp.kits.KitTearjerker;
import com.marks.kitpvp.kits.KitUltimateDefensive;
import com.marks.kitpvp.kits.KitWildMan;

public class GamePlayer {

	private Player p;
	private Kit k;
	protected int deaths;
	protected int kills;
	public int teamID;
	private GamePlayer lastDamageCause;
	private boolean hasDoubleJumped;
	
	public GamePlayer(Player p) {
		this.hasDoubleJumped = false;
		this.p = p;
		this.k = new KitNull(this);
		this.deaths = 0;
		this.kills = 0;
		this.teamID = -1;
		lastDamageCause = null;
		if (!p.isHealthScaled()) {
			p.setHealthScaled(true);
			p.setHealthScale(20);
		}
	}
	
	public void damage(double amt, GamePlayer cause) {
		if (cause != null && !cause.equals(this))
			lastDamageCause = cause;
		p.damage(amt * k.getArmorPerc());
	}
	
	public void knockback(Vector dir) {
		p.setVelocity(dir.multiply(k.getKnockBackRes()));
	}
	
	public Player player() {
		return p;
	}
	public Kit kit() {
		return k;
	}
	public int deaths() {
		return deaths;
	}
	public int kills() {
		return kills;
	}
	public GamePlayer getLastDamageCause() {
		return lastDamageCause;
	}
	public static Vector getKnockbackVector(Vector eV, double distance) { // eV cannot be <0, 0, 0>
		distance *= Game.superKnockback ? 2.7 : 1.0;
		double y = 0.3;
		if (eV.getX() == 0)
			eV.setX(0.00000000001);
		double b = Math.atan(eV.getZ() / eV.getX());
		if (eV.getX() < 0)
			b += Math.PI;
		double a = Math.sqrt(2.45 * distance * distance / y + (39.2 * y));
		double theta = Math.asin(Math.sqrt(39.2 * y) / a);
		double vx = a * Math.cos(theta) * Math.cos(b);
		double vy = a * Math.sin(theta);
		double vz = a * Math.cos(theta) * Math.sin(b);
		return new Vector(vx, vy, vz).multiply(0.0768);
	}
	
	@SuppressWarnings("deprecation")
	public void resetPlayer() {
		hasDoubleJumped = false;
		p.getInventory().clear();
		p.setHealthScale(20);
		p.resetMaxHealth();
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setWalkSpeed(0.2f);
		p.setFlySpeed(0.1f);
		p.setGameMode(GameMode.ADVENTURE);
		p.setAllowFlight(true);
		p.setFlying(false);
		lastDamageCause = null;
		for (PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
		p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 9999999, 1, false, false));
		p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999999, 0, false, false));
		for (LivingEntity lie : p.getWorld().getLivingEntities())
			if (lie instanceof Horse) {
				if (((Horse) lie).getOwner() == p)
					lie.remove();
			}
	}
	
	public void act() {
		if (hasDoubleJumped && p.isOnGround())
			hasDoubleJumped = false;
		k.onTick();
	}
	
	public void tryToDoubleJump() {
		if (hasDoubleJumped)
			return;
		hasDoubleJumped = true;
		double a = 6.0 * p.getWalkSpeed();
		Vector v = p.getLocation().getDirection().multiply(a);
		v.setY(v.getY() / a * 0.8);
		p.setVelocity(p.getLocation().getDirection().multiply(6.0 * p.getWalkSpeed()));
	}
	
	public void setKit(String kitName) {
		switch(kitName) {
		case "Null":
			k = new KitNull(this);
			break;
		case "Solar Power Enthusiast":
			k = new KitSolarPower(this);
			break;
		case "Ghoul":
			k = new KitGhoul(this);
			break;
		case "Neo-Archer":
			k = new KitNeoArcher(this);
			break;
		case "CHAAAARGE!":
			k = new KitCharge(this);
			break;
		case "Wildman":
			k = new KitWildMan(this);
			break;
		case "Ultimate Defensive":
			k = new KitUltimateDefensive(this);
			break;
		case "Tearjerker":
			k = new KitTearjerker(this);
			break;
		case "Speed Slicer":
			k = new KitSpeedSlicer(this);
			break;
		case "Big Bomber":
			k = new KitBigBomber(this);
			break;
		case "Alien":
			k = new KitAlien(this);
			break;
		case "Bullet Berserker":
			k = new KitBulletBerserker(this);
			break;
		case "Electro Bowman":
			k = new KitElectroBowman(this);
			break;
		case "Astronaut":
			k = new KitAstronaut(this);
			break;
		case "Shotgun Cycler":
			k = new KitShotgunCycler(this);
			break;
		case "King":
			k = new KitKing(this);
			break;
		case "Sector Queen":
			k = new KitSectorQueen(this);
			break;
		}
	}
	
}

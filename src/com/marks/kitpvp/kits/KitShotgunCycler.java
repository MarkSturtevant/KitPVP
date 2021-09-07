package com.marks.kitpvp.kits;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import com.marks.kitpvp.game.GamePlayer;

public class KitShotgunCycler extends Kit {
	
	int timer;
	Random rand;
	
	private final ItemStack kngun = KitUtils.makeItem(Material.WOOD_HOE, (short) 0, 1, false, "Knockback Ballistic", "Range: 20 Blocks", "Deals MASSIVE Knockback!");
	private final ItemStack bagun = KitUtils.makeItem(Material.IRON_HOE, (short) 0, 1, false, "Pistol", "Range: 20 Blocks", "The classic one-shot gun.");
	private final ItemStack shgun = KitUtils.makeItem(Material.GOLD_HOE, (short) 0, 1, false, "Gobbssmacker", "Range: 10 Blocks", "The closer, the higher damage!");
	private final ItemStack sngun = KitUtils.makeItem(Material.DIAMOND_HOE, (short) 0, 1, false, "Sniper Rifle", "Range: 150 Blocks", "The farther away, the more damage.");
	private final ItemStack stick = KitUtils.makeItem(Material.STICK, (short) 0, 1, false, "", "STICK up for me, will you?");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Cowboy Hat", Color.fromRGB(128, 64, 64), false),
			KitUtils.makeArmor(1, "Rodeo Shorts", Color.fromRGB(128, 64, 64), false),
			KitUtils.makeArmor(2, "Quit Horsing Around", Color.fromRGB(64, 32, 32), false),
			KitUtils.makeArmor(3, "Cowboy Boots", Color.fromRGB(128, 64, 64), false)
	};

	public KitShotgunCycler(GamePlayer gp) {
		super(gp);
		rand = new Random();
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Shotgun Cycler");
	}

	@Override
	public double getArmorPerc() {
		return 0.85;
	}

	@Override
	public double getKnockBackRes() {
		return 1.1;
	}

	@Override
	public void setInventory() {
		timer = 0;
		PlayerInventory pi = gp.player().getInventory();
		pi.setItem(0, getRandomGun());
		pi.setItem(1, stick);
		pi.setItem(2, stick);
		pi.setItem(3, stick);
		pi.setItem(4, stick);
		pi.setItem(5, getRandomGun());
		pi.setItem(6, getRandomGun());
		pi.setItem(7, getRandomGun());
		pi.setItem(8, getRandomGun());
		pi.setArmorContents(armor);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
		PlayerInventory pi = gp.player().getInventory();
		if (isRightClick && timer == 0 && pi.getHeldItemSlot() == 0) {
			ItemStack gun = pi.getItem(0);
			if (gun == null) {
				gp.player().sendMessage("If you see this message, tell Mark!  RIP programming skills (or maybe you broke the kit idk)");
				return;
			}
			timer = 70;
			pi.clear(0);
			gp.player().playSound(gp.player().getLocation(), Sound.BLOCK_SLIME_BREAK, 10.0f, 1.0f);
			Vector direction = gp.player().getLocation().getDirection();
			switch(gun.getType()) {
			case WOOD_HOE:
				doShot(2.0, GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), 20.0), (new Gunshot(direction, 20.0, gp.player().getEyeLocation())).getEntity());
				break;
			case IRON_HOE:
				doShot(9.0, GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), 2.0), (new Gunshot(direction, 20.0, gp.player().getEyeLocation())).getEntity());
				break;
			case GOLD_HOE:
				Gunshot shot = new Gunshot(direction, 10.0, gp.player().getEyeLocation());
				doShot(20.0 - (shot.getDist() * 2), GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), 2.0), shot.getEntity());
				break;
			case DIAMOND_HOE:
				// regression 0,0   5,1    15,6    30,18   ax^2 + bx
				Gunshot shot2 = new Gunshot(direction, 150.0, gp.player().getEyeLocation());
				doShot(0.0140187 * Math.pow(shot2.getDist(), 2) + 0.180374 * shot2.getDist(), GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), 4.0), shot2.getEntity());
				break;
			default:
				break;
			}
		}
	}
	
	private void doShot(double damage, Vector knockback, LivingEntity eHit) {
		if (eHit == null)
			return;
		dealDamage(damage, eHit);
		dealKnockback(knockback, eHit);
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		dealDamage(damage, pHit);
		dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage), pHit);
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
	}

	@Override
	public void onTick() {
		if (timer > 0) {
			PlayerInventory pi = gp.player().getInventory();
			switch(--timer) {
			case 56:
				pi.setItem(4, pi.getItem(5));
				pi.setItem(5, pi.getItem(6));
				pi.setItem(6, pi.getItem(7));
				pi.setItem(7, pi.getItem(8));
				pi.setItem(8, getRandomGun());
				gp.player().playSound(gp.player().getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 10.0f, 1.0f);
				break;
			case 42:
				pi.setItem(3, pi.getItem(4));
				pi.setItem(4, stick);
				gp.player().playSound(gp.player().getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 10.0f, 1.0f);
				break;
			case 28:
				pi.setItem(2, pi.getItem(3));
				pi.setItem(3, stick);
				gp.player().playSound(gp.player().getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 10.0f, 1.0f);
				break;
			case 14:
				pi.setItem(1, pi.getItem(2));
				pi.setItem(2, stick);
				gp.player().playSound(gp.player().getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 10.0f, 1.0f);
				break;
			case 0:
				pi.setItem(0, pi.getItem(1));
				pi.setItem(1, stick);
				gp.player().playSound(gp.player().getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 10.0f, 1.0f);
				break;
			}
		}
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(4)) {
		case 0:
			return killed + " was outcycled by " + killer;
		case 1:
			return killed + " was taken down at gunpoint by " + killer;
		case 2:
			return killed + " mistook " + killer + "'s armor for a different kit";
		case 3:
			return killed + " failed to become a stunt double in " + killer + "'s hollywood movie";
		}
		
		return "";
	}
	
	private ItemStack getRandomGun() {
		switch(rand.nextInt(4)) {
		case 0:
			return kngun;
		case 1:
			return bagun;
		case 2:
			return shgun;
		case 3:
			return sngun;
		}
		return null;
	}
	
	public class Gunshot {
		private LivingEntity eHit;
		private double dist;
		
		public Gunshot(Vector direction, double maxRange, Location startingLoc) {
			this.eHit = null;
			
			double x = startingLoc.getX();
			double y = startingLoc.getY();
			double z = startingLoc.getZ();
			double chX = direction.getX() * 0.4;
			double chY = direction.getY() * 0.4;
			double chZ = direction.getZ() * 0.4;
			World w = gp.player().getWorld();
			double rad = gp.player().getWidth() * 8.0 / 10.0;
			for (dist = 0; dist < maxRange; dist += 0.1) {
				w.spawnParticle(Particle.SMOKE_NORMAL, new Location(w, x, y, z), 1);
				if (w.getBlockAt((int) x, (int) y, (int) z).getType().isSolid())
					return;
				for (LivingEntity e : w.getLivingEntities()) {
					if (!(e instanceof Zombie || e instanceof Horse || (e instanceof Player && ((Player) e) != gp.player())))
						continue;
					Location l = e.getLocation();
					if (y > l.getY() && y < l.getY() + e.getHeight() &&
							x > l.getX() - rad && x < l.getX() + rad &&
							z > l.getZ() - rad && z < l.getZ() + rad) {
						eHit = e;
						return;
					}
				}
				x += chX;
				y += chY;
				z += chZ;
			}
		}
			
		public LivingEntity getEntity() {
			return eHit;
		}
			
		public double getDist() {
			return dist;
		}
	}

}

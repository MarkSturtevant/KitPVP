package com.marks.kitpvp.kits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftZombie;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagDouble;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;

public class KitKing extends Kit {
	
	private List<TrackingZombie> zombs;
	private List<RotatingItem> items;
	private boolean deathTriggered;
	
	private final ItemStack sword = KitUtils.makeWeapon(Material.IRON_SWORD, (short) 0, 1, true, "Royal Swing", 5, 1.5, "Maybe let the Zombies do the work.");
	private final ItemStack grenades = KitUtils.makeItem(Material.SNOW_BALL, (short) 0, 8, true, "Survant Catapult", "On impact, spawns a survant that attacks for you!");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Royal Boots", Color.fromRGB(255, 212, 0), true),
			KitUtils.makeArmor(1, "Royal Leggings", Color.fromRGB(255, 212, 0), true),
			KitUtils.makeArmor(2, "Royal Chestplate", Color.fromRGB(255, 212, 0), true),
			KitUtils.makeArmor(3, "Crown", Color.fromRGB(255, 212, 0), true)
	};
	
	private final ItemStack[] zarmor = new ItemStack[] {
			KitUtils.makeArmor(0, "Royal Boots", Color.fromRGB(255, 212, 0), false),
			KitUtils.makeArmor(1, "Royal Leggings", Color.fromRGB(255, 212, 0), false),
			KitUtils.makeArmor(2, "Royal Chestplate", Color.fromRGB(255, 212, 0), false),
			KitUtils.makeArmor(3, "Crown", Color.fromRGB(255, 212, 0), false)
	};
	
	private final ItemStack goldblock = KitUtils.makeItem(Material.GOLD_BLOCK, (short) 0, 1, true, "", "");

	public KitKing(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "King");
		items = new ArrayList<>();
		zombs = new ArrayList<>();
		deathTriggered = false;
	}

	@Override
	public double getArmorPerc() {
		return 0.8;
	}

	@Override
	public double getKnockBackRes() {
		return 0.3;
	}

	@Override
	public void setInventory() {
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, sword);
		pi.setItem(1, grenades);
		gp.player().setWalkSpeed(0.18f);
		
		deathTriggered = false;
		for (int i = 0; i < 4; i++)
			items.add(new RotatingItem());
		updateItems(4);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
		if (isRightClick) {
			PlayerInventory pi = gp.player().getInventory();
			int selSlot = pi.getHeldItemSlot();
			if (selSlot == 1 && items.size() > 0) {
				items.get(items.size() - 1).disintegrate();
				updateItems(items.size());
			}
		}
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		if (gp.player().getInventory().getHeldItemSlot() == 0) {
			dealDamage(damage, pHit);
			dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage / 5.0 * 1.2), pHit);
		}
		else dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage / 1.0 * 1.1), pHit);
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
		TrackingZombie tz = new TrackingZombie(p.getLocation());
		p.addPassenger(tz.the);
	}

	@Override
	public void onTick() {
		for (int i = 0; i < zombs.size(); i++)
			i -= zombs.get(i).tick() ? 0 : -1;
		for (RotatingItem it : items)
			it.spinAndUpdate(Math.PI / 50.0);
		
		if (gp.player().isDead() && !deathTriggered) {
			List<TrackingZombie> copy = new ArrayList<>(zombs);
			for (TrackingZombie tz : copy)
				tz.dissolve();
			List<RotatingItem> copy2 = new ArrayList<>(items);
			for (RotatingItem it : copy2)
				it.disintegrate();
			deathTriggered = true;
		}
	}
	
	private void updateItems(int num) {
		for (int i = 0; i < num; i++) {
			items.get(i).align(i, num);
		}
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(5)) {
		case 0:
			return killed + " got too low a rank in " + killer + "'s kingdom";
		case 1:
			return killed + " was not wealthy enough for " + killer;
		case 2:
			return killed + " mistook " + killer + "'s armor for butter";
		case 3:
			return killed + " couldn't pass through " + killer + "'s servants";
		case 4:
			return killed + " was blinding by " + killer + "'s shining armor";
		}
		
		return "";
	}
	
	private class TrackingZombie {
		
		private GamePlayer target;
		private int shouldBeDeaths;
		private boolean fast, flying;
		private Zombie the;
		
		public TrackingZombie(Location loc) {
			zombs.add(this);
			fast = false;
			flying = true;
			buildZombie(loc);
		}
		
		public boolean tick() {
			if (flying) {
				if (!the.isInsideVehicle()) {
					flying = false;
					return setTarget();
				}
				return true;
			}
			if (the.isDead() || target.player().isDead() || target.deaths() > shouldBeDeaths) {
				this.dissolve();
				return false;
			}
			if (the.getTarget() != target.player())
				the.setTarget(target.player());
			if (fast) {
				if (the.getLocation().distance(target.player().getLocation()) < 4.0) {
					fast = false;
					the.removePotionEffect(PotionEffectType.SPEED);
				}
			}
			else {
				if (the.getLocation().distance(target.player().getLocation()) > 5.0) {
					fast = true;
					the.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 5));
				}
			}
			return true;
		}
		
		private boolean setTarget() {
			double toBeat = 15.0;
			Location loc = the.getLocation();
			GamePlayer possibility = null;
			for (Player p : Bukkit.getOnlinePlayers()) {
				if ((possibility = Game.getGamePlayer(p)) != null && possibility.teamID != gp.teamID && p.getLocation().distance(loc) < toBeat && p.getGameMode() == GameMode.ADVENTURE) {
					target = possibility;
					toBeat = p.getLocation().distance(loc);
				}
			}
			
			if (target == null) {
				this.dissolve();
				return false;
			}
			shouldBeDeaths = target.deaths();
			return true;
		}
		
		public void dissolve() {
			if (the != null && !the.isDead())
				the.remove();
			if (zombs.contains(this))
				zombs.remove(this);
		}
		
		@SuppressWarnings("deprecation")
		private void buildZombie(Location loc) {
			the = (Zombie) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
			the.setBaby(false);
			the.setHealth(10.0f);
			the.setMaxHealth(10.0f);
			net.minecraft.server.v1_12_R1.EntityZombie nms = ((CraftZombie) the).getHandle();
			NBTTagCompound nbt = new NBTTagCompound();
			nms.c(nbt);
			
			NBTTagList modifiers = new NBTTagList();
			NBTTagCompound damage = new NBTTagCompound();
			damage.set("Name", new NBTTagString("generic.attackDamage"));
			damage.set("Base", new NBTTagInt(8));
			NBTTagCompound follow = new NBTTagCompound();
			follow.set("Name", new NBTTagString("generic.followRange"));
			follow.set("Base", new NBTTagInt(128));
			NBTTagCompound speed = new NBTTagCompound();
			speed.set("Name", new NBTTagString("generic.movementSpeed"));
			speed.set("Base", new NBTTagDouble(0.28));
			modifiers.add(damage);
			modifiers.add(follow);
			modifiers.add(speed);
			nbt.set("Attributes", modifiers);
			
			nms.a(nbt);
			
			EntityEquipment ee = the.getEquipment();
			ee.setArmorContents(zarmor);
		}
		
	}
	
	public class RotatingItem {
		
		private double spin;
		private Item item;
		
		public RotatingItem() {
			item = (Item) gp.player().getWorld().dropItem(gp.player().getLocation(), goldblock);
			item.setGravity(false);
			item.setPickupDelay(10000);
			item.setTicksLived(1);
			spin = 0;
		}
		
		public void spinAndUpdate(double rotation) {
			item.setPickupDelay(10000);
			item.setTicksLived(1);
			if ((spin += rotation) > Math.PI * 2)
				spin -= Math.PI * 2;
			Player p = gp.player();
			Location loc = new Location(p.getWorld(), 0, 0, 0);
			loc.setX(p.getLocation().getX() + p.getWidth() / 2.0 * (1.0 + 1.75 * Math.sin(spin)));
			loc.setY(p.getLocation().getY() + 1.2 * p.getHeight());
			loc.setZ(p.getLocation().getZ() + p.getWidth() / 2.0 * (1.0 + 1.75 * Math.cos(spin)));
		}
		
		public void align(int index, int counts) {
			spin = 2 * Math.PI / counts * index;
		}
		
		public void disintegrate() {
			item.remove();
			items.remove(this);
		}
		
	}

}

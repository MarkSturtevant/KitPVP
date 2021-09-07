package com.marks.kitpvp.kits;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.ChatColor;

import com.marks.kitpvp.game.GamePlayer;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagDouble;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;

public class KitCharge extends Kit {
	
	int countdown;
	final int COUNTDOWN_MAX = 300;
	private Horse horse;
	
	private final ItemStack sword = KitUtils.makeWeapon(Material.IRON_SWORD, (short) 0, 1, true, "Lance", 6, 0.9, "Don't double dip~~!");
	private final ItemStack spawn = KitUtils.makeItem(Material.GOLD_BARDING, (short) 0, 1, true, "Spawn Horse", "You are much more effective on a horse!");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Horse Gear", Color.fromRGB(109, 40, 0), false),
			KitUtils.makeArmor(1, "Horse Gear", Color.fromRGB(109, 40, 0), false),
			KitUtils.makeArmor(2, "Horse Gear", Color.fromRGB(0, 0, 0), false),
			KitUtils.makeArmor(3, "Horse Gear", Color.fromRGB(255, 255, 255), false)
	};

	public KitCharge(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Charge!");
	}

	@Override
	public double getArmorPerc() {
		return 0.8;
	}

	@Override
	public double getKnockBackRes() {
		if (isRiding())
			return 0.0;
		return 0.85;
	}

	@Override
	public void setInventory() {
		countdown = 0;
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, sword);
		pi.setItem(1, spawn);
		gp.player().setWalkSpeed(0.18f);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
		if (isRightClick && countdown == 0) {
			PlayerInventory pi = gp.player().getInventory();
			int selSlot = pi.getHeldItemSlot();
			ItemStack selItem = pi.getItemInMainHand();
			if (selSlot == 1 && selItem != null && selItem.getAmount() >= 1) {
				pi.setItem(selSlot, null);
				spawnHorse();
				countdown = COUNTDOWN_MAX + 1;
			}
		}
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		if (gp.player().getInventory().getHeldItemSlot() == 0) {
			dealDamage(damage, pHit);
			dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), 2), pHit);
		}
		else {
			dealDamage(damage, pHit);
			dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), 1), pHit);
		}
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
	}

	@Override
	public void onTick() {
		if (!isRiding()) {
			if (countdown == COUNTDOWN_MAX + 1) {
				if (!horse.isDead())
					horse.setHealth(0.0);
				countdown--;
			}
			if (countdown > 0 && --countdown == 0) {
				gp.player().getInventory().setItem(1, spawn);
			}
				
		}
		else {
			gp.player().getWorld().spawnParticle(Particle.CRIT, gp.player().getLocation(), 3, 1.2, 0.6, 1.2);
			if (!horse.isOnGround())
				horse.setFallDistance(0.1f);
		}
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(5)) {
		case 0:
			return killed + " got spiked by " + killer + "'s lance";
		case 1:
			return killed + " was ripped into shreds by " + killer;
		case 2:
			return killed + " was at the wrong end of " + killer + "'s charge strike";
		case 3:
			return killed + " got poked by " + killer;
		case 4:
			return killed + " fell under the cheers of the crowds, granting victory to " + killer;
		}
		
		return "";
	}
	
	private boolean isRiding() {
		return gp.player().isInsideVehicle() && gp.player().getVehicle() == horse;
	}
	
	private void spawnHorse() {
		horse = (Horse) gp.player().getWorld().spawnEntity(gp.player().getLocation(), EntityType.HORSE);
		horse.setColor(Horse.Color.BROWN);
		horse.setStyle(Horse.Style.NONE);
		horse.setTamed(true);
		horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		horse.setOwner(gp.player());
		horse.setHealth(20.0);
		horse.setAdult();
		horse.setJumpStrength(0.6);
		horse.setCustomName(gp.player().getDisplayName());
		
		NBTTagCompound nbt = new NBTTagCompound();
		net.minecraft.server.v1_12_R1.EntityHorse nms = ((CraftHorse) horse).getHandle();
		nms.c(nbt);
		NBTTagList modifiers = new NBTTagList();
		NBTTagCompound speed = new NBTTagCompound();
		speed.set("Name", new NBTTagString("generic.movementSpeed"));
		speed.set("Base", new NBTTagDouble(0.25));
		modifiers.add(speed);
		nbt.set("Attributes", modifiers);
		nms.a(nbt);
		
		horse.addPassenger(gp.player());
	}

}

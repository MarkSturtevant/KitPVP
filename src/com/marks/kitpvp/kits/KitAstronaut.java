package com.marks.kitpvp.kits;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import com.marks.kitpvp.game.GamePlayer;

public class KitAstronaut extends Kit {
	
	int countdown;
	boolean flying;
	
	private final ItemStack sword = KitUtils.makeWeapon(Material.IRON_SWORD, (short) 0, 1, true, "Space Spike", 3, 1.0, "A bit hot!");
	private final ItemStack bow = KitUtils.makeItem(Material.BOW, (short) 0, 1, true, "Firework Catapult", "Dangerous!");
	private final ItemStack fly = KitUtils.makeItem(Material.BLAZE_POWDER, (short) 0, 1, true, "Blast Off", "Fly! Kinda.", "Only one use.");
	private final ItemStack arrow = KitUtils.makeItem(Material.ARROW, (short) 0, 1, false, "Firework", "No, it's not an arrow.");
	
	private final ItemStack[] armor = new ItemStack[] {
			KitUtils.makeArmor(0, "Astronaut Suit", Color.fromRGB(240, 240, 240), true),
			KitUtils.makeArmor(1, "Astronaut Suit", Color.fromRGB(240, 240, 240), true),
			KitUtils.makeArmor(2, "Astronaut Suit", Color.fromRGB(240, 240, 240), true),
			KitUtils.makeItem(Material.GLASS, (short) 0, 1, true, "Astronaut Head", "Looks super cool!")
	};

	public KitAstronaut(GamePlayer gp) {
		super(gp);
		gp.player().sendMessage("Selected Kit " + ChatColor.AQUA + "Astronaut");
	}

	@Override
	public double getArmorPerc() {
		return 0.7;
	}

	@Override
	public double getKnockBackRes() {
		return 0.7;
	}

	@Override
	public void setInventory() {
		countdown = 0;
		flying = false;
		PlayerInventory pi = gp.player().getInventory();
		pi.setArmorContents(armor);
		pi.setItem(0, sword);
		pi.setItem(1, bow);
		pi.setItem(2, fly);
		pi.setItem(17, arrow);
		gp.player().setWalkSpeed(0.18f);
	}

	@Override
	public void onClickEvent(boolean isRightClick) {
		if (isRightClick) {
			PlayerInventory pi = gp.player().getInventory();
			int selSlot = pi.getHeldItemSlot();
			ItemStack selItem = pi.getItemInMainHand();
			if (selSlot == 2 && selItem != null && selItem.getAmount() >= 1) {
				pi.setItem(selSlot, null);
				gp.player().getWorld().spawnParticle(Particle.SMOKE_LARGE, gp.player().getLocation(), 40);
				countdown = 180;
				flying = true;
				gp.player().setVelocity(new Vector(0.0, 2.0, 0.0));
			}
		}
	}

	@Override
	public void onHitEvent(LivingEntity pHit, double damage) {
		dealDamage(damage, pHit);
		dealKnockback(GamePlayer.getKnockbackVector(gp.player().getLocation().getDirection(), damage / 3.0 * 6.0), pHit);
		pHit.setFireTicks((int) (damage / 3.0 * 140.0));
	}

	@Override
	public void onProjectileHitEvent(LivingEntity pHit, Projectile p) {
		if (p.getCustomName() == null)
			return;
		double powPerc = Double.valueOf(p.getCustomName()) / 3.0;
		dealDamage(4.2 * powPerc, pHit);
		dealKnockback(GamePlayer.getKnockbackVector(p.getVelocity(), 6.1 * powPerc), pHit);
		pHit.setFireTicks((int) (140 * powPerc));
	}

	@Override
	public void onProjectileLaunchEvent(Projectile p) {
		p.setCustomName(String.valueOf(p.getVelocity().length()));
		if (p.getVelocity().length() < 1.51)
			p.remove();
	}

	@Override
	public void onTick() {
		if (flying) {
			gp.player().setFallDistance(0.0f);
			if (--countdown < 10) {
				if (gp.player().isOnGround() == true) {
					flying = false;
					countdown = 500;
				}
				gp.player().setFallDistance(0.0f);
			}
			else if (countdown == 10)
				gp.player().setVelocity(gp.player().getLocation().getDirection().multiply(1.5).setY(-2.0));
			else if (countdown >= 170) {
				if (gp.player().getVelocity().getY() <= 0) {
					countdown = 170;
					gp.player().setVelocity(new Vector().zero());
				}
			}
			else if (countdown > 10 && countdown < 170) {
				gp.player().setVelocity(new Vector().zero());
				if (countdown == 50)
					gp.player().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Prepare for landing!");
			}
		}
		else if (countdown > 0 && -countdown == 0)
			gp.player().getInventory().setItem(2, fly);
	}

	@Override
	public String createDeathMessage(String nameKilled) {
		String killed = ChatColor.RED + nameKilled + ChatColor.WHITE;
		String killer = ChatColor.AQUA + gp.player().getDisplayName() + ChatColor.WHITE;
		switch(new Random().nextInt(5)) {
		case 0:
			return killed + " blasted off as a result of " + killer;
		case 1:
			return killed + " took " + killer + "'s rocket to the eye";
		case 2:
			return killed + " slowly burned to death from " + killer;
		case 3:
			return killed + " bit " + killer + "'s bullet";
		case 4:
			return killed + " couldn't keep up with " + killer + "'s rocket";
		}
		
		return "";
	}

}

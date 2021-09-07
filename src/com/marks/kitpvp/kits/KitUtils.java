package com.marks.kitpvp.kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagDouble;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;

class KitUtils {

	protected static ItemStack makeArmor(int armorType, String customName, Color color, boolean enchanted) {
		if (armorType < 0 || armorType > 3)
			return null;
		ItemStack armor = null;
		switch(armorType) {
		case 0:
			armor = new ItemStack(Material.LEATHER_BOOTS);
			break;
		case 1:
			armor = new ItemStack(Material.LEATHER_LEGGINGS);
			break;
		case 2:
			armor = new ItemStack(Material.LEATHER_CHESTPLATE);
			break;
		case 3:
			armor = new ItemStack(Material.LEATHER_HELMET);
			break;
		}
		LeatherArmorMeta lim = (LeatherArmorMeta) armor.getItemMeta();
		lim.setDisplayName(customName);
		lim.setColor(color);
		lim.setUnbreakable(true);
		lim.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		if (enchanted) {
			lim.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
			lim.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		armor.setItemMeta(lim);
		
		net.minecraft.server.v1_12_R1.ItemStack crarmor = CraftItemStack.asNMSCopy(armor);
		NBTTagCompound nbt = crarmor.getTag();
		NBTTagList modifiers = new NBTTagList();
		NBTTagCompound nbtarmor = new NBTTagCompound();
		nbtarmor.set("AttributeName", new NBTTagString("generic.armor"));
		nbtarmor.set("Name", new NBTTagString("generic.armor"));
		nbtarmor.set("Amount", new NBTTagInt(-1));
		nbtarmor.set("Operation", new NBTTagInt(1));
		modifiers.add(nbtarmor);
		nbt.set("AttributeModifiers", modifiers);
		return CraftItemStack.asBukkitCopy(crarmor);
	}
	
	protected static ItemStack makeItem(Material type, short meta, int count, boolean enchanted, String customName, String... lore) {
		ItemStack item = new ItemStack(type, count, meta);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(customName);
		List<String> usableLore = new ArrayList<>();
		for (String s : lore)
			usableLore.add(s);
		im.setLore(usableLore);
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if (enchanted) {
			im.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		item.setItemMeta(im);
		
		return item;
	}
	
	protected static ItemStack makeWeapon(Material type, short meta, int count, boolean enchanted, String customName, int attackDamage, double rechargeTime, String... lore) {
		net.minecraft.server.v1_12_R1.ItemStack critem = CraftItemStack.asNMSCopy(makeItem(type, meta, count, enchanted, customName, lore));
		NBTTagCompound nbt = critem.hasTag() ? critem.getTag() : new NBTTagCompound();
		NBTTagList modifiers = new NBTTagList();
		NBTTagCompound damage = new NBTTagCompound();
		damage.set("AttributeName", new NBTTagString("generic.attackDamage"));
		damage.set("Name", new NBTTagString("generic.attackDamage"));
		damage.set("Amount", new NBTTagInt(attackDamage - 1));
		damage.set("Operation", new NBTTagInt(0));
		damage.set("UUIDLeast", new NBTTagInt(894654));
		damage.set("UUIDMost", new NBTTagInt(2872));
		NBTTagCompound speed = new NBTTagCompound();
		speed.set("AttributeName", new NBTTagString("generic.attackSpeed"));
		speed.set("Name", new NBTTagString("generic.attackSpeed"));
		// rechargeTime is in seconds.
		speed.set("Amount", new NBTTagDouble(1.0 / rechargeTime - 4));
		speed.set("Operation", new NBTTagInt(0));
		speed.set("UUIDLeast", new NBTTagInt(113961));
		speed.set("UUIDMost", new NBTTagInt(75536));
		modifiers.add(damage);
		modifiers.add(speed);
		nbt.set("AttributeModifiers", modifiers);
		critem.setTag(nbt);
		return CraftItemStack.asBukkitCopy(critem);
	}
	
	protected static ItemStack makePotion(Material m, int count, PotionEffect effect, Color color, String displayName, String... lore) {
		ItemStack potion = new ItemStack(m, count);
		PotionMeta pm = (PotionMeta) potion.getItemMeta();
		pm.addCustomEffect(effect, true);
		pm.setColor(color);
		pm.setDisplayName(displayName);
		List<String> usableLore = new ArrayList<>();
		for (String s : lore)
			usableLore.add(s);
		pm.setLore(usableLore);
		
		potion.setItemMeta(pm);
		
		return potion;
	}
}

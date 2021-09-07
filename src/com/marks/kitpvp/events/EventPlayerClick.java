package com.marks.kitpvp.events;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.marks.kitpvp.game.Game;
import com.marks.kitpvp.game.GamePlayer;

public class EventPlayerClick implements Listener {

	@EventHandler
	public void playerClick(PlayerInteractEvent e) {
		if (!Game.getInGame() || e.getHand() == null || e.getHand().equals(EquipmentSlot.OFF_HAND))
			return;
		GamePlayer gp = Game.getGamePlayer(e.getPlayer());
		if (gp == null)
			return;
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType() == Material.SPONGE)
			Game.broadcastMessage(e.getPlayer().getDisplayName() + " has clicked a sponge!");
		gp.kit().onClickEvent(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK));
	}
	
	@EventHandler
	public void playerEntityClick(PlayerInteractAtEntityEvent e) {
		if (e.getHand() == null || e.getHand().equals(EquipmentSlot.OFF_HAND))
			return;
		if (Game.getInQueue()) {
			GamePlayer gp = Game.getGamePlayer(e.getPlayer());
			if (gp != null && e.getRightClicked() instanceof ArmorStand && e.getRightClicked().getCustomName() != null)
				Game.onKitSelect(gp, e.getRightClicked().getCustomName());
		}
		else if (Game.getInGame()) {
			GamePlayer gp = Game.getGamePlayer(e.getPlayer());
			if (gp == null)
				return;
			gp.kit().onClickEvent(true);
		}
	}
	
}

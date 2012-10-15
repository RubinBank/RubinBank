package me.criztovyl.rubinbank.listeners;


import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.tools.MySQL;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;


public class listeners implements Listener{
	@EventHandler
	public static void onPlayerClick(PlayerInteractEvent evt){
		if(evt.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(evt.hasItem()){
				if(evt.getItem().getTypeId() == 289)
					if(evt.getPlayer().hasPermission("RubinBank.createExplosion"))
						evt.getClickedBlock().getLocation().getWorld().createExplosion(evt.getClickedBlock().getLocation(), 10);
			}
		}
	}
	@EventHandler
	public static void onSignChange(SignChangeEvent evt){
		Player player = evt.getPlayer();
		String[] lines = evt.getLines();
		//Test Sign
		String test = "Test";
		if(lines[0].equals(test)){
			if(player.hasPermission("RubinBank.dummy")){
				evt.setLine(1, ChatColor.STRIKETHROUGH+"STRIKETHROUGH");
				player.sendMessage("Its a Test Sign...");
			}
		}
	}
	@EventHandler
	public static void onPlayerLogin(PlayerLoginEvent evt){
		RubinBank.log.info("RubinBank PlayerLoginEvent: "+evt.getPlayer().getName());
		if(!RubinBank.isinDB(evt.getPlayer())){
			RubinBank.log.info("Not in DB");
			MySQL.addPlayer(evt.getPlayer());
		}
		else{
			RubinBank.log.info("Updated Last Login");
			MySQL.updateLastLogin(evt.getPlayer());
		}
	}
}

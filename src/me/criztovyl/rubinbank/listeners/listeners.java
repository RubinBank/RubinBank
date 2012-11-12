package me.criztovyl.rubinbank.listeners;


import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.account.account;
import me.criztovyl.rubinbank.tools.MySQL;
import me.criztovyl.rubinbank.tools.TimeShiftBankomat;
import me.criztovyl.rubinbank.tools.TriggerButton;
import me.criztovyl.rubinbank.tools.TriggerButtonType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginEnableEvent;


public class listeners implements Listener{
	@EventHandler
	public static void onPlayerClick(PlayerInteractEvent evt){
		if(evt.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(evt.getClickedBlock().getType().equals(Material.SIGN_POST) || evt.getClickedBlock().getType().equals(Material.SIGN)
					|| evt.getClickedBlock().getType().equals(Material.WALL_SIGN)){
				BlockState state = evt.getClickedBlock().getState();
				Sign sign = (Sign) state;
				if(sign.getLine(0).equals(ChatColor.DARK_AQUA + "[RubinBank]")){
					evt.getPlayer().sendMessage(ChatColor.DARK_BLUE + "Nope.");					
				}	
			}
			if(evt.getClickedBlock().getType().equals(Material.STONE_BUTTON) || evt.getClickedBlock().equals(Material.WOOD_BUTTON)){
				if(TriggerButton.isTriggerButton(evt.getClickedBlock().getLocation())){
					if(TriggerButton.getType(evt.getClickedBlock().getLocation()).equals(TriggerButtonType.AMOUNT)){
						if(account.hasAccount(evt.getPlayer())){
							evt.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Dein Kontostand beträgt: " + account.getAccountAmount(evt.getPlayer()));
						}
						else{
							evt.getPlayer().sendMessage(ChatColor.YELLOW + "Du hast kein Konto");
						}
					}
					if(TriggerButton.getType(evt.getClickedBlock().getLocation()).equals(TriggerButtonType.CREATE)){
						account.createAccount(evt.getPlayer());
					}
				}
			}
		}
	}
	@EventHandler
	public static void onSignChange(SignChangeEvent evt){
		Player player = evt.getPlayer();
		String[] lines = evt.getLines();
		if(lines[0].equals("[RubinBank]") || lines[0].equals("[RB]")){			
			if(lines[1].equals("Bankomat")){
				if(lines[3].toLowerCase().equals("up") || lines[3].toLowerCase().equals("down") || lines[3].toLowerCase().equals("2x2d") || lines[3].toLowerCase().equals("2x2u")){
					if(lines[2].equals("Auszahlen")){
						MySQL.insertBankomat(evt.getBlock().getLocation(), "Auszahlen", lines[3]);
					}
					if(lines[2].equals("Einzahlen")){
						MySQL.insertBankomat(evt.getBlock().getLocation(), "Einzahlen", lines[3]);
					}
					if(lines[2].equals("Kontostand")){
						MySQL.insertBankomat(evt.getBlock().getLocation(), "Amount", lines[3]);
					}
					if(lines[2].equals("Erstellen")){
						MySQL.insertBankomat(evt.getBlock().getLocation(), "Create", lines[3]);
					}
					evt.setLine(0, ChatColor.DARK_AQUA + "[RubinBank]");
					player.sendMessage(ChatColor.DARK_AQUA + "Added Bankomat");
				}
				else{
					player.sendMessage("Zeile vier ist ungültig.");
				}
			}
			evt.setLine(0, ChatColor.DARK_AQUA + "[RubinBank]");
		}
	}
	@EventHandler
	public static void onSignBreak(BlockBreakEvent evt){
		if(evt.getBlock().getType().equals(Material.SIGN_POST) || evt.getBlock().getType().equals(Material.SIGN)
				|| evt.getBlock().getType().equals(Material.WALL_SIGN)){
			Sign sign = (Sign) evt.getBlock().getState();
			if(sign.getLine(0).equals(ChatColor.DARK_AQUA + "[RubinBank]") || sign.getLine(0).equals("[RB]")){
				if(sign.getLine(1).equals("Bankomat")){
					MySQL.removeBankomat(evt.getBlock().getLocation());
				}
			}
		}
		if(evt.getBlock().getType().equals(Material.STONE_BUTTON) || evt.getBlock().equals(Material.WOOD_BUTTON)){
			if(TriggerButton.isTriggerButton(evt.getBlock().getLocation())){
				MySQL.removeTriggerButton(evt.getBlock().getLocation());
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
	@EventHandler
	public static void onPluginLoad(PluginEnableEvent evt){
		if(evt.getPlugin().getName().equals("WorldGuard")){
			RubinBank.setUseWorldGuard();
			Bukkit.getPluginManager().getPlugin("RubinBank").getLogger().info("WorldGuard found :)");
		}
	}
	@EventHandler
	public static void onPlayerMove(PlayerMoveEvent evt){
		RubinBank.bankomatPlayerMove(evt);
	}
	@EventHandler
	public static void ChatEvent(AsyncPlayerChatEvent evt){
		TimeShiftBankomat.ChatEvent(evt);
	}
}

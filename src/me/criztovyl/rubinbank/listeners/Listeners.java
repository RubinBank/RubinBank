package me.criztovyl.rubinbank.listeners;


import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.account.Account;
import me.criztovyl.rubinbank.tools.BankomatType;
import me.criztovyl.rubinbank.tools.MySQL;
import me.criztovyl.rubinbank.tools.TimeShift;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginEnableEvent;


public class Listeners implements Listener{
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
						Account.amountMsg(evt.getPlayer().getName());
					}
					if(TriggerButton.getType(evt.getClickedBlock().getLocation()).equals(TriggerButtonType.CREATE)){
						Account.createAccount(evt.getPlayer().getName());
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
			if(lines[1].toLowerCase().equals("bankomat")){
				if(lines[2].toLowerCase().equals("up") || lines[2].toLowerCase().equals("down") || lines[2].toLowerCase().equals("2x2d")
						|| lines[2].toLowerCase().equals("2x2u")){
					if(!lines[3].equals("")){
						if(lines[3].toLowerCase().equals("einzahlen") || lines[3].toLowerCase().equals("auszahlen") || lines[3].toLowerCase().equals("kontostand")
								|| lines[3].toLowerCase().equals("überweisen") || lines[3].toLowerCase().equals("erstellen")){
							MySQL.insertnoMultiBankomat(evt.getBlock().getLocation(), lines[2], BankomatType.getType(lines[3].toLowerCase()), "");
						}
					}
					else{
						MySQL.insertBankomat(evt.getBlock().getLocation(), lines[2].toLowerCase(), "");
					}
					
					evt.setLine(0, ChatColor.DARK_AQUA + "[RubinBank]");
					player.sendMessage(ChatColor.DARK_AQUA + "Added Bankomat");
				}
				else{
					player.sendMessage("Zeile drei ist ungültig.");
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
	public static void onPluginLoad(PluginEnableEvent evt){
		if(evt.getPlugin().getName().equals("WorldGuard")){
			RubinBank.setUseWorldGuard();
			Bukkit.getPluginManager().getPlugin("RubinBank").getLogger().info("WorldGuard found :)");
		}
	}
	@EventHandler
	public static void onPlayerMove(PlayerMoveEvent evt){
		//evt.getPlayer().sendMessage("move...");
		RubinBank.bankomatPlayerMove(evt);
	}
	@EventHandler
	public static void ChatEvent(AsyncPlayerChatEvent evt){
		TimeShift.ChatEvent(evt);
	}
}

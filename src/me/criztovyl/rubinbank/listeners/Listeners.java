package me.criztovyl.rubinbank.listeners;


import java.util.HashMap;

import me.criztovyl.clicklesssigns.ClicklessSigns;
import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.account.Account;
import me.criztovyl.rubinbank.tools.SignArg;
import me.criztovyl.rubinbank.tools.MySQL;
import me.criztovyl.rubinbank.tools.SignType;
import me.criztovyl.rubinbank.tools.TimeShift;
import me.criztovyl.rubinbank.tools.TriggerButton;
import me.criztovyl.rubinbank.tools.TriggerButtonType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
		String p_n = evt.getPlayer().getName();
		String[] lines = evt.getLines();
		String line2 = "";
		if(lines[0].equals("[RubinBank]") || lines[0].equals("[RB]")){
			if(lines[1].toLowerCase().equals("bankomat")){
				if(lines[2].toLowerCase().equals("up") || lines[2].toLowerCase().equals("down") || lines[2].toLowerCase().equals("2x2d")
						|| lines[2].toLowerCase().equals("2x2u")){
					if(!lines[3].equals("")){
						if(lines[3].toLowerCase().equals("einzahlen") || lines[3].toLowerCase().equals("auszahlen") || lines[3].toLowerCase().equals("kontostand")
								|| lines[3].toLowerCase().equals("überweisen") || lines[3].toLowerCase().equals("erstellen")){
							HashMap<SignArg, String> args = new HashMap<SignArg, String>();
							args.put(SignArg.LOCX, Double.toString(evt.getBlock().getLocation().getBlockX()));
							args.put(SignArg.LOCY, Double.toString(evt.getBlock().getLocation().getBlockY()));
							args.put(SignArg.LOCZ, Double.toString(evt.getBlock().getLocation().getBlockZ()));
							args.put(SignArg.LOCWORLD, evt.getBlock().getLocation().getWorld().getName());
							args.put(SignArg.POS, lines[2].toLowerCase());
							args.put(SignArg.TYPE, SignType.getType(lines[3].toLowerCase()).toString());
							args.put(SignArg.MULTI, "0");
							TimeShift.storeArgs(p_n, args);
							TimeShift.addShifted(p_n, SignType.BANKOMAT_LOC);
							line2 = SignType.getType(lines[3].toLowerCase()).toString();
						}
					}
					else{
						HashMap<SignArg, String> args = new HashMap<SignArg, String>();
						args.put(SignArg.LOCX, Double.toString(evt.getBlock().getLocation().getBlockX()));
						args.put(SignArg.LOCY, Double.toString(evt.getBlock().getLocation().getBlockY()));
						args.put(SignArg.LOCZ, Double.toString(evt.getBlock().getLocation().getBlockZ()));
						args.put(SignArg.LOCWORLD, evt.getBlock().getLocation().getWorld().getName());
						args.put(SignArg.POS, lines[2].toLowerCase());
						args.put(SignArg.MULTI, "1");
						TimeShift.storeArgs(p_n, args);
						TimeShift.addShifted(p_n, SignType.BANKOMAT_LOC);
						line2 = "";
					}
					
					evt.setLine(0, ChatColor.DARK_AQUA + "[RubinBank]");
				}
				else{
					player.sendMessage("Zeile drei ist ungültig.");
				}
			}
			evt.setLine(0, ChatColor.DARK_AQUA + "[RubinBank]");
			evt.setLine(1, ChatColor.DARK_AQUA + "Bankomat");
			evt.setLine(2, line2);
			evt.setLine(3, "");
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
	public static void ChatEvent(AsyncPlayerChatEvent evt){
		TimeShift.ChatEvent(evt);
	}
	@EventHandler
	public static void PlayerMoveEvent(PlayerMoveEvent evt){
		Location locTo = new Location(evt.getTo().getWorld(), evt.getTo().getBlockX(), evt.getTo().getBlockY(), evt.getTo().getBlockZ());
		Location locFrom = new Location(evt.getFrom().getWorld(), evt.getFrom().getBlockX(), evt.getFrom().getBlockY(), evt.getFrom().getBlockZ());
		String p_n = evt.getPlayer().getName();
		if(!(locTo.equals(locFrom))){
			if(TimeShift.isShifted(p_n)){
				if(ClicklessSigns.isClicklessSignTrigger(locFrom)){
					TimeShift.removeShifted(p_n);
				}
			}
		}
	}
}

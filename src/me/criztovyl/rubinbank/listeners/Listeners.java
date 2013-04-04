package me.criztovyl.rubinbank.listeners;


import me.criztovyl.clickless.ClicklessPlugin;
import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.tools.MySQL;
import me.criztovyl.rubinbank.tools.SignType;
import me.criztovyl.rubinbank.tools.Tools;
import me.criztovyl.rubinbank.tools.TriggerButton;
import me.criztovyl.rubinbank.tools.TriggerButtonType;
import me.criztovyl.timeshift.MicroShift;

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
import org.bukkit.event.server.PluginEnableEvent;


public class Listeners implements Listener{
	@EventHandler
	public static void onPlayerClick(PlayerInteractEvent evt){
		String p_n = evt.getPlayer().getName();
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
						if(RubinBank.getHelper().getBank().hasAccount(p_n)){
							RubinBank.getHelper().getBank().getAccount(p_n).sendBalanceMessage();
						}
					}
					if(TriggerButton.getType(evt.getClickedBlock().getLocation()).equals(TriggerButtonType.CREATE)){
						if(!RubinBank.getHelper().getBank().hasAccount(p_n)){
							RubinBank.getHelper().getBank().createAccount(p_n);
						}
					}
				}
			}
		}
	}
	@EventHandler
	public static void onSignChange(final SignChangeEvent evt){
		Player player = evt.getPlayer();
		final String p_n = evt.getPlayer().getName();
		String[] lines = evt.getLines();
		String line2 = "";
		if(lines[0].equals("[RubinBank]") || lines[0].equals("[RB]")){
			if(lines[1].toLowerCase().equals("bankomat")){
				if(lines[2].toLowerCase().equals("up") || lines[2].toLowerCase().equals("down") || lines[2].toLowerCase().equals("2x2d")
						|| lines[2].toLowerCase().equals("2x2u")){
					if(!lines[3].equals("")){
						if(lines[3].toLowerCase().equals("einzahlen") || lines[3].toLowerCase().equals("auszahlen") || lines[3].toLowerCase().equals("kontostand")
								|| lines[3].toLowerCase().equals("überweisen") || lines[3].toLowerCase().equals("erstellen")){
							ClicklessPlugin.getShiftHelper().addShifted(new MicroShift() {
								boolean success = false;
								@Override
								public boolean getSuccess() {
									return success;
								}
								
								@Override
								public String getQuestion() {
									return "Wo steht dieser Bankomat?";
								}
								
								@Override
								public String getPlayer() {
									return p_n;
								}
								
								@Override
								public void executeAction(AsyncPlayerChatEvent arg0) {
									MySQL.insertnoMultiBankomat(
											evt.getBlock().getLocation(),
											evt.getLine(3),
											SignType.getType(evt.getLine(4).toUpperCase()),
											arg0.getMessage());
									RubinBank.getHelper().info("Created Sign.");
									Tools.msg(p_n, ChatColor.GREEN + "Created Bankomat.");
									success = true;
								}
							});
							line2 = Tools.getTypeLine(SignType.getType(lines[3].toLowerCase()));
						}
					}
					else{
						ClicklessPlugin.getShiftHelper().addShifted(new MicroShift() {
							boolean success = false;
							@Override
							public boolean getSuccess() {
								return success;
							}
							
							@Override
							public String getQuestion() {
								return "Wo steht dieser Bankomat?";
							}
							
							@Override
							public String getPlayer() {
								return p_n;
							}
							
							@Override
							public void executeAction(AsyncPlayerChatEvent arg0) {
								MySQL.insertBankomat(evt.getBlock().getLocation(), evt.getLine(3).toLowerCase(), arg0.getMessage());
								success = true;
							}
						});
						line2 = "";
					}
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
				if(sign.getLine(1).equals(ChatColor.DARK_AQUA + "Bankomat")){
					if(MySQL.removeBankomat(evt.getBlock().getLocation())){
						evt.getPlayer().sendMessage("Schild entfernt.");
					}
					else{
						evt.getPlayer().sendMessage(ChatColor.RED + "Es ist ein Fehler aufgetreten!");
					}
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
			Bukkit.getPluginManager().getPlugin("RubinBank").getLogger().info("WorldGuard found and Support enabled :)");
		}
	}
}

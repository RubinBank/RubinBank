package me.criztovyl.clicklesssigns;

import java.util.ArrayList;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.account.Account;
import me.criztovyl.rubinbank.tools.MySQL;
import me.criztovyl.clicklesssigns.TimeShift;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerMoveEvent;

public class ClicklessSigns {
	private static ArrayList<Location> triggers;
	public static void clicklessSignsPlayerMove(PlayerMoveEvent evt){
		Location locTo = new Location(evt.getTo().getWorld(), evt.getTo().getBlockX(), evt.getTo().getBlockY(), evt.getTo().getBlockZ());
		Location locFrom = new Location(evt.getFrom().getWorld(), evt.getFrom().getBlockX(), evt.getFrom().getBlockY(), evt.getFrom().getBlockZ());
		if(!(locTo.equals(locFrom))){
			if(TimeShift.isShifted(evt.getPlayer().getName())){
				if(ClicklessSignTriggers.isTrigger(locFrom)){
					TimeShift.removeShifted(evt.getPlayer().getName());
				}
			}
			if(!ClicklessSignTriggers.sameBankomat(locFrom, locTo)){
				if(!triggers.isEmpty()){
					if(isInClicklessSignsTriggers(locTo)){
						if(ClicklessSignTriggers.isNonMulti(locTo)){
							ClicklessSignType t = ClicklessSignTriggers.getNonMultiType(locTo);
							if(t.equals(ClicklessSignType.CREATE)){
								if(!Account.hasAccount(evt.getPlayer().getName())){
									Account.createAccount(evt.getPlayer().getName());
									evt.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Konto erstellt.");
								}
								else{
									evt.getPlayer().sendMessage(ChatColor.RED + "Du hast schon ein Konto.");
								}
							}
							else{
								if(Account.hasAccount(evt.getPlayer().getName())){
									TimeShift.addShiftedBankomat(evt.getPlayer().getName(), t);
									evt.getPlayer().sendMessage(TimeShift.typeMsg(t));
								}
								else{
									evt.getPlayer().sendMessage(ChatColor.RED + "Du hast noch kein Konto!");
								}
							}
						}
						else{
							if(Account.hasAccount(evt.getPlayer().getName())){
								evt.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Möchtest du " + ChatColor.UNDERLINE + "A" + ChatColor.RESET + ChatColor.DARK_AQUA + "bheben, " +
										 ChatColor.UNDERLINE + "E" + ChatColor.RESET + ChatColor.DARK_AQUA + "inzahlen, " +
										 ChatColor.UNDERLINE + 		"Ü" + ChatColor.RESET + ChatColor.DARK_AQUA + "berweisen oder " +
										 "deinen " + ChatColor.UNDERLINE + 	"K" + ChatColor.RESET + ChatColor.DARK_AQUA + "ontostand abrufen?");
								TimeShift.addShiftedBankomat(evt.getPlayer().getName(), ClicklessSignType.CHOOSING);
							}
							else{
								evt.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Du hast noch kein Konto, möchtest du eins erstellen? (Ja/Nein)");
								TimeShift.addShiftedBankomat(evt.getPlayer().getName(), ClicklessSignType.CREATE);
							}
						}
					}
				}
			}
		}
	}
	public static void updateClicklessSignsTriggers(){
		MySQL.updateTriggers();
		triggers = new ArrayList<Location>();
		triggers = ClicklessSignTriggers.getTriggers();
		RubinBank.log.info("Updatet ClicklessSigns Triggers ArrayList");
	}
	public static ArrayList<Location> getClicklessSignsTriggers(){
		return triggers;
	}
	public static boolean isInClicklessSignsTriggers(Location loc){
		for(int i = 0; i < triggers.size(); i++){
			Location loc2 = triggers.get(i);
			int bmatX = loc2.getBlockX();
			int bmatY = loc2.getBlockY();
			int bmatZ = loc2.getBlockZ();
			World bmatW = loc2.getWorld(); 
			loc2 = loc;
			int locX = loc2.getBlockX();
			int locY = loc2.getBlockY();
			int locZ = loc2.getBlockZ();
			World locW = loc2.getWorld();
			if(bmatX == locX && bmatY == locY && bmatZ == locZ && bmatW == locW)
				return true;
		}
		return false;
	}
}

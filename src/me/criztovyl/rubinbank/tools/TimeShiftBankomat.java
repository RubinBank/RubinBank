package me.criztovyl.rubinbank.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.criztovyl.rubinbank.account.account;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TimeShiftBankomat {
	private static Map<Player, BankomatType> type = new HashMap<Player, BankomatType>();
	private static ArrayList<Player> shiftedBankomats = new ArrayList<Player>();
	public static void addShiftedBankomat(Player p, BankomatType t){
		shiftedBankomats.add(p);
		type.put(p, t);
	}
	public static void ChatEvent(AsyncPlayerChatEvent evt){
		if(shiftedBankomats.contains(evt.getPlayer())){
			double amount;
			try{
				amount = Double.parseDouble(evt.getMessage());
			} catch(NumberFormatException e){
				evt.getPlayer().sendMessage("Du musst eine Zahl eingeben. Keine Komma, einen Punkt: 10.1 statt 10,1");
				return;
			}
			evt.setCancelled(true);
			continueBankomat(evt.getPlayer(), amount);
			
		}
	}
	public static void continueBankomat(Player p, double amount){
		BankomatType t = type.get(p);
		if(t.equals(BankomatType.OUT)){
			account.payoutFromAccount(p, amount);
		}
		if(t.equals(BankomatType.IN)){
			account.payinToAccount(p, amount);
		}
		type.remove(p);
		shiftedBankomats.remove(p);
	}
	public static boolean isShifted(Player p){
		return shiftedBankomats.contains(p);
	}
	public static void removeShifted(Player p){
		if(isShifted(p)){
			shiftedBankomats.remove(p);
			type.remove(p);
			p.sendMessage(ChatColor.YELLOW + "Chat reaktiviert.");
		}
	}
}

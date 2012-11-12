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
		Player p = evt.getPlayer();
		if(type.containsKey(p)){
			if(shiftedBankomats.contains(p)){
				if(type.get(p).equals(BankomatType.IN) || type.get(p).equals(BankomatType.OUT)){
					double amount;
					try{
						amount = Double.parseDouble(evt.getMessage());
					} catch(NumberFormatException e){
						p.sendMessage("Du musst eine Zahl eingeben. Keine Komma, einen Punkt: 10.1 statt 10,1");
						return;
					}
					continueBankomat(p, amount);
					evt.setCancelled(true);
				}
				if(type.get(p).equals(BankomatType.CHOOSING)){
					String msg = evt.getMessage();
					if(msg.toLowerCase().equals("abheben")){
						removeShiftedNoMsg(p);
						
						addShiftedBankomat(p, BankomatType.OUT);
						evt.setCancelled(true);
					}
					if(msg.toLowerCase().equals("einzahlen")){
						removeShiftedNoMsg(p);
						addShiftedBankomat(p, BankomatType.IN);
						p.sendMessage("Einzahlen");
						evt.setCancelled(true);
					}
					if(msg.toLowerCase().equals("kontostand")){
						removeShiftedNoMsg(p);
						double amount = account.getAccountAmount(p);
						p.sendMessage(ChatColor.DARK_AQUA + "Kontostand: " + amount);
						evt.setCancelled(true);
					}
					if(msg.toLowerCase().equals("konto erstellen") || msg.toLowerCase().equals("erstellen")){
						removeShiftedNoMsg(p);
						account.createAccount(p);
						evt.setCancelled(true);
					}
				}
			}
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
		removeShifted(p);
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
	public static void removeShiftedNoMsg(Player p){
		if(isShifted(p)){
			shiftedBankomats.remove(p);
			type.remove(p);
		}
	}
}

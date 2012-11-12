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
		String msg = evt.getMessage();
		boolean okay = false;
		if(type.containsKey(p)){
			if(shiftedBankomats.contains(p)){
				if(type.get(p).equals(BankomatType.IN) || type.get(p).equals(BankomatType.OUT)){
					double amount;
					try{
						amount = Double.parseDouble(msg);
					} catch(NumberFormatException e){
						p.sendMessage("Du musst eine Zahl eingeben. Keine Komma, einen Punkt: 10.1 statt 10,1");
						return;
					}
					continueBankomat(p, amount);
					evt.setCancelled(true);
					okay = true;
				}
				if(type.containsKey(p)){
					if(type.get(p).equals(BankomatType.CHOOSING)){
						if(msg.toLowerCase().equals("abheben") || msg.toLowerCase().equals("auszahlen")){
							removeShiftedNoMsg(p);
							evt.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Bitte gib den Betrag den du auszahlen möchtest in den Chat ein.(aber 10.7 statt 10,7)\n" +
									"Dein Chat ist deaktiviert bis du einen Betrag eingegeben hast.");
							addShiftedBankomat(p, BankomatType.OUT);
							evt.setCancelled(true);
							okay = true;
						}
						if(msg.toLowerCase().equals("einzahlen")){
							removeShiftedNoMsg(p);
							evt.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Bitte gib den Betrag den du einzahlen möchtest in den Chat ein.(aber 10.7 statt 10,7)\n" +
									"Dein Chat ist deaktiviert bis du einen Betrag eingegeben hast.");
							evt.setCancelled(true);
							okay = true;
							addShiftedBankomat(p, BankomatType.IN);
						}
						if(msg.toLowerCase().equals("kontostand")){
							removeShiftedNoMsg(p);
							double amount = account.getAccountAmount(p);
							p.sendMessage(ChatColor.DARK_AQUA + "Kontostand: " + amount);
							evt.setCancelled(true);
							okay = true;
						}
						if(msg.toLowerCase().equals("konto erstellen") || msg.toLowerCase().equals("erstellen")){
							removeShiftedNoMsg(p);
							account.createAccount(p);
							evt.setCancelled(true);
							okay = true;
						}
					}
				}
				if(msg.toLowerCase().equals("ende")){
					removeShifted(p);
					evt.setCancelled(true);
					okay = true;
				}
				if(!okay){
					p.sendMessage(ChatColor.YELLOW + "Unbekannte Aktion, bitte wiederholen. Ende mit \"ende\"");
				evt.setCancelled(true);
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
	public static void reset(){
		shiftedBankomats = new ArrayList<Player>();
		type = new HashMap<Player, BankomatType>();
	}
}

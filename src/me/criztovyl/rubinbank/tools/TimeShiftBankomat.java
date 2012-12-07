package me.criztovyl.rubinbank.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.criztovyl.rubinbank.account.Account;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TimeShiftBankomat {
	//Should use only player name instead of the complete player class ;)
	private static Map<String, BankomatType> type = new HashMap<String, BankomatType>();
	private static Map<String, Double> amounts = new HashMap<String, Double>();
	private static ArrayList<String> shiftedBankomats = new ArrayList<String>();
	private static ArrayList<String> loopPlayers = new ArrayList<String>();
	private static boolean noendalert;
	public static void addShiftedBankomat(String p_n, BankomatType t){
		shiftedBankomats.add(p_n);
		type.put(p_n, t);
		if(t.equals(BankomatType.TRANSFER_PLAYERII))
			msg(p_n, ChatColor.DARK_AQUA + "An wen möchtest du überweisen?");
		if(t.equals(BankomatType.UCP_PASS))
			msg(p_n, ChatColor.DARK_AQUA + "Bitte gebe jetzt dein Passwort für das Webinterface ein:");
	}
	public static void ChatEvent(AsyncPlayerChatEvent evt){
		Player p = evt.getPlayer();
		String p_n = p.getName();
		String msg = evt.getMessage();
		boolean okay = false;
		if(type.containsKey(p_n)){
			if(!noendalert)
				p.sendMessage(ChatColor.DARK_AQUA + "Abbrechen mit \"ende\".");
			if(shiftedBankomats.contains(p_n)){
				if(type.get(p_n).equals(BankomatType.IN) || type.get(p_n).equals(BankomatType.OUT)){
					double amount;
					try{
						amount = Double.parseDouble(msg);
						continueBankomat(p_n, amount);
						evt.setCancelled(true);
						okay = true;
					} catch(NumberFormatException e){
						if(msg.toLowerCase().equals("ende")){
							p.sendMessage("Abbruch...");
							removeShifted(p_n);
							okay = true;
						}
						else{
							p.sendMessage(ChatColor.YELLOW + "Du musst eine Zahl eingeben. Keine Komma, einen Punkt: 10.1 statt 10,1");
						evt.setCancelled(true);
						okay = true;
						}
					}
				}
				if(type.get(p_n) != null)
				if(type.get(p_n).equals(BankomatType.TRANSFER_AMOUNT)){
					double amount;
					try{
						amount = Double.parseDouble(msg);
						removeShiftedNoMsg(p_n);
						amounts.put(p.getName(), amount);
						addShiftedBankomat(p.getName(), BankomatType.TRANSFER_PLAYERII);
						okay = true;
						evt.setCancelled(true);
						return;
					} catch(NumberFormatException e){
						if(msg.toLowerCase().equals("ende")){
							p.sendMessage("Abbruch...");
							removeShifted(p_n);
							okay = true;
						}
						else{
							p.sendMessage(ChatColor.YELLOW + "Du musst eine Zahl eingeben. Keine Komma, einen Punkt: 10.1 statt 10,1");
							okay = true;
						}
					}
				}
				if(type.get(p_n) != null)
				if(type.get(p_n).equals(BankomatType.TRANSFER_PLAYERII)){
					if(amounts.containsKey(p_n)){
						double amount = amounts.get(p_n);
						String p_n2 = msg;
						if(Account.hasAccount(p_n2)){
							Account.transfer(amount, p_n, p_n2);
							if(loopPlayers.contains(p_n)){
								p.sendMessage(ChatColor.DARK_AQUA + "Done.");
								removeShiftedNoMsg(p_n);
								p.sendMessage(ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
								addShiftedBankomat(p_n, BankomatType.CHOOSING);
							}
							else{
								p.sendMessage(ChatColor.DARK_AQUA + "Done.");
								removeShifted(p_n);
							}
							okay = true;
						}
						else{
							p.sendMessage(ChatColor.YELLOW + p_n2 + " hat kein Konto.");
							evt.setCancelled(true);
							removeShifted(p_n);
							okay = true;
						}
					}
				}
				if(type.get(p_n) != null)
				if(type.get(p_n).equals(BankomatType.CHOOSING)){
					if(msg.toLowerCase().equals("abheben") || msg.toLowerCase().equals("auszahlen") || msg.toLowerCase().equals("a")){
						removeShiftedNoMsg(p_n);
						evt.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Wie viel möchtest du auszahlen? (10,7 -> 10.7!)");
						addShiftedBankomat(p.getName(), BankomatType.OUT);
						okay = true;
						noendalert = true;
					}
					if(msg.toLowerCase().equals("einzahlen") || msg.toLowerCase().equals("e")){
						removeShiftedNoMsg(p_n);
						evt.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Wie viel möchtest du einzahlen? (10,7 -> 10.7!)");
						okay = true;
						addShiftedBankomat(p.getName(), BankomatType.IN);
						noendalert = true;
					}
					if(msg.toLowerCase().equals("kontostand") || msg.toLowerCase().equals("k")){
						Account.amountMsg(p_n);
						if(loopPlayers.contains(p_n)){
							p.sendMessage(ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
							addShiftedBankomat(p_n, BankomatType.CHOOSING);
						}
						else{
							removeShifted(p_n);
						}
						okay = true;
					}
					if(msg.toLowerCase().equals("konto erstellen") || msg.toLowerCase().equals("erstellen") || msg.toLowerCase().equals("C")){
						Account.createAccount(p_n);
						if(loopPlayers.contains(p_n)){
							p.sendMessage(ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
							addShiftedBankomat(p_n, BankomatType.CHOOSING);
						}
						else{
							removeShifted(p_n);
						}
						okay = true;
					}
					if(msg.toLowerCase().equals("überweisen")){
						removeShiftedNoMsg(p_n);
						Account.amountMsg(p_n);
						p.sendMessage(ChatColor.DARK_AQUA + "Wie viel möchtest du überweisen?");
						addShiftedBankomat(p_n, BankomatType.TRANSFER_AMOUNT);
						okay = true;
						noendalert = true;
					}
					if(msg.toLowerCase().equals("loop")){
						if(!loopPlayers.contains(p.getName())){
							p.sendMessage(ChatColor.DARK_AQUA + "Loop aktiviert, Bankomat ist solange aktiviert bis du abbrichst oder den Bankomat verlässt.");
						loopPlayers.add(p.getName());
						okay = true;
						}
						else{
							msg(p_n, ChatColor.RED + "Du bist bereits im Loop Modus.");
						}
					}
				}
				if(type.get(p_n) != null){
					if(type.get(p_n).equals(BankomatType.CREATE)){
						if(msg.toLowerCase().equals("ja") || msg.toLowerCase().equals("j") || msg.toLowerCase().equals("y")){
							Account.createAccount(p_n);
							msg(p_n, ChatColor.DARK_AQUA + "Konto erstellt.");
							okay = true;
							removeShifted(p_n);
						}
						else{
							removeShifted(p_n);
						}
					}
				}
				if(type.get(p_n) != null)
				if(type.get(p_n).equals(BankomatType.UCP_PASS)){
					MySQL.setPassword(p_n, msg);
					if(loopPlayers.contains(p_n)){
						p.sendMessage(ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
						addShiftedBankomat(p_n, BankomatType.CHOOSING);
					}
					else{
						removeShifted(p_n);
					}
					okay = true;
				}
				if(msg.toLowerCase().equals("ende")){
					removeShifted(p_n);
					if(loopPlayers.equals(p_n))
						loopPlayers.remove(p_n);
					okay = true;
				}
				if(msg.toLowerCase().equals("help") || msg.toLowerCase().equals("hilfe")){
					p.sendMessage(ChatColor.YELLOW + "Aktionen:");
					p.sendMessage(ChatColor.YELLOW + "Einzahlen");
					p.sendMessage(ChatColor.YELLOW + "Auszahlen");
					p.sendMessage(ChatColor.YELLOW + "Kontostand abrufen");
					p.sendMessage(ChatColor.YELLOW + "Konto erstellen");
					p.sendMessage(ChatColor.YELLOW + "oder Überweisen.");
					okay = true;
				}
				if(!okay){
					p.sendMessage(ChatColor.YELLOW + "Unbekannte Aktion, bitte wiederholen. Ende mit \"ende\"");
				}
				
			}
			evt.setCancelled(true);
		}
	}
	public static void continueBankomat(String p_n, double amount){
		BankomatType t = type.get(p_n);
		if(t.equals(BankomatType.OUT)){
			Account.payoutFromAccount(p_n, amount);
			if(loopPlayers.contains(p_n)){
				msg(p_n, ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
				addShiftedBankomat(p_n, BankomatType.CHOOSING);
			}
			else{
				removeShifted(p_n);
			}
		}
		if(t.equals(BankomatType.IN)){
			Account.payinToAccount(p_n, amount);
			if(loopPlayers.contains(p_n)){
				msg(p_n, ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
				addShiftedBankomat(p_n, BankomatType.CHOOSING);
			}
			else{
				removeShifted(p_n);
			}
		}
	}
	public static boolean isShifted(String p_n){
		return shiftedBankomats.contains(p_n);
	}
	public static void removeShifted(String p_n){
		if(isShifted(p_n)){
			shiftedBankomats.remove(p_n);
			type.remove(p_n);
			msg(p_n, ChatColor.YELLOW + "Chat reaktiviert.");
		}
	}
	public static void removeShiftedNoMsg(String p_n){
		if(isShifted(p_n)){
			shiftedBankomats.remove(p_n);
			type.remove(p_n);
		}
	}
	public static void msg(String p_n, String msg){
		Bukkit.getPlayer(p_n).sendMessage(msg);
	}
	public static void reset(){
		shiftedBankomats = new ArrayList<String>();
		type = new HashMap<String, BankomatType>();
		loopPlayers = new ArrayList<String>();
	}
}

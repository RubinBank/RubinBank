package me.criztovyl.clicklesssigns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.criztovyl.clicklesssigns.ClicklessSignType;
import me.criztovyl.rubinbank.account.Account;
import me.criztovyl.rubinbank.tools.ClicklessSignArg;
import me.criztovyl.rubinbank.tools.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TimeShift {
	private static Map<String, ClicklessSignType> type = new HashMap<String, ClicklessSignType>();
	private static Map<String, Double> amounts = new HashMap<String, Double>();
	private static HashMap<String, HashMap<ClicklessSignArg, String>> args = new HashMap<String, HashMap<ClicklessSignArg,String>>();
	private static ArrayList<String> shifted = new ArrayList<String>();
	private static ArrayList<String> loopPlayers = new ArrayList<String>();
	private static boolean noendalert;
	public static void addShiftedBankomat(String p_n, ClicklessSignType t){
		shifted.add(p_n);
		type.put(p_n, t);
		msg(p_n, ChatColor.DARK_AQUA + "Chat deaktiviert.");
		switch(t){
		case TRANSFER_PLAYERII:
			msg(p_n, ChatColor.DARK_AQUA + "An wen möchtest du überweisen?");
			break;
		case BANKOMAT_LOC:
			msg(p_n, ChatColor.DARK_AQUA + "Wo steht dieses Schild? e.g. \"Bahnhof Spawn\"");
			break;
		}
	}
	public static String typeMsg(ClicklessSignType t){
		switch(t){
		case IN:
			return ChatColor.DARK_AQUA + "Wie viel möchtest du einzahlen? (10,7 -> 10.7!)";
		case OUT:
			return ChatColor.DARK_AQUA + "Wie viel möchtest du auszahlen? (10,7 -> 10.7!)";
		case TRANSFER:
			return ChatColor.DARK_AQUA + "Wie viel möchtest du überweisen? (10,7 -> 10.7!)";
		default:
			return "";
		}
	}
	public static void ChatEvent(AsyncPlayerChatEvent evt){
		Player p = evt.getPlayer();
		String p_n = p.getName();
		String msg = evt.getMessage();
		boolean okay = false;
		if(type.containsKey(p_n)){
			if(!noendalert)
				p.sendMessage(ChatColor.DARK_AQUA + "Abbrechen mit \"ende\".");
			double amount;
			switch(type.get(p_n)){
			case IN:
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
				break;
			case OUT:
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
				break;
			case TRANSFER_AMOUNT:
				try{
					amount = Double.parseDouble(msg);
					removeShiftedNoMsg(p_n);
					amounts.put(p.getName(), amount);
					addShiftedBankomat(p.getName(), ClicklessSignType.TRANSFER_PLAYERII);
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
				break;
			case TRANSFER_PLAYERII:
				if(amounts.containsKey(p_n)){
					amount = amounts.get(p_n);
					String p_n2 = msg;
					if(Account.hasAccount(p_n2)){
						Account.transfer(amount, p_n, p_n2);
						if(loopPlayers.contains(p_n)){
							p.sendMessage(ChatColor.DARK_AQUA + "Done.");
							removeShiftedNoMsg(p_n);
							p.sendMessage(ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
							addShiftedBankomat(p_n, ClicklessSignType.CHOOSING);
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
				break;
			case CHOOSING:
				if(msg.toLowerCase().equals("abheben") || msg.toLowerCase().equals("auszahlen") || msg.toLowerCase().equals("a")){
					removeShiftedNoMsg(p_n);
					evt.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Wie viel möchtest du auszahlen? (10,7 -> 10.7!)");
					addShiftedBankomat(p.getName(), ClicklessSignType.OUT);
					okay = true;
					noendalert = true;
					break;
				}
				if(msg.toLowerCase().equals("einzahlen") || msg.toLowerCase().equals("e")){
					removeShiftedNoMsg(p_n);
					evt.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Wie viel möchtest du einzahlen? (10,7 -> 10.7!)");
					okay = true;
					addShiftedBankomat(p.getName(), ClicklessSignType.IN);
					noendalert = true;
					break;
				}
				if(msg.toLowerCase().equals("kontostand") || msg.toLowerCase().equals("k")){
					Account.amountMsg(p_n);
					if(loopPlayers.contains(p_n)){
						p.sendMessage(ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
						addShiftedBankomat(p_n, ClicklessSignType.CHOOSING);
					}
					else{
						removeShifted(p_n);
					}
					okay = true;
					break;
				}
				if(msg.toLowerCase().equals("konto erstellen") || msg.toLowerCase().equals("erstellen") || msg.toLowerCase().equals("c")){
					Account.createAccount(p_n);
					if(loopPlayers.contains(p_n)){
						p.sendMessage(ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
						addShiftedBankomat(p_n, ClicklessSignType.CHOOSING);
					}
					else{
						removeShifted(p_n);
					}
					okay = true;
					break;
				}
				if(msg.toLowerCase().equals("überweisen") || msg.toLowerCase().equals("ü")){
					removeShiftedNoMsg(p_n);
					Account.amountMsg(p_n);
					p.sendMessage(ChatColor.DARK_AQUA + "Wie viel möchtest du überweisen?");
					addShiftedBankomat(p_n, ClicklessSignType.TRANSFER_AMOUNT);
					okay = true;
					noendalert = true;
					break;
				}
				if(msg.toLowerCase().equals("loop")){
					if(!loopPlayers.contains(p.getName())){
						p.sendMessage(ChatColor.DARK_AQUA + "Loop aktiviert, das Schild ist solange aktiviert bis du abbrichst oder das Schild verlässt.");
					loopPlayers.add(p.getName());
					okay = true;
					}
					else{
						msg(p_n, ChatColor.RED + "Du bist bereits im Loop Modus.");
					}
					break;
				}
				break;
			case BANKOMAT_LOC:
				addArg(p_n, ClicklessSignArg.LOCATION, msg);
				removeShifted(p_n);
				okay = true;
				HashMap<ClicklessSignArg, String> args = getArgs(p_n);
				if(args.get(ClicklessSignArg.MULTI).equals("1")){
					MySQL.insertBankomat(args.get(ClicklessSignArg.LOCX), args.get(ClicklessSignArg.LOCY), args.get(ClicklessSignArg.LOCZ), args.get(ClicklessSignArg.LOCWORLD),
							args.get(ClicklessSignArg.POS), args.get(ClicklessSignArg.LOCATION));
				}
				else{
					
				}
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
				p.sendMessage(ChatColor.YELLOW + "oder Überweisen.");
				okay = true;
			}
			if(!okay){
				p.sendMessage(ChatColor.YELLOW + "Unbekannte Aktion. Ende mit \"ende\"");
			}
			evt.setCancelled(true);
		}
	}
	public static void continueBankomat(String p_n, double amount){
		ClicklessSignType t = type.get(p_n);
		if(t.equals(ClicklessSignType.OUT)){
			Account.payoutFromAccount(p_n, amount);
			if(loopPlayers.contains(p_n)){
				msg(p_n, ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
				addShiftedBankomat(p_n, ClicklessSignType.CHOOSING);
			}
			else{
				removeShifted(p_n);
			}
		}
		if(t.equals(ClicklessSignType.IN)){
			Account.payinToAccount(p_n, amount);
			if(loopPlayers.contains(p_n)){
				msg(p_n, ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
				addShiftedBankomat(p_n, ClicklessSignType.CHOOSING);
			}
			else{
				removeShifted(p_n);
			}
		}
	}
	public static boolean isShifted(String p_n){
		return shifted.contains(p_n);
	}
	public static void removeShifted(String p_n){
		if(isShifted(p_n)){
			shifted.remove(p_n);
			type.remove(p_n);
			msg(p_n, ChatColor.YELLOW + "Chat reaktiviert.");
		}
	}
	public static void removeShiftedNoMsg(String p_n){
		if(isShifted(p_n)){
			shifted.remove(p_n);
			type.remove(p_n);
		}
	}
	public static void msg(String p_n, String msg){
		Bukkit.getPlayer(p_n).sendMessage(msg);
	}
	public static void reset(){
		shifted = new ArrayList<String>();
		type = new HashMap<String, ClicklessSignType>();
		loopPlayers = new ArrayList<String>();
	}
	public static void storeArgs(String p_n, HashMap<ClicklessSignArg, String> storeArgs){
		args.put(p_n, storeArgs);
	}
	public static HashMap<ClicklessSignArg, String> getArgs(String p_n){
		return args.get(p_n);
	}
	public static void addArg(String p_n, ClicklessSignArg arg_k, String arg_v){
		HashMap<ClicklessSignArg, String> args = getArgs(p_n);
		args.put(arg_k, arg_v);
		storeArgs(p_n, args);
	}
}

package me.criztovyl.rubinbank.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.config.Config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TimeShift {
	private static Map<String, SignType> type = new HashMap<String, SignType>();
	private static Map<String, Double> amounts = new HashMap<String, Double>();
	private static HashMap<String, HashMap<SignArg, String>> args = new HashMap<String, HashMap<SignArg,String>>();
	private static ArrayList<String> shifted = new ArrayList<String>();
	private static ArrayList<String> loopPlayers = new ArrayList<String>();
	public static void addShifted(String p_n, SignType t){
		shifted.add(p_n);
		type.put(p_n, t);
		msg(p_n, ChatColor.DARK_AQUA + "Chat deaktiviert.\n Abbruch mit \"Ende\"");
		switch(t){
		case IN:
			if(Bukkit.getPlayer(p_n).getItemInHand().getTypeId() == Config.getMinorID() || Bukkit.getPlayer(p_n).getItemInHand().getTypeId() == Config.getMajorID()){
				if(RubinBank.getBank().hasAccount(p_n)){
					RubinBank.getBank().getAccount(p_n).payinItemInHand();
				}
				else{
					msg(p_n, ChatColor.RED + "Du hast kein Konto auf das du einzahlen kannst!");
				}
				break;
			}
			msg(p_n, ChatColor.DARK_AQUA + "Wie viel m\u00F6chtest du einzahlen?");
			break;
		case OUT:
			if(RubinBank.getBank().getAccount(p_n).getBalance() > 0){
				RubinBank.getBank().getAccount(p_n).sendBalanceMessage();
				msg(p_n, ChatColor.DARK_AQUA + "Wie viel m\u00F6chtest du abheben?");
			}
			else{
				msg(p_n, ChatColor.YELLOW + "Dein Konto ist leer.");
				removeShifted(p_n);
			}
			break;
		case TRANSFER_PLAYERII:
			msg(p_n, ChatColor.DARK_AQUA + "An wen m\u00F6chtest du \u00FCberweisen?");
			break;
		case BANKOMAT_LOC:
			msg(p_n, ChatColor.DARK_AQUA + "Wo steht dieses Schild? e.g. \"Bahnhof Spawn\"");
			break;
		case TRANSFER:
			if(RubinBank.getBank().getAccount(p_n).getBalance()  > 0){
				RubinBank.getBank().getAccount(p_n).sendBalanceMessage();
				msg(p_n, ChatColor.DARK_AQUA + "Wie viel möchtest du \u00FCberweisen?");
				type.put(p_n, SignType.TRANSFER_AMOUNT);
			}
			else{
				msg(p_n, ChatColor.YELLOW + "Dein Konto ist leer.");
				removeShifted(p_n);
			}
			break;
		case CREATE:
			if(RubinBank.getBank().hasAccount(p_n)){
				msg(p_n, ChatColor.RED + "Du hast schon ein Konto!");
				break;
			}
			else{
				RubinBank.getBank().createAccount(p_n);
				msg(p_n, ChatColor.DARK_AQUA + "Konto erstellt.");
				break;
			}
		case CHOOSING:
			msg(p_n, ChatColor.DARK_AQUA + "Möchtest du " + ChatColor.UNDERLINE + "A" + ChatColor.RESET + ChatColor.DARK_AQUA + "bheben, " + ChatColor.UNDERLINE + "E" + ChatColor.RESET +
					ChatColor.DARK_AQUA + "inzahlen, " + ChatColor.UNDERLINE + 		"Ü" + ChatColor.RESET + ChatColor.DARK_AQUA + "berweisen oder " + "deinen " + 
					ChatColor.UNDERLINE + "K" + ChatColor.RESET + ChatColor.DARK_AQUA + "ontostand abrufen?");
			break;
		case AMOUNT:
			if(RubinBank.getBank().hasAccount(p_n)){
				RubinBank.getBank().getAccount(p_n).sendBalanceMessage();
			}
			else{
				msg(p_n, ChatColor.RED + "Du hast kein Konto!");
			}
			break;
		}
	}
	public static void ChatEvent(AsyncPlayerChatEvent evt){
		String p_n = evt.getPlayer().getName();
		String msg = evt.getMessage();
		if(type.containsKey(p_n)){
			double amount;
			switch(type.get(p_n)){
			case IN:
				try{
					amount = Double.parseDouble(msg);
					if(amount < 0){
						msg(p_n, ChatColor.RED + "Einzahlungen können nicht negativ sein.");
						break;
					}
					continueBankomat(p_n, amount);
				} catch(NumberFormatException e){
					if(msg.toLowerCase().equals("ende")){
						msg(p_n, ChatColor.RED + "Abbruch...");
						removeShifted(p_n);
					}
					else{
						msg(p_n, ChatColor.YELLOW + "Du musst eine Zahl eingeben. Keine Komma, einen Punkt: 10.1 statt 10,1");
					}
				}
				break;
			case OUT:
				try{
					amount = Double.parseDouble(msg);
					if(amount < 0){
						msg(p_n, ChatColor.RED + "Auszahlungen können nicht negativ sein.");
						break;
					}
					continueBankomat(p_n, amount);
				} catch(NumberFormatException e){
					if(msg.toLowerCase().equals("ende")){
						msg(p_n, ChatColor.RED + "Abbruch...");
						removeShifted(p_n);
					}
					else{
						msg(p_n, ChatColor.YELLOW + "Du musst eine Zahl eingeben. Keine Komma, einen Punkt: 10.1 statt 10,1");
					}
				}
				break;
			case TRANSFER_AMOUNT:
				try{
					amount = Double.parseDouble(msg);
					if(amount < 0){
						msg(p_n, ChatColor.RED + "Überweisungen sollten Positiv sein!");
						break;
					}
					amounts.put(p_n, amount);
					removeShiftedNoMsg(p_n);
					addShifted(p_n, SignType.TRANSFER_PLAYERII);
					break;
				} catch(NumberFormatException e){
					if(msg.toLowerCase().equals("ende")){
						msg(p_n, ChatColor.RED + "Abbruch...");
						removeShifted(p_n);
					}
					else{
						msg(p_n, ChatColor.YELLOW + "Du musst eine Zahl eingeben. Keine Komma, einen Punkt: 10.1 statt 10,1");
					}
				}
				break;
			case TRANSFER_PLAYERII:
				if(amounts.containsKey(p_n)){
					amount = amounts.get(p_n);
					String p_n2 = msg;
					if(RubinBank.getBank().hasAccount(p_n2)){
						RubinBank.getBank().transfer(p_n, p_n2, amount);
						if(loopPlayers.contains(p_n)){
							msg(p_n, ChatColor.DARK_AQUA + "Done.");
							msg(p_n, ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
							type.put(p_n, SignType.CHOOSING);
						}
						else{
							msg(p_n, ChatColor.DARK_AQUA + "Done.");
							removeShifted(p_n);
						}
					}
					else{
						msg(p_n, ChatColor.YELLOW + p_n2 + " hat kein Konto.");
						removeShifted(p_n);
					}
				}
				break;
			case CHOOSING:
				if(msg.toLowerCase().equals("abheben") || msg.toLowerCase().equals("auszahlen") || msg.toLowerCase().equals("a")){
					removeShiftedNoMsg(p_n);
					addShifted(p_n, SignType.OUT);
					break;
				}
				if(msg.toLowerCase().equals("einzahlen") || msg.toLowerCase().equals("e")){
					removeShiftedNoMsg(p_n);
					addShifted(p_n, SignType.IN);
					break;
				}
				if(msg.toLowerCase().equals("kontostand") || msg.toLowerCase().equals("k")){
					RubinBank.getBank().getAccount(p_n).sendBalanceMessage();
					if(loopPlayers.contains(p_n)){
						removeShiftedNoMsg(p_n);
						addShifted(p_n, SignType.AMOUNT);
					}
					else{
						removeShifted(p_n);
					}
					break;
				}
				if(msg.toLowerCase().equals("überweisen") || msg.toLowerCase().equals("ü")){
					removeShiftedNoMsg(p_n);
					addShifted(p_n, SignType.TRANSFER);
					break;
				}
				if(msg.toLowerCase().equals("loop")){
					if(!loopPlayers.contains(p_n)){
						msg(p_n, ChatColor.DARK_AQUA + "Loop aktiviert, das Schild ist solange aktiviert bis du abbrichst oder das Schild verlässt.");
					loopPlayers.add(p_n);
					}
					else{
						msg(p_n, ChatColor.RED + "Du bist bereits im Loop Modus.");
					}
					break;
				}
				msg(p_n, ChatColor.YELLOW + "Unbekannte Aktion. Ende mit \"ende\"");
				break;
			case BANKOMAT_LOC:
				addArg(p_n, SignArg.LOCATION, msg);
				removeShifted(p_n);
				HashMap<SignArg, String> args = getArgs(p_n);
				if(args.get(SignArg.MULTI).equals("1")){
					MySQL.insertBankomat(args.get(SignArg.LOCX), args.get(SignArg.LOCY), args.get(SignArg.LOCZ), args.get(SignArg.LOCWORLD),
							args.get(SignArg.POS), args.get(SignArg.LOCATION));
				}
				else{
					MySQL.insertnoMultiBankomat(args.get(SignArg.LOCX), args.get(SignArg.LOCY), args.get(SignArg.LOCZ), args.get(SignArg.LOCWORLD),
							args.get(SignArg.POS), args.get(SignArg.TYPE), args.get(SignArg.LOCATION));
				}
			}
			if(msg.toLowerCase().equals("ende")){
				removeShifted(p_n);
				if(loopPlayers.equals(p_n))
					loopPlayers.remove(p_n);
			}
			if(msg.toLowerCase().equals("help") || msg.toLowerCase().equals("hilfe")){
				msg(p_n, ChatColor.YELLOW + "Aktionen:");
				msg(p_n, ChatColor.YELLOW + "Einzahlen (Alias: e)");
				msg(p_n, ChatColor.YELLOW + "Auszahlen (Alias: a)");
				msg(p_n, ChatColor.YELLOW + "deinen Kontostand abrufen (Alias: k)");
				msg(p_n, ChatColor.YELLOW + "oder Überweisen (Alias: ü)");
				msg(p_n, ChatColor.YELLOW + "Gro\u00DF- und Kleinschreibung wird nicht beachtet.");
			}
			evt.setCancelled(true);
		}
	}
	public static void continueBankomat(String p_n, double amount){
		SignType t = type.get(p_n);
		if(t.equals(SignType.OUT)){
			RubinBank.getBank().getAccount(p_n).payOutViaInv(amount);
			if(loopPlayers.contains(p_n)){
				msg(p_n, ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
				addShifted(p_n, SignType.CHOOSING);
			}
			else{
				removeShifted(p_n);
			}
		}
		if(t.equals(SignType.IN)){
			RubinBank.getBank().getAccount(p_n).payInViaInv(amount);
			if(loopPlayers.contains(p_n)){
				msg(p_n, ChatColor.DARK_AQUA + "Was möchtest du als nächstes tun?");
				addShifted(p_n, SignType.CHOOSING);
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
		type = new HashMap<String, SignType>();
		loopPlayers = new ArrayList<String>();
	}
	public static void storeArgs(String p_n, HashMap<SignArg, String> storeArgs){
		args.put(p_n, storeArgs);
	}
	public static HashMap<SignArg, String> getArgs(String p_n){
		return args.get(p_n);
	}
	public static void addArg(String p_n, SignArg arg_k, String arg_v){
		HashMap<SignArg, String> args = getArgs(p_n);
		args.put(arg_k, arg_v);
		storeArgs(p_n, args);
	}
}

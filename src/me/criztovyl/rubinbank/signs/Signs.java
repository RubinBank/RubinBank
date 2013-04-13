package me.criztovyl.rubinbank.signs;

import java.util.ArrayList;
import java.util.HashMap;

import me.criztovyl.clickless.ClicklessPlugin;
import me.criztovyl.clickless.ClicklessSign;
import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.bankomat.BankomatType;
import me.criztovyl.rubinbank.bankomat.TriggerPosition;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.tools.Tools;
import me.criztovyl.timeshift.TimeShifter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Signs {
	public static void addSign(final Location loc, BankomatType t, TriggerPosition pos, final String place){
		 Location trigger_ = null;
		switch(pos){
		case DOWN:
			trigger_ = new Location(loc.getWorld(), loc.getBlockX(), loc.getY()-1, loc.getBlockZ());
			break;
		case UP:
			trigger_ = new Location(loc.getWorld(), loc.getBlockX(), loc.getY()+2, loc.getBlockZ());
			break;
		}
		final Location trigger = trigger_;
		switch(t){
		case AMOUNT:
			ClicklessPlugin.getClickless().addClicklessSign(new ClicklessSign() {
				
				@Override
				public TimeShifter getTimeShifter() {
					return null;
				}
				
				@Override
				public void action(String p_n) {
					if(RubinBank.getHelper().getBank().hasAccount(p_n))
						RubinBank.getHelper().getBank().getAccount(p_n).sendBalanceMessage(ChatColor.BLUE);
					else
						Tools.msg(p_n, ChatColor.RED + "Du hast kein Konto!");
				}

				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					return trigger;
				}

				@Override
				public HashMap<String, String> getOptions() {
					HashMap<String, String> options = new HashMap<String, String>();
					options.put("place", place);
					return options;
				}
			});
			break;
		case CHOOSING:
			ClicklessPlugin.getClickless().addClicklessSign(new ClicklessSign() {
				TimeShifter shifter = new TimeShifter() {
					ArrayList<String> shifted = new ArrayList<String>();
					HashMap<String, Boolean> playerSuccess = new HashMap<String, Boolean>();
					HashMap<String, String> playerDo = new HashMap<String, String>();
					HashMap<String, String> transferArgs = new HashMap<String, String>();
					@Override
					public void preChatAction(String playername) {
						if(RubinBank.getHelper().getBank().hasAccount(playername)){
							msg(playername, "Chat deaktiviert.");
							msg(playername, ChatColor.AQUA + "Möchtest du auf dein Konto einzahlen(e), von deinem Konto " +
									"auszahlen(a), deinen Kontostand abrufen(k) oder etwas Überweisen(ü)?");
						}
						else{
							msg(playername, ChatColor.RED + "Du hast kein Konto! Möchtest du eins erstellen? (Ja/Nein)");
							playerDo.put(playername, "CREATE");
						}
					}
					
					@Override
					public void onChatAction(AsyncPlayerChatEvent evt) {
						String msg = evt.getMessage();
						String p_n = evt.getPlayer().getName();
						evt.setCancelled(true);
						if(msg.toLowerCase().equals("exit")){
							removePlayer(p_n);
							return;
						}
						if(playerDo.containsKey(p_n)){
							if(playerDo.get(p_n).toLowerCase().equals("in")){
								msg = msg.replace(",", ".");
								double amount = 0;
								try{
									amount = Double.parseDouble(msg);
									RubinBank.getHelper().getBank().getAccount(p_n).payInViaInv(
											amount,
											getOptions().get("place"));
									playerSuccess.put(p_n, true);
									removePlayer(p_n);
									return;
								} catch (NumberFormatException e) {
									msg(p_n, ChatColor.RED + "'" + msg + "' ist keine gültige Zahl!");
									return;
								}
							}
							if(playerDo.get(p_n).toLowerCase().equals("out")){
								msg = msg.replace(",", ".");
								double amount = 0;
								try{
									amount = Double.parseDouble(msg);
									RubinBank.getHelper().getBank().getAccount(p_n).payOutViaInv(
											amount,
											getOptions().get("place"));
									playerSuccess.put(p_n, true);
									removePlayer(p_n);
									return;
								} catch (NumberFormatException e) {
									msg(p_n, ChatColor.RED + "'" + msg + "' ist keine gültige Zahl!");
									return;
								}
							}
							if(playerDo.get(p_n).toUpperCase().equals("TRANSFER_PLAYERII")){
								if(RubinBank.getHelper().getBank().hasAccount(p_n)){
									transferArgs.put(p_n, msg);
									playerDo.put(p_n, "TRANSFER_AMOUNT");
									RubinBank.getHelper().getBank().getAccount(p_n).sendBalanceMessage();
									msg(p_n, ChatColor.GREEN + "Wie viel möchtest du überweisen?");
									return;
								}
								else{
									msg(p_n, ChatColor.RED + "'" + msg + "' hat kein Konto.");
									msg(p_n, ChatColor.YELLOW + "An wen möchtest du überweisen? Ende mit \"ende\"");
									return;
								}
							}
							if(playerDo.get(p_n).toUpperCase().equals("TRANSFER_AMOUNT")){
								double amount = 0;
								msg = msg.replace(",", ".");
								try{
									amount = Double.parseDouble(msg);
									RubinBank.getHelper().getBank().transfer(
											p_n, 
											transferArgs.get(p_n),
											amount,
											getOptions().get("place"));
									playerSuccess.put(p_n, true);
									removePlayer(p_n);
									return;
								} catch(NumberFormatException e){
									msg(p_n, ChatColor.RED + "'" + msg + "' ist keine gültige Zahl!");
									msg(p_n, ChatColor.GREEN + "Wie viel möchtest du überweisen? Ende mit \"ende\"");
									return;
								}
							}
							if(playerDo.get(p_n).toLowerCase().equals("create")){
								if(msg.toLowerCase().equals("ja") || msg.toLowerCase().equals("j")){
									RubinBank.getHelper().getBank().createAccount(p_n);
									msg(p_n, ChatColor.GREEN + "Done :)");
									removePlayer(p_n);
									return;
								}
								if(msg.toLowerCase().equals("nein") || msg.toLowerCase().equals("n")){
									msg(p_n, ChatColor.BLUE + "Abbruch...");
									removePlayer(p_n);
									return;
								}
							}
						}
						//playerDo End
						else{
							if(msg.toLowerCase().equals("einzahlen") || msg.toLowerCase().equals("e")){
								if(evt.getPlayer().getItemInHand().getTypeId() == Config.getMajorID() ||
										evt.getPlayer().getItemInHand().getTypeId() == Config.getMinorID()){
									if(RubinBank.getHelper().getBank().hasAccount(p_n)){
										RubinBank.getHelper().getBank().getAccount(p_n).payInItemInHand(
												getOptions().get("place"));
										msg(p_n, ChatColor.DARK_AQUA + "Done :)");
										removePlayer(p_n);
										return;
									}
								}
								playerDo.put(p_n, "IN");
								playerSuccess.put(p_n, false);
								msg(p_n, ChatColor.GREEN + "Wie viel möchtest du einzahlen?");
								return;
							}
							if(msg.toLowerCase().equals("auszahlen") || msg.toLowerCase().equals("a")){
								playerDo.put(p_n, "OUT");
								playerSuccess.put(p_n, false);
								RubinBank.getHelper().getBank().getAccount(p_n).sendBalanceMessage();
								msg(p_n, ChatColor.GREEN + "Wie viel möchtest du auszahlen?");
								return;
							}
							if(msg.toLowerCase().equals("kontostand") || msg.toLowerCase().equals("k")){
								playerSuccess.put(p_n, true);
								RubinBank.getHelper().getBank().getAccount(p_n).sendBalanceMessage();
								return;
							}
							if(msg.toLowerCase().equals("überweisen") || msg.toLowerCase().equals("ü")){
								playerDo.put(p_n, "TRANSFER_PLAYERII");
								playerSuccess.put(p_n, false);
								msg(p_n, ChatColor.GREEN + "An wen möchtest du überweisen?");
								return;
							}
							if(msg.toLowerCase().equals("ende")){
								playerSuccess.put(p_n, true);
								msg(p_n, ChatColor.BLUE + "Abbruch...");
								return;
							}
							else{
								msg(p_n, ChatColor.RED + "Unbekannte Aktion. Ende mit \"ende\".");
							}
						}
					}
					
					@Override
					public boolean getSuccess(String playername) {
						if(playerSuccess.containsKey(playername))
							return playerSuccess.get(playername);
						else
							return false;
					}
					
					@Override
					public void afterChatAction(String playername) {
						msg(playername, "Chat reaktiviert.");
					}
					
					private void msg(String p_n, String msg){
						Bukkit.getServer().getPlayer(p_n).sendMessage(msg);
					}

					@Override
					public void removePlayer(String playername) {
						playerDo.remove(playername);
						playerSuccess.remove(playername);
						shifted.remove(playername);
						msg(playername, "Chat reaktiviert.");
					}

					@Override
					public void addPlayer(String playername) {
						shifted.add(playername);
						msg(playername, "Chat deaktiviert.");
						preChatAction(playername);
					}

					@Override
					public boolean hasPlayer(String playername) {
						return shifted.contains(playername);
					}
				};
				@Override
				public TimeShifter getTimeShifter() {
					return shifter;
				}
				
				@Override
				public void action(String p_n) {}

				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					return trigger;
				}

				@Override
				public HashMap<String, String> getOptions() {
					HashMap<String, String> options = new HashMap<String, String>();
					options.put("place", place);
					return options;
				}
			});
			break;
		case CREATE:
			ClicklessPlugin.getClickless().addClicklessSign(new ClicklessSign() {
				
				@Override
				public TimeShifter getTimeShifter() {
					return null;
				}
				
				@Override
				public void action(String p_n) {
					RubinBank.getHelper().getBank().createAccount(p_n);
				}

				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					return trigger;
				}

				@Override
				public HashMap<String, String> getOptions() {
					HashMap<String, String> options = new HashMap<String, String>();
					options.put("place", place);
					return options;
				}
			});
			break;
		case IN:
			ClicklessPlugin.getClickless().addClicklessSign(new ClicklessSign() {
				TimeShifter shifter = new TimeShifter() {
					ArrayList<String> shifteds = new ArrayList<String>();
					@Override
					public void removePlayer(String playername) {
						msg(playername, ChatColor.YELLOW + "Chat reaktiviert.");
						shifteds.remove(playername);
					}
					
					@Override
					public void preChatAction(String playername) {
						if(RubinBank.getHelper().getBank().hasAccount(playername)){
							if(Tools.hasMajorOrMinorInHand(playername)){
								RubinBank.getHelper().getBank().getAccount(playername).payInItemInHand(
										getOptions().get("place"));
								msg(playername, ChatColor.DARK_AQUA + "Done :)");
								removePlayer(playername);
								return;
							}
							msg(playername, ChatColor.DARK_AQUA + "Wie viel möchtest du einzahlen?");
						}
						else{
							msg(playername, ChatColor.RED + "Du hast noch kein Konto!");
							removePlayer(playername);
						}
					}
					
					@Override
					public void onChatAction(AsyncPlayerChatEvent evt) {
						String msg = evt.getMessage();
						msg = msg.replace(",", ".");
						double amount = 0;
						try{
							amount = Double.parseDouble(msg);
						} catch(NumberFormatException e){
							msg(evt.getPlayer().getName(), ChatColor.RED + "'" + msg + "' ist keine gültig Zahl!");
							evt.setCancelled(true);
						}
						RubinBank.getHelper().getBank().getAccount(evt.getPlayer().getName()).payInViaInv(
								amount,
								getOptions().get("place"));
						msg(evt.getPlayer().getName(), ChatColor.GREEN + "Done :)");
						evt.setCancelled(true);
					}
					
					@Override
					public boolean hasPlayer(String playername) {
						return shifteds.contains(playername);
					}
					
					@Override
					public boolean getSuccess(String playername) {
						return !hasPlayer(playername);
					}
					
					@Override
					public void afterChatAction(String playername) {
						removePlayer(playername);
					}
					
					@Override
					public void addPlayer(String playername) {
						shifteds.add(playername);
						msg(playername, ChatColor.YELLOW + "Chat deaktiviert.");
						preChatAction(playername);
					}
					void msg(String playername, String msg){
						Player p = Bukkit.getServer().getPlayer(playername);
						if(p != null){
							p.sendMessage(msg);
						}
					}
				};
				@Override
				public TimeShifter getTimeShifter() {
					return shifter;
				}
				
				@Override
				public void action(String p_n) {
					if(RubinBank.getHelper().getBank().hasAccount(p_n))
						RubinBank.getHelper().getBank().getAccount(p_n).payInItemInHand(getOptions().get("place"));
				}

				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					return trigger;
				}

				@Override
				public HashMap<String, String> getOptions() {
					HashMap<String, String> options = new HashMap<String, String>();
					options.put("place", place);
					return options;
				}
			});
			break;
		case OUT:
			ClicklessPlugin.getClickless().addClicklessSign(new ClicklessSign() {
				TimeShifter shifter = new TimeShifter() {
					ArrayList<String> shifteds = new ArrayList<String>();
					@Override
					public void removePlayer(String playername) {
						shifteds.remove(playername);
						msg(playername, ChatColor.YELLOW + "Chat reaktiviert");
					}
					
					@Override
					public void preChatAction(String playername) {
						if(RubinBank.getHelper().getBank().hasAccount(playername)){
							RubinBank.getHelper().getBank().getAccount(playername).sendBalanceMessage();
							msg(playername, ChatColor.GREEN + "Wie viel möchtest du Abheben?");
						}
						else{
							msg(playername, ChatColor.RED + "Du hast kein Konto!");
						}
					}
					
					@Override
					public void onChatAction(AsyncPlayerChatEvent evt) {
						msg(evt.getPlayer().getName(), "Chat Event.");
						String msg = evt.getMessage().replace(",", ".");
						double amount = 0;
						try{
							amount = Double.parseDouble(msg);
						} catch(NumberFormatException e){
							msg(evt.getPlayer().getName(), ChatColor.RED + "'" + msg +"' ist keine gültige Zahl!");
						}
						if(RubinBank.getHelper().getBank().hasAccount(evt.getPlayer().getName())){
							RubinBank.getHelper().getBank().getAccount(evt.getPlayer().getName()).payOutViaInv(
									amount,
									getOptions().get("place"));
							msg(evt.getPlayer().getName(), ChatColor.GREEN + "Done :)");
						}
						else{
							msg(evt.getPlayer().getName(), ChatColor.RED + "Du hast kein Konto!");
						}
						removePlayer(evt.getPlayer().getName());
						evt.setCancelled(true);
					}
					
					@Override
					public boolean hasPlayer(String playername) {
						return shifteds.contains(playername);
					}
					
					@Override
					public boolean getSuccess(String playername) {
						return !hasPlayer(playername);
					}
					
					@Override
					public void afterChatAction(String playername) {
						removePlayer(playername);
					}
					
					@Override
					public void addPlayer(String playername) {
						shifteds.add(playername);
						msg(playername, ChatColor.YELLOW + "Chat deaktiviert.");
						preChatAction(playername);
					}
					void msg(String playername, String msg){
						Player p = Bukkit.getPlayer(playername);
						if(p != null){
							p.sendMessage(msg);
						}
					}
				};
				@Override
				public TimeShifter getTimeShifter() {
					return shifter;
				}
				
				@Override
				public void action(String p_n) {}

				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					return trigger;
				}

				@Override
				public HashMap<String, String> getOptions() {
					HashMap<String, String> options = new HashMap<String, String>();
					options.put("place", place);
					return options;
				}
			});
			break;
		case TRANSFER:
			ClicklessPlugin.getClickless().addClicklessSign(new ClicklessSign() {
				TimeShifter shifter = new TimeShifter() {
					ArrayList<String> shifteds = new ArrayList<String>();
					HashMap<String, String> to = new HashMap<String, String>();
					@Override
					public void removePlayer(String playername) {
						shifteds.remove(playername);
						to.remove(playername);
						msg(playername, ChatColor.YELLOW + "Chat deaktiviert.");
					}
					
					@Override
					public void preChatAction(String playername) {
						if(RubinBank.getHelper().getBank().hasAccount(playername)){
							RubinBank.getHelper().getBank().getAccount(playername).sendBalanceMessage(ChatColor.BLUE);
							msg(playername, ChatColor.GREEN + "An wen möchtest du überweisen?");
						}
						else{
							msg(playername, ChatColor.RED + "Du hast kein Konto!");
							removePlayer(playername);
							return;
						}
					}
					
					@Override
					public void onChatAction(AsyncPlayerChatEvent evt) {
						if(evt.getMessage().toLowerCase().equals("ende")){
							removePlayer(evt.getPlayer().getName());
							return;
						}
						if(to.containsKey(evt.getPlayer().getName())){
							String msg = evt.getMessage().replace(",", ".");
							double amount = 0;
							try{
								amount = Double.parseDouble(msg);
							} catch(NumberFormatException e){
								msg(evt.getPlayer().getName(), ChatColor.RED + "'" + msg + "' ist keine gültige Zahl!");
								evt.setCancelled(true);
								msg(evt.getPlayer().getName(), ChatColor.GREEN + "Wie viel möchtest du überweisen?");
								return;
							}
							RubinBank.getHelper().getBank().transfer(
									evt.getPlayer().getName(), 
									to.get(evt.getPlayer().getName()),
									amount,
									getOptions().get("place"));
							msg(evt.getPlayer().getName(), ChatColor.GREEN + "Done :)");
							removePlayer(evt.getPlayer().getName());
						}
						else{
							String to_ = evt.getMessage();
							if(RubinBank.getHelper().getBank().hasAccount(to_)){
								to.put(evt.getPlayer().getName(), to_);
								msg(evt.getPlayer().getName(), ChatColor.GREEN + "Wie viel möchtest du an " +
								to_ + " überweisen?");
							}
							else{
								msg(evt.getPlayer().getName(), ChatColor.RED + "'" + to_ + "' hat kein Konto!");
								msg(evt.getPlayer().getName(), ChatColor.YELLOW + "Ende mit \"ende\"");
							}
						}
						evt.setCancelled(true);
					}
					
					@Override
					public boolean hasPlayer(String playername) {
						return shifteds.contains(playername);
					}
					
					@Override
					public boolean getSuccess(String playername) {
						return !hasPlayer(playername);
					}
					
					@Override
					public void afterChatAction(String playername) {}
					
					@Override
					public void addPlayer(String playername) {
						shifteds.add(playername);
						msg(playername, ChatColor.YELLOW + "Chat deaktiviert.");
						preChatAction(playername);
					}
					void msg(String playername, String msg){
						Player p = Bukkit.getServer().getPlayer(playername);
						if(p != null){
							p.sendMessage(msg);
						}
					}
				};
				@Override
				public TimeShifter getTimeShifter() {
					return shifter;
				}
				
				@Override
				public void action(String p_n) {}

				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					return trigger;
				}

				@Override
				public HashMap<String, String> getOptions() {
					HashMap<String, String> options = new HashMap<String, String>();
					options.put("place", place);
					return options ;
				}
			});
			break;
		}
	}
}


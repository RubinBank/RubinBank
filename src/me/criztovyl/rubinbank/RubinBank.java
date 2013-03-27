package me.criztovyl.rubinbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import me.criztovyl.rubinbank.account.Account;
import me.criztovyl.rubinbank.bank.Bank;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.listeners.Listeners;
import me.criztovyl.rubinbank.tools.MySQL;
import me.criztovyl.rubinbank.tools.TimeShift;
import me.criztovyl.rubinbank.tools.Tools;
import me.criztovyl.rubinbank.tools.TriggerButton;
import me.criztovyl.rubinbank.tools.TriggerButtonType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


public class RubinBank extends JavaPlugin{
	public static Logger log;
	public static Logger console;
	private static boolean useWorldGuard;
	private Date date_start;
	private Date date_stop;
	private static Bank bank;
	private static Connection con;
	private static boolean conSuccess;
	private boolean failure = false;
	public void onEnable(){
		if(Config.enable()){
			//Get the Plugin Logger ("[RubinBank]...")
			log = Bukkit.getPluginManager().getPlugin("RubinBank").getLogger();
			//Get Bukkit Server Logger
			console = Bukkit.getServer().getLogger();
			//Set up the MySQL Object
			log.info("RubinBank enabeling...");
			//Register Listeners
			Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
			//Plugin start date
			date_start = new Date();
			//write the default Configuration if not exists and is set in the Configuration
			this.saveDefaultConfig();
			try{
				//Set Up Connection
				con = DriverManager.getConnection("jdbc:mysql://" + Config.HostAddress(), Config.HostUser(), Config.HostPassword());
				//Creates Accounts Table if not exists
				if(!Config.isSet("MySQL.Host.Table_Accounts")){
					log.severe("Accounts Table is not set in the config.yml");
					failure = true;
				}
				//Creates Bankomats Table if not exists and is set in the Configuration
				if(Config.isSet("MySQL.Host.Table_Bankomats"))
					con.createStatement().executeUpdate("create table if not exists " + Config.BankomatsTable() + " (id int not null auto_increment primary key, LocationX int, LocationY int, LocationZ int, " +
							"LocationWorld varchar(50), Pos varchar(8), Multi boolean default true, Type varchar(8), Location varchar(50))");
				else{
					log.severe("Bankomats Table is not set in the config.yml");
					failure = true;
				}
				//Creates the Trigger Buttons Table if not exists and is set in the Configuration
				if(Config.isSet("MySQL.Host.Table_Buttons"))
					con.createStatement().executeUpdate("create table if not exists " + Config.ButtonsTable() + " (id int not null auto_increment primary key, LocationX int, LocationY int, LocationZ int, " +
							"LocationWorld varchar(50), Type varchar(20))");
				else{
					log.severe("Buttons Table is not set in the config.yml");
					failure = true;
				}
				//Creates the Statements Table if not exists and is set in the Configuration
				if(!Config.isSet("MySQL.Host.Table_Statements")){
					log.severe("Statements Table is not set in the Config!");
					failure = true;
				}
				con.close();
			} catch(SQLException e){
				log.severe(e.toString() + "\n @ Initial MySQL Connect");
				failure = true;
			}
			//Find out if WorldGuard is present
			if(Config.useWorldGuard()){
				if(Bukkit.getPluginManager().getPlugin("WorldGuard") == null){
					log.warning("RubinBank WorldGuard Implementation disabled: No WorldGuard found.");
					useWorldGuard = false;
					
				}
				else{
					if(Bukkit.getPluginManager().getPlugin("WorldGuard").isEnabled()){
						log.info("RubinBank WorldGuard Implementation enabled!");
						useWorldGuard = true;
					}
					else{
						log.info("RubinBank WorldGuard Implementation disabled: WorldGuard currently not enabled: waiting to enable...");
						useWorldGuard = false;
					}
				}
			}
			else{
				useWorldGuard = false;
			}
			log.info("RubinBank enabled.");
			if(!failure){
				bank = new Bank();
				bank.load();
				//Load the Bankomat Signs to ClicklessSigns
				MySQL.updateTriggers();
				//Load the Trigger Buttons
				MySQL.updateTriggerButtons();
			}
			else{
				log.severe("There was errors while enabeling. Please Check! RubinBank will be disabled...");
				Bukkit.getServer();
				Bukkit.getServer().broadcast("[RubinBank] Während der Aktivierung sind Fehler aufgetreten! RubinBank wird wieder deaktiviert...", Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
				disable();
			}
		}
		else{
			log.info("RubinBank disabled by option from config.");
			Bukkit.getPluginManager().getPlugin("RubinBank").getPluginLoader().disablePlugin((Plugin) this);
		}

	}
	private void disable(){
		Bukkit.getPluginManager().getPlugin("RubinBank").getPluginLoader().disablePlugin((Plugin) this);
	}
	public void onDisable(){
		//Kick all TimeShifted Players out of TimeShift.
		TimeShift.reset();
		if(!failure){
			//Save Bank, Accounts and AccountStatements
			bank.save();
		}
		//Plugin stop date.
		date_stop = new Date();
		String Time;
		long time = date_stop.getTime() - date_start.getTime();
		Time = time + "ms";
		log.info("RubinBank disabeling after " + Time + "...");
		log.info("RubinBank disabled.");
	}
	/**
	 * Sends a Player a message by his name
	 * @param p_n
	 * @param msg
	 */
	private static void msg(String p_n, String msg){
		Tools.msg(p_n, msg);
	}
	/**
	 * Bukkit Stuff
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			//The Player who send the Command
			Player player = (Player) sender;
			// The Player name of him/her
			String p_n = sender.getName();
			//The RubinBank native Command
			if(cmd.getName().equalsIgnoreCase("rubinbank")){
				//checks if the Player has the Permission to use the Command.
				if(player.hasPermission("RubinBank.cmd")){
					if(args.length >= 1){
						//List 
						if(args[0].equals("ids")){
							msg(p_n, "Major: " + Material.getMaterial(Config.getMajorID()));
							msg(p_n, "Minor: " + Material.getMaterial(Config.getMinorID()));
							return true;
						}
						if(args[0].contains("help")){
							msg(p_n, "/rb ids - Major und Minor");
							//Sends the help only if player has Permissions for TriggerButton(s)
							if(player.hasPermission("RubinBank.TriggerButton"))
								msg(p_n, "/rb triggerbutton - alles für den RubinBank TriggerButton (alias: /rb tb)");
							return true;
						}
						if(args[0].equals("triggerbutton") || args[0].equals("tb")){

						}
						if(args[0].equals("ut")){
							MySQL.updateTriggers();
							return true;
						}
						if(args[0].toLowerCase().equals("haveaccount")){
							msg(p_n, ": " + bank.hasAccount(player.getName()));
						}
						if(args[0].toLowerCase().equals("accounts")){
							ArrayList<Account> accs = bank.getAccounts();
							for(int i = 0; i < accs.size(); i++){
								msg(p_n, ChatColor.ITALIC + accs.get(i).getOwner() + ": "
										+ accs.get(i).getBalance());
							}
						}
					}
				}//RubinBank CMD Permission END
			}//RubinBank CMD END
			if(cmd.getName().equalsIgnoreCase("TriggerButton")){
				Block b = player.getTargetBlock(null, 20);
				if(args.length > 0){
					if(args.length >= 1){
						if(args.length >= 2){
							if(args[0].toLowerCase().equals("create")){
								if(args[1].toLowerCase().equals("amount") || args[2].toLowerCase().equals("create")){
									if(b.getType().equals(Material.STONE_BUTTON) || b.getType().equals(Material.WOOD_BUTTON)){
										TriggerButton.addTriggerButton(b.getLocation(), TriggerButtonType.valueOf(args[1].toUpperCase()));
										msg(p_n, ChatColor.DARK_AQUA + "Erstellt.");
										return true;
									}
									else{
										msg(p_n, "Das ist kein Button!");
									}
								}
								else{
									msg(p_n, "Ungültiger Typ!");
									msg(p_n, "Typen: 'create' und 'amount'");
								}
							}
						}
						if(args[0].toLowerCase().equals("list") || args[0].toLowerCase().equals("ls")){
							msg(p_n, "LocationX, LocationY, LocationZ from ArrayList");
							for(int i = 0; i < TriggerButton.triggerbuttons.size(); i++){
									Location loc = TriggerButton.triggerbuttons.get(i);
									msg(p_n, loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
							}
						return true;
						}
					}
				}
				else{
					msg(p_n, "RubinBank TriggerButtons: Erstelle und Verwalte TriggerButtons:");
					msg(p_n, "list(alias: ls)");
					msg(p_n, "create [type]: erstelle einen Button mit Typ 'create'(Konto erstellen) oder 'amount'(Kontostand).");
				}
			}
			if(cmd.getName().equalsIgnoreCase("error")){
				msg(p_n, "Du hast das Error Command ausgeführt...\n\u00A2");
				msg(p_n, "WHY DO YOU DO THIS? Arghhhh...");
				player.kickPlayer(ChatColor.RED + "Server is crashing...");
				return false;
			}
			if(cmd.getName().equalsIgnoreCase("account")){
				if(args.length == 0){
					if(bank.hasAccount(p_n))
						bank.getAccount(p_n).sendBalanceMessage();
					else
						msg(p_n, "Du hast kein Konto.");
					return true;
				}
				if(args.length >= 1){
					if(args[0].equals("create")){
						if(Config.limitedToRegion().contains("create") || Config.limitedToRegion().contains("all")){
							if(inWorldGuardRegion(player)){
								bank.createAccount(p_n);
								return true;
							}
							else{
								msg(p_n, "Du kannst ein Konto nur in einer Bankfiliale eröffnen.");
								return true;
							}
						}
						else{
							bank.createAccount(p_n);
							return true;
						}
					}
					if(args[0].equals("amount")){
						if(Config.limitedToRegion().contains("amount") || Config.limitedToRegion().contains("all")){
							if(inWorldGuardRegion(player)){
								if(bank.hasAccount(p_n)){
									bank.getAccount(p_n).sendBalanceMessage();
								}
								return true;
							}
							else{
								msg(p_n, "Du kannst deinen Kontostand nur in einer Bankfiliale abfragen.");
								return true;
							}
						}
						else{
							if(bank.hasAccount(p_n)){
								bank.getAccount(p_n).sendBalanceMessage();
							}
						}
					}
					if(args.length >= 2){
						if(args[0].equals("payin")){
							if(Config.limitedToRegion().contains("payin") || Config.limitedToRegion().contains("all")){
								if(inWorldGuardRegion(player)){
									try{
										if(bank.hasAccount(p_n)){
											bank.getAccount(p_n).payInViaInv(Double.parseDouble(args[1]));
										}
									} catch(NumberFormatException e){
										Tools.msg(p_n, ChatColor.RED + "[amount] sollte eine Zahl sein! (10,1 => 10.1)");
									}
								}
								else{
									msg(p_n, "Du kannst nur in einer Bankfiliale einzahlen");
								}
							}
							else{
								try{
									if(bank.hasAccount(p_n)){
										bank.getAccount(p_n).payInViaInv(Double.parseDouble(args[1]));
									}
								} catch(NumberFormatException e){
									Tools.msg(p_n, ChatColor.RED + "[amount] sollte eine Zahl sein! (10,1 => 10.1)");
								}
							}
						}
						if(args[0].equals("payout")){
							if(Config.limitedToRegion().contains("payout") || Config.limitedToRegion().contains("all")){
								if(inWorldGuardRegion(player)){
									try{
										if(bank.hasAccount(p_n)){
											bank.getAccount(p_n).payOutViaInv(Double.parseDouble(args[1]));
										}
									} catch(NumberFormatException e){
										Tools.msg(p_n, ChatColor.RED + "[amount] sollte eine Zahl sein! (10,1 => 10.1)");
									}
								}
								else{
									msg(p_n, "Du kannst nur in einer Bankfiliale Geld abheben.");
								}
							}
							else{
								try{
									if(bank.hasAccount(p_n)){
										bank.getAccount(p_n).payOutViaInv(Double.parseDouble(args[1]));
									}
								} catch(NumberFormatException e){
									Tools.msg(p_n, ChatColor.RED + "[amount] sollte eine Zahl sein! (10,1 => 10.1)");
								}
							}
						}
					}
					if(args[0].equals("help")){
						msg(p_n, "/account create:          Erstelle ein Konto.");
						msg(p_n, "/account amount get:      Frage deinen Kontostand ab.");
						msg(p_n, "/account payin  [amount]: Zahle auf dein Konto ein.");
						msg(p_n, "/account payout [amount]: Hebe von deinem Konto ab.");
					}
				}
				else{
					msg(p_n, "/account create:          Erstelle ein Konto.");
					msg(p_n, "/account amount get:      Frage deinen Kontostand ab.");
					msg(p_n, "/account payin  [amount]: Zahle auf dein Konto ein.");
					msg(p_n, "/account payout [amount]: Hebe von deinem Konto ab.");
				}
			return true;
			}//Account CMD END
		}//PLAYER END
		else{
			if(cmd.getName().equalsIgnoreCase("rubinbank")){
				if(args.length >= 1){
					if(args[0].toLowerCase().equals("accounts")){
						ArrayList<Account> accs = bank.getAccounts();
						for(int i = 0; i < accs.size(); i++){
							log.info(accs.get(i).getOwner() + ": "
									+ accs.get(i).getBalance());
						}
					}
				}
				log.info("oO you have found it...");
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("error")){
				log.severe("You performed the Error command...");
				return false;
			}
			log.info("Only a Player can perform this command!");
			return true;
		}//CONSOLE END
		return false;
	}
	/**
	 * Get Boolean if WorldGuard is present and/or should be used.
	 * @return Boolean if WorldGuard is usable and should used
	 */
	public static boolean getUseWorldGuard(){
		return useWorldGuard;
	}
	/**
	 * Set the Boolean if WorldGuard is present and/or should be used to true.
	 */
	public static void setUseWorldGuard(){
		if(Config.useWorldGuard()){
			useWorldGuard = true;
		}
	}
	/**
	 * Checks if a player is in a WorldGuard Region with the parent defined in the Config.
	 * @param player
	 * @return if the Players is inside such a region true; any other false
	 */
	public static boolean inWorldGuardRegion(Player player){
		if(Bukkit.getPluginManager().getPlugin("WorldGuard").isEnabled()){
			WorldGuardPlugin wgp = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
			ApplicableRegionSet regions = wgp.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
			for(Iterator<ProtectedRegion> i = regions.iterator(); i.hasNext();){
				ProtectedRegion region = i.next();
				if(region.getParent() != null){
					if(region.getParent().getId().equals(Config.getRegion().toLowerCase())){
						return true;
					}
				}
				
			}
			return false;
		}
		else{
			return false;
		}
	}
	public static Connection getCon() {
		try {
			if(con.isClosed()){
				reopenCon();
			}
			conSuccess = true;
			return con;
		} catch (SQLException e) {
			log.severe(e.toString() + "\n @ RubinBank.getCon()");
			conSuccess = false;
			return null;
		}
	}
	public static boolean conWasSuccess(){
		return conSuccess;
	}
	public static void reopenCon(){
		try {
			if(con.isClosed()){
				con = DriverManager.getConnection("jdbc:mysql://" + Config.HostAddress(), Config.HostUser(), Config.HostPassword());
			}
			else{
				log.info("Connection is not closed.");
			}
			conSuccess = true;
		} catch (SQLException e) {
			log.severe(e.toString() + "\n @ RubinBank.getCon()");
			conSuccess = false;
		}
	}
	public static Bank getBank(){
		return bank;
	}
}

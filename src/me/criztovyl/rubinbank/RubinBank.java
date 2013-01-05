package me.criztovyl.rubinbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import me.criztovyl.clicklesssigns.ClicklessSigns;
import me.criztovyl.rubinbank.account.Account;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.listeners.Listeners;
import me.criztovyl.rubinbank.tools.MySQL;
import me.criztovyl.rubinbank.tools.TimeShift;
import me.criztovyl.rubinbank.tools.TriggerButton;
import me.criztovyl.rubinbank.tools.TriggerButtonType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


public class RubinBank extends JavaPlugin{
	private static String url;
	public static Logger log;
	public static Logger console;
	private static boolean useWorldGuard;
	private Date date_start;
	private Date date_stop;
	private static Connection con;
	public void onEnable(){
		if(Config.enable()){
			log = Bukkit.getPluginManager().getPlugin("RubinBank").getLogger();
			console = Bukkit.getServer().getLogger();
			log.info("RubinBank enabeling...");
			Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
			date_start = new Date();
			this.saveDefaultConfig();
			url = "jdbc:mysql://" + Config.HostAddress() + "/" + Config.HostDatabase() + "?user=" + Config.HostUser() + "&password=" + Config.HostPassword();
			try{
				con = DriverManager.getConnection(url);
				
				Statement stmt = con.createStatement();
				
				if(Config.isSet("MySQL.Host.Table_Accounts"))
				stmt.executeUpdate("create table if not exists " + Config.AccountsTable() + " (id int not null auto_increment primary key, user varchar(50) not null, account boolean default 0, amount double)");
				else
					log.warning("Accounts Table is not set in the config");
				if(Config.isSet("MySQL.Host.Table_Bankomats"))
				stmt.executeUpdate("create table if not exists " + Config.BankomatsTable() + " (id int not null auto_increment primary key, LocationX int, LocationY int, LocationZ int, LocationWorld varchar(50), Pos varchar(8), Multi boolean default true, Type varchar(8), Location varchar(50))");
				else
					log.warning("Bankomats Table is not set in the config");
				if(Config.isSet("MySQL.Host.Table_Buttons"))
				stmt.executeUpdate("create table if not exists " + Config.ButtonsTable() + " (id int not null auto_increment primary key, LocationX int, LocationY int, LocationZ int, LocationWorld varchar(50), Type varchar(20))");
				else
					log.warning("(Trigger-)Buttons Table is not set in the config");
				if(Config.isSet("MySQL.Host.Table_Statements"))
					stmt.executeUpdate("create table if not exists " + Config.ActionsTable() + " (id int not null auto_increment primary key, user varchar(50) not null, Action varchar(20) not null, user2 varchar(50), ActionAmount double, newAmount double, Date date)");
				else
					log.warning("StatementsTable is not set in the config");
			} catch(SQLException e){
				log.severe("MySQL Exception:\n" + e.toString() + "\nAt: RubinBank create MySQL Tables");
			}
			ClicklessSigns.updateClicklessSignsTriggers();
			MySQL.updateTriggerButtons();
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
		}
		else{
			log.info("RubinBank disabled by option from config.");
			Bukkit.getPluginManager().getPlugin("RubinBank").getPluginLoader().disablePlugin(this);
		}

	}
	public void onDisable(){
		TimeShift.reset();
		date_stop = new Date();
		String Time;
		long time = date_stop.getTime() - date_start.getTime();
		Time = time + "ms";
		log.info("RubinBank disabeling after " + Time + "...");
		log.info("RubinBank disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("rubinbank")){
				if(player.hasPermission("RubinBank.cmd")){
					if(args.length >= 1){
						if(args[0].equals("ids")){
							int major = Config.getMajorID();
							int minor = Config.getMinorID();
							Material majormaterial = Material.getMaterial(major);
							Material minormaterial = Material.getMaterial(minor);
							player.sendMessage("Major: " + major + " ("+majormaterial.name() + ")");
							player.sendMessage("Minor: " + minor + " (" + minormaterial.name() + ")");
							return true;
						}
						if(args[0].contains("help")){
							player.sendMessage("/rb ids - Alle Wichtigen IDs die für RubinBank wichtig sind(e.g. Major oder Minor)");
							if(player.hasPermission("RubinBank.TriggerButton"))
								player.sendMessage("/rb triggerbutton - alles für den RubinBank TriggerButton");
							return true;
						}
						if(args[0].equals("triggerbutton") || args[0].equals("tb")){
							if(player.hasPermission("RubinBank.TriggerButton")){
								Block b = player.getTargetBlock(null, 20);
								if(args.length == 2){
									if(args[1].toLowerCase().equals("list") || args[1].toLowerCase().equals("ls")){
										player.sendMessage("LocationX, LocationY, LocationZ from ArrayList");
										for(int i = 0; i < TriggerButton.triggerbuttons.size(); i++){
											Location loc = TriggerButton.triggerbuttons.get(i);
											player.sendMessage(loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
										}
										return true;
									}
								}
								if(b.getType().equals(Material.STONE_BUTTON) || b.getType().equals(Material.WOOD_BUTTON)){
									if(args.length == 2){
										if(args[1].toUpperCase().equals("AMOUNT") || args[1].toUpperCase().equals("CREATE")){
											TriggerButton.addTriggerButton(b.getLocation(), TriggerButtonType.valueOf(args[1].toUpperCase()));
											player.sendMessage(ChatColor.DARK_AQUA + "Erstellt.");
											return true;
										}
										else{
											player.sendMessage(ChatColor.YELLOW + "Argument 2 \"" + args[1] + "\" ist ungültig");
											player.sendMessage(ChatColor.YELLOW + "Argumente: amount  oder create");
											return true;
										}
									}
									else{
										player.sendMessage(ChatColor.YELLOW + "Argument fehlt!");
										player.sendMessage(ChatColor.YELLOW + "Argumente: amount oder create");
										return true;
									}
								}
								else{
									player.sendMessage(ChatColor.YELLOW + "Das ist kein Button.");
									return true;
								}
							}
						}
						if(args[0].equals("ut")){
							MySQL.updateTriggers();
							return true;
						}
						if(args[0].toLowerCase().equals("haveaccount")){
							player.sendMessage(": " + Account.hasAccount(player.getName()));
						}
					}
				}//RubinBank CMD Permission END
			}//RubinBank CMD END
			if(cmd.getName().equalsIgnoreCase("error")){
				player.sendMessage("Du hast das Error Command ausgeführt...\n\u00A2");
				player.sendMessage("WHY DO YOU DO THIS? Arghhhh...");
				player.kickPlayer(ChatColor.RED + "Server is crashing...");
				return false;
			}
			if(cmd.getName().equalsIgnoreCase("account")){
				if(args.length == 0){
					if(Account.hasAccount(player.getName()))
						player.sendMessage("Dein Kontostand beträgt" + Double.toString(Account.getAccountAmount(player.getName())));
					else
						player.sendMessage("Du hast kein Konto.");
					return true;
				}
				if(args.length >= 1){
					if(args[0].equals("create")){
						if(Config.limitedToRegion().equals("create") || Config.limitedToRegion().equals("all")){
							if(inWorldGuardRegion(player)){
								Account.createAccount(player.getName());
								return true;
							}
							else{
								player.sendMessage("Du kannst ein Konto nur in einer Bankfiliale eröffnen.");
								return true;
							}
						}
						else{
							Account.createAccount(player.getName());
							return true;
						}
					}
					if(args[0].equals("amount")){
						if(Config.limitedToRegion().equals("amount") || Config.limitedToRegion().equals("all")){
							if(inWorldGuardRegion(player)){
								Account.amountMsg(player.getName());
								return true;
							}
							else{
								player.sendMessage("Du kannst deinen Kontostand nur in einer Bankfiliale abfragen.");
								return true;
							}
						}
						else{
							Account.amountMsg(player.getName());
							return true;
						}
					}
					if(args.length >= 2){
						if(args[0].equals("payin")){
							if(Config.limitedToRegion().equals("payin") || Config.limitedToRegion().equals("all")){
								if(inWorldGuardRegion(player)){
									double i;
									try{
										i = Double.parseDouble(args[1]);
									} catch(NumberFormatException e){
										player.sendMessage(ChatColor.RED + "'amount' muss eine Zahl sein!");
										player.sendMessage("/account payin amount");
										return true;
									}
									if(Account.payinToAccount(player.getName(), i))
										player.sendMessage(ChatColor.DARK_AQUA + "Zahlung erfolgt.");
									else
										player.sendMessage(ChatColor.RED + "Zahlung nicht erfolgreich.");
									return true;
								}
								else{
									player.sendMessage("Du kannst nur in einer Bankfiliale einzahlen");
								}
							}
							else{
								double i;
								try{
									i = Double.parseDouble(args[1]);
								} catch(NumberFormatException e){
									player.sendMessage(ChatColor.RED + "'amount' muss eine Zahl sein!");
									player.sendMessage("/account payin amount");
									return true;
								}
								if(Account.payinToAccount(player.getName(), i))
									player.sendMessage(ChatColor.DARK_AQUA + "Zahlung erfolgt.");
								else
									player.sendMessage(ChatColor.RED + "Zahlung nicht erfolgreich");
								return true;
							}
						}
						if(args[0].equals("payout")){
							if(Config.limitedToRegion().equals("payout") || Config.limitedToRegion().equals("all")){
								if(inWorldGuardRegion(player)){
									double  i;
									try{
										i = Double.parseDouble(args[1]);
									} catch(NumberFormatException e){
										player.sendMessage(ChatColor.RED + "'amount' muss eine Zahl sein!");
										player.sendMessage("/account payout amount");
										return true;
									}
									if(Account.payoutFromAccount(player.getName(), i)){
										player.sendMessage("PayOut " + i + "...");
										player.sendMessage(ChatColor.DARK_AQUA + "Auszahlung erfolg.");
										return true;
									}
									else{
										player.sendMessage(ChatColor.RED + "Auszahlung fehlgeschlagen!");
										return true;
									}
								}
								else{
									player.sendMessage("Du kannst nur in einer Bankfiliale Geld abheben.");
								}
							}
							else{
								double  i;
								try{
									i = Double.parseDouble(args[1]);
								} catch(NumberFormatException e){
									player.sendMessage(ChatColor.RED + "'amount' muss eine Zahl sein!");
									player.sendMessage("/account payout amount");
									return true;
								}
								if(Account.payoutFromAccount(player.getName(), i)){
									player.sendMessage("PayOut " + i + "...");
									int major = (int) i;
									int minor = (int) ((i - major) * 10);
									player.getInventory().addItem(new ItemStack(Config.getMajorID(), major));
									if(i > 0)
											player.getInventory().addItem(new ItemStack(Config.getMinorID(), minor));
									player.sendMessage(ChatColor.DARK_AQUA + "Auszahlung erfolg.");
									return true;
								}
								else{
									player.sendMessage(ChatColor.RED + "Auszahlung fehlgeschlagen!");
									return true;
								}
							}
						}
					}
					if(args[0].equals("help")){
						player.sendMessage("/account create:          Erstelle ein Konto.");
						player.sendMessage("/account amount get:      Frage deinen Kontostand ab.");
						player.sendMessage("/account payin  [amount]: Zahle auf dein Konto ein.");
						player.sendMessage("/account payout [amount]: Hebe von deinem Konto ab.");
						return true;
					}
				}
				else{
					player.sendMessage("Zu wenig Argumente");
				}
			}
		}//PLAYER END
		else{
			if(cmd.getName().equalsIgnoreCase("rubinbank")){
				log.info("oO you have found it...");
				//if(args.length > 0)
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
	public static Connection getConnection(){
		try {
			con = DriverManager.getConnection(url);
		} catch (SQLException e) {
			log.severe("MySQL Exception:\n" + e.toString() + "\nAt RubinBank.getConnection");
		}
		return con;
	}
	public static boolean getUseWorldGuard(){
		return useWorldGuard;
	}
	public static void setUseWorldGuard(){
		if(Config.useWorldGuard()){
			useWorldGuard = true;
		}
	}
	public static boolean inWorldGuardRegion(Player player){
		if(Bukkit.getPluginManager().getPlugin("WorldGuard").isEnabled()){
			WorldGuardPlugin wgp = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
			ApplicableRegionSet regions = wgp.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
			for(Iterator<ProtectedRegion> i = regions.iterator(); i.hasNext();){
				ProtectedRegion region = i.next();
				if(region.getParent().equals(Config.getRegion())){
					return true;
				}
			}
			return false;
		}
		else{
			return false;
		}
	}
}

package me.criztovyl.rubinbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.criztovyl.rubinbank.account.account;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.listeners.listeners;
import me.criztovyl.rubinbank.tools.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
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
	private Date date1;
	private Date date2;
	private static Connection con;
	public void onEnable(){
		if(Config.enable()){
			log = this.getLogger();
			console = Bukkit.getLogger();
			console.info("RubinBank enabeling...");
			Bukkit.getServer().getPluginManager().registerEvents(new listeners(), this);
			date1 = new Date();
			this.reloadConfig();
			url = "jdbc:mysql://"+Config.HostAddress()+"/"+Config.HostDatabase()+"?user="+Config.HostUser()+"&password="+Config.HostPassword();
			try{
				Class.forName("com.mysql.jdbc.Driver");
			} catch(ClassNotFoundException e){
				log.severe("MySQL Driver Class not found!\nDisabeling...");
				Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getPluginLoader().disablePlugin(this);
			}
			try{
				con = DriverManager.getConnection(url);
				
				Statement stmt = con.createStatement();
				
				stmt.executeUpdate("create table if not exists "+Config.DataBaseAndTable()+" (id int not null auto_increment, user varchar(50) not null, amount double, lastlogin date)");
			} catch(SQLException e){
				log.severe("MySQL Exception:\n"+e.toString());
			}
			if(Config.useWorldGuard()){
				if(Bukkit.getPluginManager().getPlugin("WorldGuard").equals(null)){
					log.warning("RubinBank WorldGuard Implementation disabled: No WorldGuard found.");
					useWorldGuard = false;
					
				}
				else{
					if(Bukkit.getPluginManager().getPlugin("WorldGuard").isEnabled()){
						log.info("RubinBank WorldGuard Implementation enabled!");
						useWorldGuard = true;
					}
					else{
						log.warning("RubinBank WorldGuard Implementation disabled: WorldGuard not enabled.");
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
		date2 = new Date();
		String Time;
		long time = date2.getTime() - date1.getTime();
		Time = time+"ms";
		log.info("RubinBank disabeling after "+Time+"...");
		log.info("RubinBank disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("rubinbank")){
				if(player.hasPermission("RubinBank.dummy"))
				if(args.length >= 1){
					if(args[0].equals("ids")){
						int major = Config.getMajorID();
						int minor = Config.getMinorID();
						int gunpowder = 289;
						Material majormaterial = Material.getMaterial(major);
						Material minormaterial = Material.getMaterial(minor);
						Material gunpowdermaterial = Material.getMaterial(gunpowder);
						player.sendMessage("Major: "+major+" ("+majormaterial.name()+")");
						player.sendMessage("Minor: "+minor+" ("+minormaterial.name()+")");
						player.sendMessage("Create Explosion: "+gunpowder+" ("+gunpowdermaterial.name()+")");
						return true;
					}
					if(args[0].equals("inv")){
						if(args[1].equals("nodirt")){
							ItemStack items;
							if(args.length == 3)
								try{
									items = new ItemStack(Material.DIRT, Integer.parseInt(args[2]));
								} catch(NumberFormatException e){
									player.sendMessage("'amount' muss eine Zahl sein!\n" +
											ChatColor.RED + "/rubinbank inv nodirt amount");
									return true;
								}
							else
								items = new ItemStack(Material.DIRT);
							player.getInventory().removeItem(items);
							player.sendMessage("Dirt entfernt ;)");
							return true;
						}
						if(args[1].equals("clear")){
							player.getInventory().clear();
							player.sendMessage("Inventar gelöscht.");
							return true;
						}
					}
					if(args[0].contains("day")){
						player.getWorld().setTime(800);
						return true;
					}
					if(args[0].contains("rain")){
						player.getWorld().setStorm(!player.getWorld().hasStorm());
						return true;
					}
					if(args[0].contains("help")){
						player.sendMessage("/rubinbank ids - Alle Wichtigen IDs die für RubinBank wichtig sind(e.g. Major oder Minor)");
						player.sendMessage("/rubinbank inv");
						player.sendMessage("/rubinbank inv nodirt [X] - Entferne allen oder X Dirt(Blöcke) aus deinem Inventar.");
						player.sendMessage("/rubinbank inv clear - Inventar leeren");
						player.sendMessage("/rubinbank day - Tag machen ;) (0800h)");
						player.sendMessage("/rubinbank rain - Stoppt Rain (oderr Sturm). Alias /rain.");
						player.sendMessage("/rubinbank regiontest - Testet ob du in der RubinBank Region bist.");
						return true;
					}
					if(args[0].contains("regiontest")){
						if(useWorldGuard){
							if(inWorldGuardRegion(player)){
								player.sendMessage("UIIIII :D");
							}
							else{
								player.sendMessage("Nicht in der RubinBank Region");
							}
						}
						else{
							player.sendMessage(":( kein WorldGuard geladen...oder abgeschaltet...");
							return true;
						}
					}
				}
			}//RubinBank CMD END
			if(cmd.getName().equalsIgnoreCase("error")){
				player.sendMessage("Du hast das Error Command ausgeführt...\n\u00A2");
				return false;
			}
			if(cmd.getName().equalsIgnoreCase("yaw")){
				player.sendMessage("Your Yaw: "+player.getLocation().getYaw());
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("mysql")){
				try{
					con = DriverManager.getConnection(url);
					
					Statement stmt = con.createStatement();
					
					ResultSet resultset;
					
					if(args.length == 0){
						resultset = stmt.executeQuery("describe "+Config.HostDatabase()+"."+Config.HostTable());
					}
					else{
						if(args.length > 2){
							if(args[0].equals("update")){
								String query = "";
								log.info(Integer.toString(args.length));
								for(int i = 1; i < args.length; i++){
									query += " " + args[i];
								}
								log.info(query);
								
								stmt.executeUpdate(query);
								return true;
							}
						}
						String query = "";
						log.info(Integer.toString(args.length));
						for(int i = 0; i < args.length; i++){
							query += " " + args[i];
						}
						log.info(query);
						
						resultset = stmt.executeQuery(query);
					}
					resultset.last();
					int rowcount = resultset.getRow();
					resultset.beforeFirst();
					int columns = resultset.getMetaData().getColumnCount();
					String results[][] = new String[rowcount+1][columns];
					for(int k = 1; k <= columns; k++){
						results[0][k-1] = resultset.getMetaData().getColumnName(k);
					}
					for(int i = 1; resultset.next(); i++){
						for(int j = 1; j <= columns; j++){
							results[i][j-1] = resultset.getString(j);
						}
					}
					for(int i = 0; i < results.length; i++){
						String out = "";
						for(int j = 0; j < results[i].length; j++){
							out += results[i][j]+ " ";
						}
						player.sendMessage(out);
					}
					return true;
			} catch(SQLException e){
				log.severe("MySQL exception:\n" + e.toString());
				player.sendMessage(ChatColor.RED+"MySQL Error.!");
				return true;
			}
		}
			if(cmd.getName().equalsIgnoreCase("account")){
				if(args.length == 0){
					player.sendMessage("Dein Kontostand beträgt" + Double.toString(account.getAccountAmount(player)));
					return true;
				}
				if(args.length >= 1){
					if(args[0].equals("create")){
						if(Config.limitedToRegion().equals("create") || Config.limitedToRegion().equals("all")){
							if(inWorldGuardRegion(player)){
								if(account.createAccount(player)){
									player.sendMessage("Konto eröffnet.");
									return true;
								}
								else{
									player.sendMessage("Du hast bereits ein Konto.");
									return true;
								}
							}
							else{
								player.sendMessage("Du kannst ein Konto nur in einer Bankfiliale eröffnen.");
								return true;
							}
						}
						else{
							if(account.createAccount(player)){
								player.sendMessage("Konto eröffnet.");
								return true;
							}
							else{
								player.sendMessage("Du hast bereits ein Konto.");
								return true;
							}
						}
					}
					if(args[0].equals("amount")){
						if(Config.limitedToRegion().equals("amount") || Config.limitedToRegion().equals("all")){
							if(inWorldGuardRegion(player)){
								double amount = account.getAccountAmount(player);
								if(amount >= 0){
									player.sendMessage("Dein Kontostand betr\u00E4gt "+account.getAccountAmount(player)+".");
								}
								else{
									player.sendMessage("Du hast noch kein Konto.");
								}
								return true;
							}
							else{
								player.sendMessage("Du kannst deinen Kontostand nur in einer Bankfiliale abfragen.");
								return true;
							}
						}
						else{
							double amount = account.getAccountAmount(player);
							if(amount >= 0){
								player.sendMessage("Dein Kontostand betr\u00E4gt "+account.getAccountAmount(player)+".");
							}
							else{
								player.sendMessage("Du hast noch kein Konto.");
							}
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
									if(account.payinToAccount(player, i))
										player.sendMessage(ChatColor.GREEN + "Zahlung erfolgt.");
									else
										player.sendMessage(ChatColor.RED + "Zahlung nicht erfolgreich :/");
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
								if(account.payinToAccount(player, i))
									player.sendMessage(ChatColor.GREEN + "Zahlung erfolgt.");
								else
									player.sendMessage(ChatColor.RED + "Zahlung nicht erfolgreich :/");
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
									if(account.payoutFromAccount(player, i)){
										player.sendMessage("PayOut "+i+"...");
										int major = (int) i;
										int minor = (int) ((i - major) * 10);
										player.getInventory().addItem(new ItemStack(Config.getMajorID(), major));
										if(i > 0)
												player.getInventory().addItem(new ItemStack(Config.getMinorID(), minor));
										player.sendMessage(ChatColor.GREEN + "Auszahlung erfolg.");
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
								if(account.payoutFromAccount(player, i)){
									player.sendMessage("PayOut "+i+"...");
									int major = (int) i;
									int minor = (int) ((i - major) * 10);
									player.getInventory().addItem(new ItemStack(Config.getMajorID(), major));
									if(i > 0)
											player.getInventory().addItem(new ItemStack(Config.getMinorID(), minor));
									player.sendMessage(ChatColor.GREEN + "Auszahlung erfolg.");
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
			if(cmd.getName().equalsIgnoreCase("rain")){
				World world = player.getWorld();
				world.setStorm(!world.hasStorm());
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("lastlog")){
				if(player.hasPermission("RubinBank.lastlog.all")){
					String[] lastlogs = MySQL.getLastLogins();
					if(!lastlogs.equals(null)){
					player.sendMessage(lastlogs);
					return true;
					}
					else{
						player.sendMessage(ChatColor.RED + "Interner Fehler! Versuche es später noch ein mal... #mysql #lastlog");
						return true;
					}
				}
			}
		}//PLAYER END
		else{
			if(cmd.getName().equalsIgnoreCase("rubinbank")){
				Level level = Level.ALL;
				log.log(level, "oO you have found it...");
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("error")){
				log.info("You performed the Error command...");
				return false;
			}
			if(cmd.getName().equalsIgnoreCase("mysql")){
				try{
					con = DriverManager.getConnection(url);
					
					Statement stmt = con.createStatement();
					
					ResultSet resultset;
					
					if(args.length == 0){
						resultset = stmt.executeQuery("describe "+Config.HostDatabase()+"."+Config.HostTable());
					}
					else{
						if(args.length > 2){
							if(args[0].equals("update")){
								String query = "";
								log.info(Integer.toString(args.length));
								for(int i = 1; i < args.length; i++){
									query += " " + args[i];
								}
								log.info(query);
								
								stmt.executeUpdate(query);
								return true;
							}
						}
						String query = "";
						log.info(Integer.toString(args.length));
						for(int i = 0; i < args.length; i++){
							query += " " + args[i];
						}
						log.info(query);
						
						resultset = stmt.executeQuery(query);
					}
					resultset.last();
					int rowcount = resultset.getRow();
					resultset.beforeFirst();
					int columns = resultset.getMetaData().getColumnCount();
					String results[][] = new String[rowcount+1][columns];
					for(int k = 1; k <= columns; k++){
						results[0][k-1] = resultset.getMetaData().getColumnName(k);
					}
					for(int i = 1; resultset.next(); i++){
						for(int j = 1; j <= columns; j++){
							results[i][j-1] = resultset.getString(j);
						}
					}
					for(int i = 0; i < results.length; i++){
						String out = "";
						for(int j = 0; j < results[i].length; j++){
							out += results[i][j]+ " ";
						}
						log.info(out);
					}
					return true;
			} catch(SQLException e){
				log.severe("MySQL exception:\n" + e.toString());
				return true;
			}
		}
			log.info("Only a Player can perform this command!");
			return true;
		}//CONSOLE END
		return false;
	}

	public static String getURL(){
		return url;
	}
	public static boolean isinDB(Player p){
		return MySQL.isInDB(p);
	}
	public static Connection getConnection(){
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

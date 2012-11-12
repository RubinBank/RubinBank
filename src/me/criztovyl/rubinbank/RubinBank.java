package me.criztovyl.rubinbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import me.criztovyl.rubinbank.account.account;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.listeners.listeners;
import me.criztovyl.rubinbank.tools.BankomatTriggers;
import me.criztovyl.rubinbank.tools.BankomatType;
import me.criztovyl.rubinbank.tools.MySQL;
import me.criztovyl.rubinbank.tools.TimeShiftBankomat;
import me.criztovyl.rubinbank.tools.TriggerButton;
import me.criztovyl.rubinbank.tools.TriggerButtonType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
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
	private static ArrayList<Location> triggers;
	public void onEnable(){
		if(Config.enable()){
			log = Bukkit.getPluginManager().getPlugin("RubinBank").getLogger();
			console = Bukkit.getServer().getLogger();
			log.info("RubinBank enabeling...");
			Bukkit.getServer().getPluginManager().registerEvents(new listeners(), this);
			date1 = new Date();
			triggers = new ArrayList<Location>();
			this.saveDefaultConfig();
			url = "jdbc:mysql://" + Config.HostAddress() + "/" + Config.HostDatabase() + "?user=" + Config.HostUser() + "&password=" + Config.HostPassword();
			try{
				Class.forName("com.mysql.jdbc.Driver");
			} catch(ClassNotFoundException e){
				log.severe("MySQL Driver Class not found!");
				Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getPluginLoader().disablePlugin(this);
			}
			try{
				con = DriverManager.getConnection(url);
				
				Statement stmt = con.createStatement();
				
				stmt.executeUpdate("create table if not exists " + Config.DataBaseAndTable() + " (id int not null auto_increment primary key, user varchar(50) not null, amount double, account boolean default 0, lastlogin date)");
				stmt.executeUpdate("create table if not exists " + Config.DataBaseAndTable2() + " (id int not null auto_increment primary key, LocationX int, LocationY int, LocationZ int, LocationWorld varchar(50), Type varchar(20), Pos varchar(8))");
				stmt.executeUpdate("create table if not exists " + Config.DataBaseAndTable3() + " (id int not null auto_increment primary key, LocationX int, LocationY int, LocationZ int, LocationWorld varchar(50), Type varchar(20))");
			} catch(SQLException e){
				log.severe("MySQL Exception:\n"+e.toString());
			}
			updateBankomatLocs();
			MySQL.updateTriggerButtons();
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
						log.info("RubinBank WorldGuard Implementation disabled: WorldGuard not enabled.");
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
		Time = time + "ms";
		log.info("RubinBank disabeling after " + Time + "...");
		log.info("RubinBank disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("rubinbank")){
				if(player.hasPermission("RubinBank"))
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
						player.sendMessage("/rubinbank ids - Alle Wichtigen IDs die für RubinBank wichtig sind(e.g. Major oder Minor)");
						player.sendMessage("/rubinbank inv");
						player.sendMessage("/rubinbank inv nodirt [X] - Entferne allen oder X Dirt(Blöcke) aus deinem Inventar.");
						player.sendMessage("/rubinbank inv clear - Inventar leeren");
						player.sendMessage("/rubinbank day - Tag machen ;) (0800h)");
						player.sendMessage("/rubinbank rain - Stoppt Rain (oder	 Sturm). Alias /rain.");
						player.sendMessage("/rubinbank regiontest - Testet ob du in der RubinBank Region bist.");
						return true;
					}
					if(args[0].equals("regiontest")){
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
					if(args[0].equals("triggers")){
						player.sendMessage("Triggers: BlockX, BlockY, BlockZ");
						for(int i = 0; i < triggers.size(); i++){
							player.sendMessage(triggers.get(i).getBlockX() + ", " + triggers.get(i).getBlockY() + ", " + triggers.get(i).getBlockZ());
						}
						return true;
					}
					if(args[0].equals("triggerbutton") || args[0].equals("tb")){
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
									player.sendMessage(TriggerButtonType.valueOf(args[1].toUpperCase()).toString());
									player.sendMessage(ChatColor.DARK_AQUA + "Erstellt.");
									return true;
								}
								else{
									player.sendMessage(ChatColor.YELLOW + "Argument 2 \"" + args[1] + "\" ist ungültig");
									player.sendMessage(ChatColor.YELLOW + "Argumente: amount oder create");
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
						resultset = stmt.executeQuery("describe " + Config.DataBaseAndTable());
					}
					else{
						if(args.length > 2){
							if(args[0].equals("update")){
								String query = "";
								for(int i = 1; i < args.length; i++){
									query += " " + args[i];
								}
								
								stmt.executeUpdate(query);
								return true;
							}
						}
						String query = "";
						for(int i = 0; i < args.length; i++){
							query += " " + args[i];
						}
						
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
				log.severe("MySQL exception at MySQL Command:\n" + e.toString());
				player.sendMessage(ChatColor.RED+"MySQL Error:\n" + e.toString());
				return true;
			}
		}
			if(cmd.getName().equalsIgnoreCase("account")){
				if(args.length == 0){
					if(account.hasAccount(player))
						player.sendMessage("Dein Kontostand beträgt" + Double.toString(account.getAccountAmount(player)));
					else
						player.sendMessage("Du hast kein Konto.");
					return true;
				}
				if(args.length >= 1){
					if(args[0].equals("create")){
						if(Config.limitedToRegion().equals("create") || Config.limitedToRegion().equals("all")){
							if(inWorldGuardRegion(player)){
								account.createAccount(player);
								return true;
							}
							else{
								player.sendMessage("Du kannst ein Konto nur in einer Bankfiliale eröffnen.");
								return true;
							}
						}
						else{
							account.createAccount(player);
							return true;
						}
					}
					if(args[0].equals("amount")){
						if(Config.limitedToRegion().equals("amount") || Config.limitedToRegion().equals("all")){
							if(inWorldGuardRegion(player)){
								double amount = account.getAccountAmount(player);
								if(amount >= 0){
									player.sendMessage("Dein Kontostand betr\u00E4gt " + account.getAccountAmount(player) + ".");
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
								player.sendMessage("Dein Kontostand betr\u00E4gt " + account.getAccountAmount(player) + ".");
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
										player.sendMessage(ChatColor.DARK_AQUA + "Zahlung erfolgt.");
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
									player.sendMessage(ChatColor.DARK_AQUA + "Zahlung erfolgt.");
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
								if(account.payoutFromAccount(player, i)){
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
						player.sendMessage(ChatColor.RED + "Interner Fehler! Lastlog == null ");
						return true;
					}
				}
			}
		}//PLAYER END
		else{
			if(cmd.getName().equalsIgnoreCase("rubinbank")){
				log.info("oO you have found it...");
				if(args[0].equals("triggers")){
					console.info("Triggers: BlockX, BlockY, BlockZ");
					log.info(Integer.toString(triggers.size()));
					for(int i = 0; i < triggers.size(); i++){
						console.info(triggers.get(i).getBlockX() + ", " + triggers.get(i).getBlockY() + ", " + triggers.get(i).getBlockZ());
					}
					return true;
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("error")){
				log.severe("You performed the Error command...");
				return false;
			}
			if(cmd.getName().equalsIgnoreCase("mysql")){
				try{
					con = DriverManager.getConnection(url);
					
					Statement stmt = con.createStatement();
					
					ResultSet resultset;
					
					if(args.length == 0){
						resultset = stmt.executeQuery("describe " + Config.DataBaseAndTable());
					}
					else{
						if(args.length > 2){
							if(args[0].equals("update")){
								String query = "";
								for(int i = 1; i < args.length; i++){
									query += " " + args[i];
								}
								
								stmt.executeUpdate(query);
								return true;
							}
						}
						String query = "";
						for(int i = 0; i < args.length; i++){
							query += " " + args[i];
						}
						
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
	public static void updateBankomatLocs(){
		MySQL.updateTriggers();
		triggers = BankomatTriggers.getTriggers();
		log.info("Updatet BankomatTriggers ArrayList");
	}
	public static ArrayList<Location> getBankomatTriggers(){
		return triggers;
	}
	public static boolean isInBankomatTriggers(Location loc){
		for(int i = 0; i < triggers.size(); i++){
			Location loc2 = triggers.get(i);
			int bmatX = loc2.getBlockX();
			int bmatY = loc2.getBlockY();
			int bmatZ = loc2.getBlockZ();
			World bmatW = loc2.getWorld(); 
			loc2 = loc;
			int locX = loc2.getBlockX();
			int locY = loc2.getBlockY();
			int locZ = loc2.getBlockZ();
			World locW = loc2.getWorld();
			if(bmatX == locX && bmatY == locY && bmatZ == locZ && bmatW == locW)
				return true;
		}
		return false;
	}
	public static void bankomatPlayerMove(PlayerMoveEvent evt){
		Location locTo = new Location(evt.getTo().getWorld(), evt.getTo().getBlockX(), evt.getTo().getBlockY(), evt.getTo().getBlockZ());
		Location locFrom = new Location(evt.getFrom().getWorld(), evt.getFrom().getBlockX(), evt.getFrom().getBlockY(), evt.getFrom().getBlockZ());
		if(!(locTo.equals(locFrom))){
			if(TimeShiftBankomat.isShifted(evt.getPlayer())){
				if(BankomatTriggers.isTrigger(locFrom)){
					TimeShiftBankomat.removeShifted(evt.getPlayer());
				}
			}
			if(!BankomatTriggers.sameBankomat(locFrom, locTo)){
				if(!triggers.isEmpty()){
					if(RubinBank.isInBankomatTriggers(locTo)){
						evt.getPlayer().sendMessage("Möchtest du Abheben, Einzahlen, Kontostand oder Konto erstellen?");
						TimeShiftBankomat.addShiftedBankomat(evt.getPlayer(), BankomatType.CHOOSING);
					}
				}
			}
		}
	}
}

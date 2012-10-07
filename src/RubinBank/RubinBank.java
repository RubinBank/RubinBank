//TODO Version
package RubinBank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import RubinBank.account.account;
import RubinBank.bankomat.bankomat;
import RubinBank.listeners.listeners;
import RubinBank.tools.MySQL;
import RubinBank.tools.PlayerDetails;
import RubinBank.tools.Temp;
import config.Config;

public class RubinBank extends JavaPlugin{
	private static ArrayList<bankomat> bankomats;
	private static ArrayList<Temp> temp;
	private static ArrayList<PlayerDetails> pd;
	private static String url;
	public static Logger log = Bukkit.getLogger();
	private Date date1;
	private Date date2;
	private static Connection con;
	public void onEnable(){
		log.info("RubinBank enabeling...");
		Bukkit.getServer().getPluginManager().registerEvents(new listeners(), this);
		date1 = new Date();
		this.reloadConfig();
		temp = new ArrayList<Temp>();
		pd = new ArrayList<PlayerDetails>();
		bankomats = new ArrayList<bankomat>();
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
		log.info("RubinBank enabled.");
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
				player.sendMessage("Du hast das RubinBank Dummy-Command ausgef\u00FChrt.");
				if(args.length >= 1){
					if(args[0].equals("ids")){
						int major = Config.getMajorID();
						int minor = Config.getMinorID();
						Material majormaterial = Material.getMaterial(major);
						Material minormaterial = Material.getMaterial(minor);
						player.sendMessage("Major: "+major+" ("+majormaterial.name()+")");
						player.sendMessage("Minor: "+minor+" ("+minormaterial.name()+")");
						return true;
					}
					if(args[0].equals("inv")){
						if(args[1].equals("test")){
							player.getInventory().remove(Material.DIRT);
							player.sendMessage("Dirt entfernt ;)");
							return true;
						}
						if(args[1].equals("clear")){
							player.getInventory().clear();
							player.sendMessage("Inventar gelöscht.");
							return true;
						}
					}
				}
			}
			if(cmd.getName().equalsIgnoreCase("error")){
				player.sendMessage("Du hast das Error Command ausgeführt...\n\u00A2");
				return false;
			}
			if(cmd.getName().equalsIgnoreCase("playerdetails")){
				if(args.length < 2){
					if(args[0].equals(";")){
						player.sendMessage(ChatColor.RED+"Es darft kein"+ChatColor.ITALIC+"\";\""+ChatColor.RESET+ChatColor.RED+" enthalten sein!");
						return true;
					}
					else{
						PlayerDetails details = getPlayerDetails(player);
						details.addDetail(args[1], args[0]);
						setPlayerDetails(details);
						player.sendMessage(ChatColor.GREEN+"Hinzugef\u00FCgt.");
						return true;
					}
				}
				else{
					player.sendMessage(ChatColor.RED+"Du brauchst wenigstens "+ChatColor.ITALIC+"2"+ChatColor.RESET+ChatColor.RED+" Argumente");
					return true;
				}
			}
			if(cmd.getName().equalsIgnoreCase("yaw")){
				player.sendMessage("Your Yaw: "+player.getLocation().getYaw());
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("tools")){
				player.sendMessage("Bankomat Up:" + Config.getBankomatUp().name());
				player.sendMessage("Bankomat Down: " + Config.getBankomatDown().name());
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
				log.info("args.length: "+args.length);
				if(args.length >= 1){
					if(args[0].equals("create")){
						if(account.createAccount(player)){
							player.sendMessage("Konto erstellt.");
							return true;
						}
						else{
							player.sendMessage("Du hast bereits ein Konto.");
							return true;
						}
					}
					if(args[0].equals("amount")){
							double amount = account.getAccountAmount(player);
							if(amount >= 0){
								player.sendMessage("Dein Kontostand betr\u00E4gt "+account.getAccountAmount(player)+".");
							}
							else{
								player.sendMessage("Du hast noch kein Konto.");
							}
							return true;
					}
					if(args.length >= 2){
						if(args[0].equals("payin")){
							double i;
							try{
								i = Double.parseDouble(args[1]);
							} catch(NumberFormatException e){
								player.sendMessage(ChatColor.RED + "'amount' muss eine Zahl sein!");
								player.sendMessage("/account payin amount");
								return true;
							}
							player.sendMessage(Boolean.toString(account.payinToAccount(player, i)));
							player.sendMessage("Zahlung erfolgt.");
						}
						if(args[0].equals("payout")){
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
								return true;
								
							}
							else{
								player.sendMessage("Auszahlung fehlgeschlagen!");
								return true;
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
						player.sendMessage(ChatColor.RED + "Interner Fehler! Versuche es später noch ein mal...");
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
		}//CONSOLE END
		return false;
	}
	public static void addBankomat(bankomat bankomat){
		bankomats.add(bankomat);
	}
	//Temp used to return a existing bankomat.
	public static boolean isBankomat(Block b, int tmp){
		int i = 0;
		while(i < bankomats.size()){
			if(bankomats.get(i).getBlock() == b){
				if(tmp != -1){
					getTemp(tmp).setTemp(bankomats.get(i));
				}
				return true;
			}
			i++;
		}
		return false;
	}
	/*TEMP usage:
	 * you cannot create a temp, you request one
	 * after a request you get an id for your temp.
	 * Then you can use your temp by your id.
	*/
	public static int getATemp(){
		int i = temp.size();
		Temp nulltmp = null;
		temp.add(i, nulltmp);
		return i;
	}
	public static Temp getTemp(int i){
		if(i < temp.size())
			return temp.get(i);
		else
			return null;
	}
	public void overrideTemp(int i, Temp tmp){
		if(i < temp.size())
			temp.set(i, tmp);
	}
	/*PlayerDetails 
	 * bmat.ic.Ma: Bankomat incrase major
	 * bmat.ic.Mi:     --||--		minor
	 * bmat.dc.Ma: --||--   decrase major
	 * bmat.dc.Mi:     --||--		minor
	*/
	public static PlayerDetails getPlayerDetails(Player p){
		int i = 0;
		while(i < pd.size()){
			if(pd.get(i).getPlayer() == p){
				return pd.get(i);
			}
			i++;
		}
		return null;
	}
	public static void setPlayerDetails(PlayerDetails pds){
		int i = 0;
		while(i < pd.size()){
			if(pd.get(i).getPlayer() == pds.getPlayer()){
				break;
			}
			i++;
		}
		if(i == pd.size())
			pd.add(pds);
		else
			pd.set(i, pds);
	}
	public static ArrayList<bankomat> getBankomats(){
		return bankomats;
	}
	public static String getURL(){
		return url;
	}
	public static boolean isinDB(Player p){
		try{
			Connection con = DriverManager.getConnection(url);
			
			Statement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("select * from "+Config.DataBaseAndTable());
			
			while(rs.next()){
				if(rs.getString("user").equals(p.getName())){
					return true;
				}
			}
		} catch (SQLException e) {
			log.severe("MySQL Exception:\n"+e.toString());
		}
		return false;
	}
	public static Connection getConnection(){
		return con;
	}
}

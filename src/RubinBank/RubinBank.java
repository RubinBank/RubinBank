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
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import config.Config;

import RubinBank.bankomat.bankomat;
import RubinBank.listeners.listeners;
import RubinBank.tools.PlayerDetails;
import RubinBank.tools.Temp;

public class RubinBank extends JavaPlugin{
	private static ArrayList<bankomat> bankomats;
	private static ArrayList<Temp> temp;
	private static ArrayList<PlayerDetails> pd;
	private static String url;
	public static Logger log = Bukkit.getLogger();
	private Date date1;
	private Date date2;
	public void onEnable(){
		log.info("RubinBank enabeling...");
		Bukkit.getServer().getPluginManager().registerEvents(new listeners(), this);
		date1 = new Date();
		this.reloadConfig();
		temp = new ArrayList<Temp>();
		pd = new ArrayList<PlayerDetails>();
		bankomats = new ArrayList<bankomat>();
		url = "jdbc:mysql://"+Config.HostAddress()+"/"+Config.HostDatabase()+"?user="+Config.HostUser()+"&password="+Config.HostPassword();
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
			}
			if(cmd.getName().equalsIgnoreCase("error")){
				player.sendMessage("You performed the Error command...\n\u00A2");
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
					Class.forName("com.mysql.jdbc.Driver");
				} catch(ClassNotFoundException e){
					player.sendMessage(ChatColor.RED+"MySQL Treiberklasse nicht gefunden!");
					log.severe(ChatColor.RED+"MySQL Driver Class not found!");
					return true;
				}
				try{
					Connection con = DriverManager.getConnection(url);
					
					Statement stmt = con.createStatement();
					
					ResultSet resultset = stmt.executeQuery("describe "+Config.HostDatabase()+"."+Config.HostTable());
					
					for(int i = 1; resultset.next(); i++){
						player.sendMessage(resultset.getString(i));
					}
					return true;
			} catch(SQLException e){
				player.sendMessage(ChatColor.RED+"MySQL Fehler!");
				log.severe("MySQL exception:\n");
				e.printStackTrace();
				return true;
			}
		}
			if(cmd.getName().equalsIgnoreCase("account")){
				if(args.length > 1){
					if(args[0].equals("create")){
						
					}
				}
				else{
					player.sendMessage("Zu wenig Argumente");
				}
			}
		}
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
					Class.forName("com.mysql.jdbc.Driver");
				} catch(ClassNotFoundException e){
					log.severe(ChatColor.RED+"MySQL Driver Class not found!");
					return true;
				}
				try{
					Connection con = DriverManager.getConnection(url);
					
					Statement stmt = con.createStatement();
					
					ResultSet resultset;
					
					if(args.length == 0){
						resultset = stmt.executeQuery("describe "+Config.HostDatabase()+"."+Config.HostTable());
					}
					else{
						String query = "";
						log.info(Integer.toString(args.length));
						for(int i = 0; i < args.length; i++){
							query += " " + args[i];
						}
						log.info(query);
						
						resultset = stmt.executeQuery(query);
					}
					while(resultset.next()){
						log.info(resultset.getString(1));
					}
					return true;
			} catch(SQLException e){
				log.severe("MySQL exception:\n" + e.toString());
				return true;
			}
		}
			log.info("Only a Player can perform this command!");
		}
		return true;
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
}

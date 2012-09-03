package RubinBank;

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

import RubinBank.bankomat.bankomat;
import RubinBank.listeners.listeners;
import RubinBank.tools.PlayerDetails;
import RubinBank.tools.Temp;

public class RubinBank extends JavaPlugin{
	private static ArrayList<bankomat> bankomats;
	private static ArrayList<Temp> temp;
	private static ArrayList<PlayerDetails> pd;
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
				player.sendMessage("Du hast das RubinBank Dummy-Command ausgefuehrt.");
			}
			if(cmd.getName().equalsIgnoreCase("error")){
				player.sendMessage("You performed the Error command...\n");
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
						player.sendMessage(ChatColor.GREEN+"Hinzugefügt.");
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
			log.info("Only a Player can perform this command!");
		}
		return true;
	}
	public static void addBankomat(bankomat bankomat){
		bankomats.add(bankomat);
	}
	public static boolean isBankomat(Block b, int tmp){
		int i = 0;
		while(i < bankomats.size()){
			if(bankomats.get(i).getBlock() == b){
				Temp t = new Temp("block");
				t.temp(bankomats.get(i));
				return true;
			}
			i++;
		}
		return false;
	}
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
}

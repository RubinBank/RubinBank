package RubinBank;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import RubinBank.bankomat.bankomat;
import RubinBank.bankomat.bankomatsign;

import config.Config;

public class RubinBank extends JavaPlugin{
	private static ArrayList<bankomat> bankomats;
	private static ArrayList<bankomatsign> bankomatsigns;
	public static Logger log = Bukkit.getLogger();
	private static Config config = new Config();
	private Date date1;
	private Date date2;
	public void onEnable(){
		log.info("RubinBank enabeling...");
		date1 = new Date();
		config.startup();
		log.info("RubinBank enabled.");
	}
	public void onDisable(){
		date2 = new Date();
		String Time;
		long time = date2.getTime() - date1.getTime();
		if(!(time / 60 < 5)){
			time = time / 60;
			Time = time + "min";
		}
		else{
			Time = time + "ms";
		}
		log.info("RubinBank disabeling after "+Time+"...");
		config.shutdown();
		log.info("RubinBank disabled.");
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("rubinbank")){
				player.sendMessage("Du hast das RubinBank Dummy-Command ausgeführt.");
			}
		}
		else{
			log.info("Only a Player can perform this command!");
		}
		return true;
	}
	public static void addBankomat(bankomat bankomat){
		bankomats.add(bankomat);
	}
	public static void addBankomatSign(bankomatsign bankomatsign){
		bankomatsigns.add(bankomatsign);
	}
	public static bankomatsign getbankomatsign(Block block){
		int i = 0;
		while(i < bankomatsigns.size()){
			Block b = bankomatsigns.get(i).getBlock();
			if(b == block){
				return bankomatsigns.get(i);
			}
			i++;
		}
		return null;
	}
	
}

package me.criztovyl.rubinbank.config;
import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
public class Config {
	public static String sep = File.separator;
	JavaPlugin jp;
	private static File RubinBankConf = new File(Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getDataFolder() + sep + "config.yml");
	private static FileConfiguration conf = Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig();
	public Config(){
		if(!RubinBankConf.exists()){
			Bukkit.getServer().getPluginManager().getPlugin("RubinBank").saveDefaultConfig();
		}
	}
	public static boolean enable(){
		return conf.getBoolean("enabled.RubinBank");
	}
	public static int getMajorID(){
		return conf.getInt("Currency.Major.ItemID");
	}
	public static int getMinorID(){
		if(useMinor()){
			return conf.getInt("Currency.Minor.ItemID");
		}
		else{
		return -1;
		}
	}
	public static boolean useMinor(){
		return conf.getBoolean("Currency.Minor.useItem");
	}
	public static String getMajorS(){
		return conf.getString("Currency.Name.Major.Singular");
	}
	public static String getMajorP(){
		return conf.getString("Currency.Name.Major.Plural");
	}
	public static String getMinorS(){
		return conf.getString("Currency.Name.Minor.Singular");
	}
	public static  boolean useMinorP(){
		return conf.getBoolean("Currency.Name.Minor.usePlural");
	}
	public static String getMinorP(){
		if(useMinorP()){
			return conf.getString("Currency.Name.Minor.Plural");
		}
		else{
			return null;
			
		}
	}
	public static String sep(){
		return sep;
	}
	public static String HostAddress(){
		return conf.getString("MySQL.Host.Address");
	}
	public static String HostUser(){
		return conf.getString("MySQL.Host.User");
	}
	public static String HostPassword(){
		return conf.getString("MySQL.Host.Password");
	}
	public static String HostDatabase(){
		return conf.getString("MySQL.Host.Database");
	}
	public static String HostTable(){
		return conf.getString("MySQL.Host.Table");
	}
	public static String DataBaseAndTable(){
		return conf.getString("MySQL.Host.Database") + "."
				+ conf.getString("MySQL.Host.Table");
	}
	public static boolean useWorldGuard(){
		return conf.getBoolean("enabled.WorldGuard");
	}
	public static void setWorldGuard(boolean WG){
		conf.set("enabled.WorldGuard", WG);
	}
	public static String getRegion(){
		return conf.getString("WorldGuardOptions.limitToRegionWithParent");
	}
	public static List<String> limitedToRegion(){
		return conf.getStringList("WorldGuardOptions.commandsLimitedToRegion");
	}
}

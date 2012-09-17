package config;
import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
public class Config {
	public static String sep = File.separator;
	JavaPlugin jp;
	private static File RubinBankConf = new File(Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getDataFolder() + sep + "config.yml");
	public Config(){
		if(!RubinBankConf.exists()){
			Bukkit.getServer().getPluginManager().getPlugin("RubinBank").saveDefaultConfig();
		}
	}
	public void doNothing(){

	}
	public int getMajorID(){
		return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getInt("Currency.Major.ItemID");
	}
	public int getMinorID(){
		if(useMinor()){
			return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getInt("Currency.Minor.ItemID");
		}
		else{
		return -1;
		}
	}
	public boolean useMinor(){
		return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getBoolean("Currency.Minor.useItem");
	}
	public String getMajorS(){
		return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getString("Currency.Name.Major.Singular");
	}
	public String getMajorP(){
		return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getString("Currency.Name.Major.Plural");
	}
	public String getMinorS(){
		return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getString("Currency.Name.Minor.Singular");
	}
	public boolean useMinorP(){
		return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getBoolean("Currency.Name.Minor.usePlural");
	}
	public String getMinorP(){
		if(useMinorP()){
			return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getString("Currency.Name.Minor.Plural");
		}
		else{
			return null;
			
		}
	}
	public static Material getBankomatUp(){
		int id = Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getInt("Bankomat.Item.Up.ID");
		Material mat = Material.getMaterial(id);
		return mat;
	}
	public static Material getBankomatDown(){
		int id = Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getInt("Bankomat.Item.Down.ID");
		Material mat = Material.getMaterial(id);
		return mat;
	}
	public double getMinorRatio(){
		double x = 0;
		String y = Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getString("Currency.ratio.MajorToMinor");
		String[] z = y.split("/");
		int a = Integer.parseInt(z[0]);
		int b = Integer.parseInt(z[1]);
		x = a / b;
		return x;
	}
	public static String sep(){
		return sep;
	}
	public static String HostAddress(){
		return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getString("MySQL.Host.Address");
	}
	public static String HostUser(){
		return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getString("MySQL.Host.User");
	}
	public static String HostPassword(){
		return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getString("MySQL.Host.Password");
	}
	public static String HostDatabase(){
		return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getString("MySQL.Host.Database");
	}
	public static String HostTable(){
		return Bukkit.getServer().getPluginManager().getPlugin("RubinBank").getConfig().getString("MySQL.Host.Table");
	}
}

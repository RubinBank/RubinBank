package me.criztovyl.rubinbank.config;
import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
	public void doNothing(){

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
	public static Material getBankomatUp(){
		int id = conf.getInt("Bankomat.Item.Up.ID");
		Material mat = Material.getMaterial(id);
		return mat;
	}
	public static Material getBankomatDown(){
		int id = conf.getInt("Bankomat.Item.Down.ID");
		Material mat = Material.getMaterial(id);
		return mat;
	}
	public static double getMinorRatio(){
		double x = 0;
		String y = conf.getString("Currency.ratio.MajorToMinor");
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
	public static boolean hasSpawn(String worldname){
		return conf.contains("Server.Spawn."+worldname);
	}
	public static Location getSpawn(String worldname){
		if(hasSpawn(worldname)){
			double x = conf.getDouble("Server.Spawn."+worldname+".x");
			double y = conf.getDouble("Server.Spawn."+worldname+"y");
			double z = conf.getDouble("Server.Spawn."+worldname+"z");
			World world = Bukkit.getWorld(conf.getString("Server.Spawn."+worldname));
			return new Location(world, x, y, z);
		}
		else{
			if(conf.getBoolean("GlobalSpawn.use") && conf.getBoolean("GlobalSpawn.set")){
				double x = conf.getDouble("GlobalSpawn.x");
				double y = conf.getDouble("GlobalSpawn.y");
				double z = conf.getDouble("GlobalSpawn.z");
				World world = Bukkit.getWorld(conf.getString("GlobalSpawn.world"));
				return new Location(world, x, y, z);
			}
			return null;
		}
	}
	public static boolean GlobalSpawn(){
		return conf.getBoolean("GlobalSpawn.use");
	}
	public static void setGlobalSpawn(Location loc){
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		String world = loc.getWorld().getName();
		conf.set("GlobalSpawn.x", x);
		conf.set("GlobalSpawn.y", y);
		conf.set("GlobalSpawn.z", z);
		conf.set("GlobalSpawn.world", world);
	}
	public static void setSpawn(Location loc){
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		String world = loc.getWorld().getName();
		conf.set("Server.Spawn."+world+".x", x);
		conf.set("Server.Spawn."+world+".y", y);
		conf.set("Server.Spawn."+world+".z", z);
		conf.set("Server.Spawn."+world+".world", world);
	}
}

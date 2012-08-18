package config;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import RubinBank.RubinBank;

public class Config {
	private static FileConfiguration conf;
	private static String sep = File.separator;
	private static File RubinBankConf = new File("plugins"+sep+"RubinBank"+sep+"config.yml");
	//TODO Make Comments / Config File head
	public Config(){
		try {
			conf.load(RubinBankConf);
		} catch (FileNotFoundException e) {
			RubinBank.log.severe("");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conf.addDefault("enabled", true);
		conf.addDefault("mysql.Host", "localhost");
		conf.addDefault("mysql.Host.Password", "");
		conf.addDefault("mysql.Host.User", "");
		conf.addDefault("mysql.Host.Database", "RubinBank");
		conf.addDefault("mysql.Host.Database.Table", "Bank");
		conf.addDefault("Currency.Name.Major.Singular", "Rubin");
		conf.addDefault("Currency.Name.MajorPlural", "Rubins");
		conf.addDefault("Currency.Name.Minor.Singular", "Gold");
		conf.addDefault("Currency.Name.Minor.usePlural", false);
		conf.addDefault("Currency.Name.Minor.Plural", "");
		conf.addDefault("Currency.Major.ItemID", 388);
		conf.addDefault("Currency.Minor.useItem", false);
		conf.addDefault("Currency.Minor.ItemID", "");
		conf.addDefault("Currency.Minor.useVirtual", true); //Minor nur als Virtuelle Währung also kein Item
		conf.addDefault("Currency.ratio.MajorinMinor", "3");
		conf.addDefault("Bankomat.Item.Up.ID", 288);
		conf.addDefault("Bankomat.Item.Down.ID", 280);
	}
	public void startup(){
		RubinBank.log.info("Beginning Config startup...");
		Date date = new Date();
		if(!RubinBankConf.exists()){
			try{
				RubinBankConf.createNewFile();
				conf.save(RubinBankConf);
			}
			catch(Exception e){
				RubinBank.log.severe("Exception at RubinBank.config.Config.startup RubinBankConf.mkdirs()(Branch !RBC.e/mkdirs):\n"+e.toString()+"\n");
				e.printStackTrace();
			}
		}	
		else{
			try {
				conf.load(RubinBankConf);
			} catch (FileNotFoundException e) {
				RubinBank.log.severe("Exception at RubinBank.config.Config.startup conf.Load()(Branch RBC.e/load):\n"+e.toString());
			} catch (IOException e) {
				RubinBank.log.severe("Exception at RubinBank.config.Config.startup conf.Load()(Branch RBC.e/load):\n"+e.toString());
			} catch (InvalidConfigurationException e) {
				RubinBank.log.severe("Exception at RubinBank.config.Config.startup conf.Load()(Branch RBC.e/load):\n"+e.toString());
			}
		}
		Date date2 = new Date();
		long ms = date2.getTime() - date.getTime();
		RubinBank.log.info("Config startup done.("+ms+"ms)");
	}
	public void shutdown(){
		RubinBank.log.info("Config shutdown begin...");
		Date date = new Date();
		try{
			conf.save(RubinBankConf);
		}
		catch(Exception e){
			RubinBank.log.severe("Exception at RubinBank.config.Config.shutdown conf.save():\n"+e.toString());
		}
		Date date2 = new Date();
		long ms = date2.getTime() - date.getTime();
		RubinBank.log.info("Config shutdown done.("+ms+"ms)");
	}
	public static void save(){
		try{
			conf.save(RubinBankConf);
		}
		catch(Exception e){
			RubinBank.log.severe("Exception at RubinBank.config.Config.save conf.save():\n"+e.toString());
		}
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
	public static boolean useMinorP(){
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
}

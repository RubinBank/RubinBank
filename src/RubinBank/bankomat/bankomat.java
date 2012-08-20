//TODO meld bankomat.java and bankomatsign.java
package RubinBank.bankomat;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import RubinBank.bank.Bank;
import config.Config;


public class bankomat {
	private static Player player;
	private static String Name;
	private static bankomatsign attachedbmatsign;
	private Config conf;
	public bankomat(Player p){
		player = p;
		conf = new Config();
	}
	public static void setName(String name){
		Name = name;
	}
	public static Player getPlayer(){
		return player;
	}
	public static String getName(){
		return Name;
	}
	public void withdraw(double amount, Player player){
		double withdraw = Bank.withdraw(amount, player);
		if(withdraw > 0){
			int major = (int) ((amount - (amount % 4)) * 0.75); //75% von der geraden Anzahl von Major
			double restamount = (amount - major) % conf.getMinorRatio();//Rest von Major / 4
			int minor = (int) (((amount - major)+restamount) / conf.getMinorRatio());//Minor in ganzer Zahl
			restamount -= minor;
			if(restamount > 0){
				player.sendMessage("Es wurden "+(restamount / conf.getMinorRatio())+" "+conf.getMajorS()+" zurückgebucht, da diese sich nicht sinnvoll umwandeln lassen.");
				Bank.store(restamount, player);
			}
			int MajorID = conf.getMajorID();
			if(conf.useMinor()){
				String majorName;
				String minorName;
				int MinorID = conf.getMinorID();
				Material majorMaterial = Material.getMaterial(MajorID);
				Material minorMaterial = Material.getMaterial(MinorID);
				ItemStack majorstack = new ItemStack(majorMaterial, major);
				ItemStack minorstack = new ItemStack(minorMaterial, minor);
				player.getInventory().addItem(majorstack);
				player.getInventory().addItem(minorstack);
				if(major < 2){
					majorName = conf.getMajorS();
				}
				else{
					majorName = conf.getMajorP();
				}
				if(minor < 2){
					minorName = conf.getMinorS();
				}
				else{
					minorName = conf.getMinorP();
				}
				player.sendMessage("Du hast " + major + " " + majorName + " und " + minor + " " + minorName + " abgehoben.\nAlles wurde in deinem Inventar abgelegt.");
			}
			else{
				player.sendMessage("Noch nicht eingebunden");
				Bank.store(amount, player);
			}	
		}
	
	}
	public static void setattachedSign(bankomatsign bmatsign){
		attachedbmatsign = bmatsign;
	}
	public static bankomatsign getattachedSign(){
		return attachedbmatsign;
	}
	public static void store(double amount, Player player){
		Bank.store(amount, player);
	}
}

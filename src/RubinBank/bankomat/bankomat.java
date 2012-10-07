package RubinBank.bankomat;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import RubinBank.RubinBank;
import RubinBank.tools.BankomatTyp;
import RubinBank.tools.PlayerDetails;
import config.Config;


public class bankomat {
	private static Player player;
	private static String Name;
	private static Block b;
	private static BankomatTyp typ;
	private int MajorAmount;
	private int MinorAmount;
	private int incrasevaluemajor;
	private int incrasevalueminor;
	private int decrasevaluemajor;
	private int decrasevalueminor;
	public bankomat(Block block){
		b = block;
		new Config();
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
	/*public void withdraw(double amount, Player player){
		double withdraw = Bank.withdraw(amount, player);
		if(withdraw > 0){
			int major = (int) ((amount - (amount % 4)) * 0.75); //75% of even Major
			double restamount = (amount - major) % conf.getMinorRatio();//Rest of Major / 4(ratio)
			int minor = (int) ((amount - major)-restamount / conf.getMinorRatio());//Minor integer
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
					//TODO minor not choose able?
			}	
		}
	
	}*/
	public void setType(BankomatTyp t){
		typ = t;
	}
	public static BankomatTyp getTyp(){
		return typ;
	}
	public Block getBlock(){
		return b;
	}
	public Location getLoc(){
		return b.getLocation();
	}
	/*
	public static void store(double amount, Player player){
		Bank.store(amount, player);
	}*/
	public int getMajorAmount(){
		return MajorAmount;
	}
	public int getMinorAmount(){
		return MinorAmount;
	}
	public void incraseMajor(int i){
		MajorAmount += i;
	}
	public void decraseMajor(int i){
		MajorAmount -= i;
	}
	public void incraseMinor(int i){
		MinorAmount += i;
	}
	public void decraseMinor(int i){
		MinorAmount -= i;
	}
	public void setIncraseValueMajor(int i){
		incrasevaluemajor = i;
	}
	public void setIncraseValueMinor(int i){
		incrasevalueminor = i;
	}
	public void setDecraseValueMajor(int i){
		decrasevaluemajor = i;
	}
	public void setDecraseValueMinor(int i){
		decrasevalueminor = i;
	}
	public int getIncraseValueMajor(Player p){
		if(p == null)
		return incrasevaluemajor;
		else{
			PlayerDetails pd = RubinBank.getPlayerDetails(p);
			return Integer.parseInt((String) pd.getDetail("incrasevaluemajor"));
		}
			
	}
	public int getIncraseValueMinor(Player p){
		if(p == null)
		return incrasevalueminor;
		else{
			PlayerDetails pd = RubinBank.getPlayerDetails(p);
			return Integer.parseInt((String) pd.getDetail("incrasevalueminor"));
		}
			
	}
	public int getDecraseValueMajor(Player p){
		if(p == null)
		return decrasevaluemajor;
		else{
			PlayerDetails pd = RubinBank.getPlayerDetails(p);
			return Integer.parseInt((String) pd.getDetail("decrasevaluemajor"));
		}
			
	}
	public int getDecraseValueMinor(Player p){
		if(p == null)
		return decrasevalueminor;
		else{
			PlayerDetails pd = RubinBank.getPlayerDetails(p);
			return Integer.parseInt((String) pd.getDetail("decrasevalueminor"));
		}
			
	}
	public void incraseMajorPD(Player p){
		if(p.hasPermission("RubinBank.Bankomat.changeAmount.up.byPD")){
			incraseMajor(Integer.parseInt(RubinBank.getPlayerDetails(p).getDetail("bmat.ic.Ma").toString()));
		}
		else{
			incraseMajor(incrasevaluemajor);
		}
	}
	public void incraseMinorPD(Player p){
		if(p.hasPermission("RubinBank.Bankomat.changeAmount.up.byPD")){
			incraseMajor(Integer.parseInt(RubinBank.getPlayerDetails(p).getDetail("bmat.ic.Mi").toString()));
		}
		else{
			incraseMajor(incrasevalueminor);
		}
	}
	public void decraseMajorPD(Player p){
		if(p.hasPermission("RubinBank.Bankomat.changeAmount.down.byPD")){
			incraseMajor(Integer.parseInt(RubinBank.getPlayerDetails(p).getDetail("bmat.dc.Ma").toString()));
		}
		else{
			incraseMajor(decrasevaluemajor);
		}
	}
	public void decraseMinorPD(Player p){
		if(p.hasPermission("RubinBank.Bankomat.changeAmount.down.byPD")){
			incraseMajor(Integer.parseInt(RubinBank.getPlayerDetails(p).getDetail("bmat.dc.Mi").toString()));
		}
		else{
			incraseMajor(decrasevalueminor);
		}
	}
}

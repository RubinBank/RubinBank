package me.criztovyl.rubinbank.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.tools.MySQL;
import me.criztovyl.rubinbank.tools.MySQL_;
import me.criztovyl.rubinbank.tools.Tools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Account {
	public static void createAccount(String p_n){
		MySQL.accountAction(p_n, null, AccountAction.CREATE, 0);
	}
	public static boolean hasAccount(String p_n){
		MySQL_ mysql = RubinBank.getMySQL_();
		ResultSet rs = mysql.executeQuery("select account from " + Config.AccountsTable() + " where user='" + p_n + "'");
		try {
			if(rs.first()){
				return rs.getBoolean("account");
			}
			else{
				return false;
			}
		} catch (SQLException e) {
			RubinBank.log.severe("MySQL ResultSet Exception:\n" + e.toString());
			return false;
		}

	}
	public static boolean hasEnughMoney(String p_n, double amount){
		if(getAccountAmount(p_n) >= amount)
			return true;
		else
			return false;
	}
	public static double getAccountAmount(String p_n){
		ResultSet rs = RubinBank.getMySQL_().executeQuery("Select account, amount from " + Config.AccountsTable() + " where user='" + p_n + "'");
		if(hasAccount(p_n)){
			try{
				rs.first();
				
				if(rs.getBoolean("account")){
					double amount = (double) (Math.round(rs.getDouble("amount") * 100.0 )/100.0);
					return amount;
				}
				else
					return -1;
			} catch(SQLException e){
				RubinBank.log.severe("MySQL resultSet Exception:\n" + e.toString());
				return -1;
			}
		}
		else
			return -1.0;
	}
	public static void amountMsg(String p_n){
		if(hasAccount(p_n))
		msg(p_n, ChatColor.DARK_AQUA + "Dein Kontostand beträgt: " + getAccountAmount(p_n));
		else
			msg(p_n, ChatColor.DARK_AQUA + "Du hast noch kein Konto erstellt.");
	}
	public static boolean payinToAccount(String p_n, double increase){
		if(hasAccount(p_n)){
			Player p = Bukkit.getServer().getPlayer(p_n);
			int major = (int) increase;
			int minor = (int)((Math.round((increase - (int) increase)*10.0)/10.0)*10.0);
			boolean hasmajor = p.getInventory().contains(Material.getMaterial(Config.getMajorID()), major);
			boolean hasminor = p.getInventory().contains(Material.getMaterial(Config.getMinorID()), minor);
			boolean ignoreminor;
			if(minor <= 0){
				hasminor = true;
				ignoreminor = true;
			}
			else{
				ignoreminor = false;
			}
			if(hasmajor && hasminor){
				try{
					p.getInventory().removeItem(new ItemStack(Config.getMajorID(), major));
					if(!ignoreminor){
						p.getInventory().removeItem(new ItemStack(Config.getMinorID(), minor));
					}
				} catch(Exception e){
				 RubinBank.log.severe(e.toString());
				 return false;
				}
				MySQL.accountAction(p_n, null, AccountAction.IN, increase);
				msg(p_n, ChatColor.DARK_AQUA + "Neuer Kontostand: " + getAccountAmount(p_n));
				return true;
			}
			else{
				msg(p_n, ChatColor.YELLOW + "Du hast nicht genug Items in deinem Inventar!");
				return false;
			}
		}
		else{
			msg(p_n, "Du hast kein Konto!");
			return false;
		}
	}
	public static boolean payoutFromAccount(String p_n, double decrease){
		Player p = Bukkit.getServer().getPlayer(p_n);
		if(decrease > 0){
			if(hasEnughMoney(p_n, decrease)){
				int minor = (int)((Math.round((decrease - (int) decrease)*10.0)/10.0)*10.0);
				ItemStack majorStack = new ItemStack(Config.getMajorID(), (int) decrease);
				ItemStack minorStack = new ItemStack(Config.getMinorID(), minor);
				HashMap<Integer, ItemStack> majorfit = new HashMap<Integer, ItemStack>();
				HashMap<Integer, ItemStack> minorfit = new HashMap<Integer, ItemStack>();
				if(minor > 0)
					minorfit = p.getInventory().addItem(minorStack);
				if((int) decrease > 0){
					majorfit = p.getInventory().addItem(majorStack);
				}
				if(!majorfit.isEmpty()){
					ItemStack stack = majorfit.get(0);
					decrease -=  stack.getAmount();
				}
				if(!minorfit.isEmpty()){
					ItemStack stack = minorfit.get(0);
					decrease = (decrease*10 - stack.getAmount())/10;
				}
				MySQL.accountAction(p_n, null, AccountAction.OUT, decrease);
				msg(p_n, ChatColor.DARK_AQUA + "Neuer Kontostand: " + getAccountAmount(p_n));
				return true;
			}
			else{
				msg(p_n, ChatColor.YELLOW + "Du hast nicht genug Geld!");
				return false;
			}
		}
		else{
			return true;
		}
	}
	public static void transfer(double transfer, String from, String to){
		if(transfer <= 0){
			msg(from, ChatColor.RED + "Überweisungen sollten Positiv sein!");
		}
		else{
			if(hasEnughMoney(from, transfer)){
				if(hasAccount(to)){
					MySQL.accountAction(from, to, AccountAction.TRANSFER, transfer);
				}
				else{
					msg(from, ChatColor.YELLOW + to + " hat kein Konto!");
				}
			}
			else{
				msg(from, ChatColor.RED + "Du hast nicht genug Geld!");
			}
		}
	}
	public static void msg(String p_n, String msg){
		Tools.msg(p_n, msg);
	}
	public static void payinItemInHand(String p_n){
		Player p = Bukkit.getPlayer(p_n);
		int id = p.getItemInHand().getTypeId();
		if(id == Config.getMajorID()){
			ItemStack stack = p.getItemInHand();
			p.getInventory().remove(stack);
			MySQL.accountAction(p_n, null, AccountAction.IN, stack.getAmount());
		}
		if(id == Config.getMinorID()){
			ItemStack stack = p.getItemInHand();
			p.getInventory().remove(stack);
			MySQL.accountAction(p_n, null, AccountAction.IN, stack.getAmount()/10.0);
		}
	}
	public static void withdraw(String p_n, double amount){
		MySQL.accountAction(p_n, null, AccountAction.OUT, amount);
	}
	public static void deposit(String p_n, double amount){
		MySQL.accountAction(p_n, null, AccountAction.IN, amount);
	}
}

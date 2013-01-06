package me.criztovyl.rubinbank.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.tools.AccountAction;
import me.criztovyl.rubinbank.tools.MySQL;
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
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("select account from " + Config.AccountsTable() + " where user='" + p_n + "'");
			
			if(rs.next()){
				rs.first();
				return rs.getBoolean("account");
			}
			else
				return false;
			
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: select account from " + Config.AccountsTable() + " where user='" + p_n + "'");
			e.printStackTrace();
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
		if(hasAccount(p_n)){
			try{
				Statement stmt = RubinBank.getConnection().createStatement();
				
				ResultSet rs = stmt.executeQuery("Select account, amount from " + Config.AccountsTable() + " where user='" + p_n + "'");
				
				rs.first();
				
				if(rs.getBoolean("account")){
					double amount = (double) (Math.round(rs.getDouble("amount") * 100.0 )/100.0);
					return amount;
				}

				else
					return -1;
			} catch(SQLException e){
				RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Select account, amount from " + Config.AccountsTable() + " where user='" + p_n + "'");
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
		Player p = Bukkit.getServer().getPlayer(p_n);
		int major = (int) increase;
		int minor = (int) ((double) Math.round((increase - major)*10));
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
				if(!ignoreminor)
					p.getInventory().removeItem(new ItemStack(Config.getMinorID(), minor));
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
	public static boolean payoutFromAccount(String p_n, double decrase){
		Player p = Bukkit.getServer().getPlayer(p_n);
		if(decrase > 0){
			if(hasEnughMoney(p_n, decrase)){
				int major = (int) decrase;
				int minor = ((int) ((decrase - major) * 10))/10;
				ItemStack majorStack = new ItemStack(Config.getMajorID(), major);
				ItemStack minorStack = new ItemStack(Config.getMinorID(), minor);
				if(minor > 0)
					p.getInventory().addItem(minorStack);
				p.getInventory().addItem(majorStack);
				MySQL.accountAction(p_n, null, AccountAction.OUT, decrase);
				msg(p_n, ChatColor.DARK_AQUA + "Neuer Kontostand: " + getAccountAmount(p_n));
				return true;
			}
			else{
				RubinBank.log.info("Player " + p_n + " PayOut Error...(amount < 0)");
				msg(p_n, ChatColor.YELLOW + "Du hast nicht genug Geld!");
				return false;
			}
		}
		else{
			return true;
		}
	}
	public static void transfer(double transfer, String from, String to){
		if(getAccountAmount(from) >= transfer){
			if(hasAccount(to)){
				MySQL.accountAction(from, to, AccountAction.TRANSFER, transfer);
			}
			else{
				msg(from, ChatColor.YELLOW + to + " hat kein Konto!");
			}
		}
	}
	public static void msg(String p_n, String msg){
		Tools.msg(p_n, msg);
	}
}

package me.criztovyl.rubinbank.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.config.Config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Account {
	public static void createAccount(Player p){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("select amount, account from " + Config.UsersTable() + " where user='" + p.getName() + "'");
			
			rs.first();
			
			
			if(hasAccount(p)){
				p.sendMessage(ChatColor.YELLOW + "Du hast schon ein Konto.");
			}
			
			
			stmt.executeUpdate("Update "+Config.UsersTable()+" set amount=0, account=true where user='" + p.getName() + "'");
			p.sendMessage(ChatColor.DARK_AQUA + "Konto erstellt.");
		} catch(SQLException e){
			p.sendMessage("Interner Fehler.");
			RubinBank.log.severe("MySQL Exception:\n" + e.toString());
		}
	}
	public static boolean hasAccount(Player p){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("select amount, account from " + Config.UsersTable() + " where user='" + p.getName() + "'");
			
			rs.first();
			
			return rs.getBoolean("account");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString());
			return false;
		}

	}
	public static double getAccountAmount(Player p){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("Select amount, account from "+Config.UsersTable()+" where user='"+p.getName()+"'");
			
			rs.first();
			
			if(rs.getBoolean("account")){
				double amount = (Math.round(rs.getDouble("amount") * 100 )/100);
				return amount;
			}

			else
				return -1;
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n"+e.toString());
			return -1;
		}
	}
	public static void amountMsg(Player p){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("Select amount, account from "+Config.UsersTable()+" where user='"+p.getName()+"'");
			
			rs.first();
			
			if(rs.getBoolean("account")){
				double amount = (Math.round(rs.getDouble("amount") * 100 )/100);
				p.sendMessage(ChatColor.DARK_AQUA + "Dein Kontostand beträgt " + amount);
			}

			else
				p.sendMessage(ChatColor.YELLOW + "Du hast kein Konto");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n"+e.toString());
			p.sendMessage(ChatColor.RED + "Interner Fehler.");
		}
	}
	public static boolean payinToAccount(Player p, double increase){
		increase = (double) Math.round((increase * 100 ))/100;
		int major = (int) increase;
		int minor = (int) ((double) Math.round(((increase - major)*10)*10)/10);
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
			
			try{
				Statement stmt = RubinBank.getConnection().createStatement();
				
				ResultSet rs = stmt.executeQuery("select amount, account from "+Config.UsersTable()+" where user='"+p.getName()+"'");
				
				rs.first();
				
				double amount = rs.getDouble("amount");
				
				if(!rs.getBoolean("account"))
					return false;
				
				amount += (double) (Math.round(increase * 100) /100);
				
				p.sendMessage(ChatColor.DARK_AQUA + "neuer Kontostand: " + Double.toString(amount));
				
				stmt.executeUpdate("Update "+Config.UsersTable()+" set amount="+amount+" where user='"+p.getName()+"'");
				return true;
			} catch(SQLException e){
				RubinBank.log.severe("MySQL Exception:\n"+e.toString());
				return false;
			}
		}
		else{
			p.sendMessage(ChatColor.YELLOW + "Du hast nicht genug Items in deinem Inventar! ");
			return false;
		}

	}
	public static boolean payoutFromAccount(Player p, double decrase){
		if(decrase > 0){
			try{
				
				Statement stmt = RubinBank.getConnection().createStatement();
				
				ResultSet rs = stmt.executeQuery("select amount, account from "+Config.UsersTable()+" where user=\""+p.getName()+"\"");
				
				rs.first();
				
				if(rs.getBoolean("account")){
					double amount = rs.getDouble("amount");
					amount -= (double) Math.round((decrase * 100 )/100);
					if(amount >= 0){
						stmt.executeUpdate("update "+Config.UsersTable()+" set amount=\""+amount+"\" where user=\""+p.getName()+"\"");
						int major = (int) decrase;
						int minor = (int) ((double) Math.round(((decrase - major)*10)*10)/10);
						ItemStack majorStack = new ItemStack(Config.getMajorID(), major);
						ItemStack minorStack = new ItemStack(Config.getMinorID(), minor);
						if(minor > 0)
							p.getInventory().addItem(minorStack);
						p.getInventory().addItem(majorStack);
						p.sendMessage(ChatColor.DARK_AQUA + "neuer Kontostand: " + Double.toString((Math.round(amount * 100)/100)));
						return true;
					}
					else{
						RubinBank.log.info("Player "+p.getName()+" PayOut Error...(amount < 0)");
						p.sendMessage(ChatColor.YELLOW + "Du hast nicht genug Geld!");
						return false;
					}
				}
				else{
					RubinBank.log.info("Player "+p.getName()+" PayOut Error... has no account");
					return false;
				}
			} catch(SQLException e){
				RubinBank.log.info("SQLEception: "+e.toString());
				return false;
			}
		}
		else{
			return true;
		}
	}
	public static void transfer(double transfer, Player from, Player to){
		if(getAccountAmount(from) >= transfer){
			if(hasAccount(to)){
				
			}
			else{
				from.sendMessage(ChatColor.YELLOW + to.getName() + " hat kein Konto!");
			}
		}
	}
}

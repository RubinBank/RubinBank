package me.criztovyl.rubinbank.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.config.Config;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class account {
	public static boolean createAccount(Player p){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("select amount, account from " + Config.DataBaseAndTable() + " where user='" + p.getName() + "'");
			
			rs.first();
			
			boolean hasaccount = rs.getBoolean("account");
			
			if(hasaccount)
				return false;
			
			stmt.executeUpdate("Update "+Config.DataBaseAndTable()+" set amount=0, account=true where user='" + p.getName() + "'");
			return true;
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString());
			return false;
		}
	}
	public static boolean hasAccount(Player p){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("select amount, account from " + Config.DataBaseAndTable() + " where user='" + p.getName() + "'");
			
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
			
			ResultSet rs = stmt.executeQuery("Select amount, account from "+Config.DataBaseAndTable()+" where user='"+p.getName()+"'");
			
			rs.first();
			
			if(rs.getBoolean("account"))
				return rs.getDouble("amount");
			else
				return -1;
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n"+e.toString());
			return -1;
		}
	}
	public static boolean payinToAccount(Player p, double incrase){
		int major = (int) incrase;
		int minor = (int) (Math.round( ((incrase - major)*10) * 100.0 ) / 100.0);//Thanks to http://stackoverflow.com/a/4796874
		incrase = Math.round((incrase * 100.0 ) / 100.0);//Thanks to http://stackoverflow.com/a/4796874
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
				
				ResultSet rs = stmt.executeQuery("select amount, account from "+Config.DataBaseAndTable()+" where user='"+p.getName()+"'");
				
				rs.first();
				
				double amount = rs.getDouble("amount");
				
				if(!rs.getBoolean("account"))
					return false;
				
				p.sendMessage("Kontostand: "+amount);
				
				amount += incrase;
				
				p.sendMessage("neuer Kontostand: "+amount);
				
				stmt.executeUpdate("Update "+Config.DataBaseAndTable()+" set amount="+amount+" where user='"+p.getName()+"'");
				return true;
			} catch(SQLException e){
				RubinBank.log.severe("MySQL Exception:\n"+e.toString());
				return false;
			}
		}
		else{
			p.sendMessage("Du hast nicht genug Items in deinem Inventar! ");
			return false;
		}

	}
	public static boolean payoutFromAccount(Player p, double decrase){
		try{
			
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("select amount, account from "+Config.DataBaseAndTable()+" where user=\""+p.getName()+"\"");
			
			rs.first();
			
			if(rs.getBoolean("account")){
				double amount = rs.getDouble("amount");
				amount -= decrase;
				if(amount >= 0){
					stmt.executeUpdate("update "+Config.DataBaseAndTable()+" set amount=\""+amount+"\" where user=\""+p.getName()+"\"");
					int major = (int) decrase;
					int minor = (int) (Math.round( ((decrase - major)*10) * 100.0 ) / 100.0);//Thanks to http://stackoverflow.com/a/4796874
					ItemStack majorStack = new ItemStack(Config.getMajorID(), major);
					ItemStack minorStack = new ItemStack(Config.getMinorID(), minor);
					p.getInventory().addItem(minorStack);
					p.getInventory().addItem(majorStack);
					return true;
				}
				else{
					RubinBank.log.info("Player "+p.getName()+" PayOut Error...(amount < 0)");
					p.sendMessage("Du hast nicht genug Geld!");
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
}

package me.criztovyl.rubinbank.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.account.Account;
import me.criztovyl.rubinbank.config.Config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;



public class MySQL{
	public static void insertBankomat(Location loc, String pos, String location){
		RubinBank.log.info("Inserted Bankomat");
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("Insert into " + Config.BankomatsTable() + " (LocationX, LocationY, LocationZ, LocationWorld, Pos, Location) values(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld().getName() + "\", \"" + pos + "\", \"" + location + "\")");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "Query: " +
					"Insert into " + Config.BankomatsTable() + " (LocationX, LocationY, LocationZ, LocationWorld, Pos, Location) values(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld().getName() + "\", \"" + pos + "\", \"" + location + "\")");
		}
		RubinBank.updateBankomatLocs();
	}
	public static void insertnoMultiBankomat(Location loc, String pos, BankomatType type, String location){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("Insert into " + Config.BankomatsTable() + " (LocationX, LocationY, LocationZ, LocationWorld, Pos, Type, Multi, Location) values(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld().getName() + "\", \"" + pos + "\", \"" + type.toString() +"\", 0, \"" + location + "\")");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "Query: " +
					"Insert into " + Config.BankomatsTable() + " (LocationX, LocationY, LocationZ, LocationWorld, Pos, Type, Multi, Location) values(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld().getName() + "\", \"" + pos + "\", \"" + type.toString() +"\", 0, \"" + location + "\")");
		}
		RubinBank.updateBankomatLocs();
	}
	public static boolean updateTriggers(){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("Select * from " + Config.BankomatsTable());
			
			ArrayList<Location> triggerLocs = new ArrayList<Location>();
			ArrayList<Location> nonMultiTriggerLocs = new ArrayList<Location>();
			Map<Location, Location> bankomatOfTrigger = new HashMap<Location, Location>();
			Map<Location, BankomatType> nonMultiType = new HashMap<Location, BankomatType>();
			while(rs.next()){
				int locX = rs.getInt("LocationX");
				int locY = rs.getInt("LocationY");
				int locZ = rs.getInt("LocationZ");
				boolean nonMulti = !rs.getBoolean("Multi");
				World world = Bukkit.getWorld(rs.getString("LocationWorld"));
				if(rs.getString("Pos").toLowerCase().equals("up")){
					Location trigger = new Location(world, locX, locY+2, locZ);
					triggerLocs.add(trigger);
					if(nonMulti){
						nonMultiTriggerLocs.add(trigger);
						nonMultiType.put(trigger, BankomatType.valueOf(rs.getString("Type")));
					}
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
				}
				if(rs.getString("Pos").toLowerCase().equals("down")){
					Location trigger = new Location(world, locX, locY-1, locZ);
					triggerLocs.add(trigger);
					if(nonMulti){
						nonMultiTriggerLocs.add(trigger);
						nonMultiType.put(trigger, BankomatType.valueOf(rs.getString("Type")));
					}
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
				}
				if(rs.getString("Pos").toLowerCase().equals("2x2d")){
						Location trigger = new Location(world, locX, locY-1, locZ);
						triggerLocs.add(trigger);
						if(nonMulti){
							nonMultiTriggerLocs.add(trigger);
							nonMultiType.put(trigger, BankomatType.valueOf(rs.getString("Type")));
						}
						bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
						trigger = new Location(world, locX+1, locY-1, locZ);
						triggerLocs.add(trigger);
						if(nonMulti){
							nonMultiTriggerLocs.add(trigger);
							nonMultiType.put(trigger, BankomatType.valueOf(rs.getString("Type")));
						}
						bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
						trigger = new Location(world, locX, locY-1, locZ+1);
						triggerLocs.add(trigger);
						if(nonMulti){
							nonMultiTriggerLocs.add(trigger);
							nonMultiType.put(trigger, BankomatType.valueOf(rs.getString("Type")));
						}
						bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
						trigger = new Location(world, locX+1, locY-1, locZ+1);
						triggerLocs.add(trigger);
						if(nonMulti){
							nonMultiTriggerLocs.add(trigger);
							nonMultiType.put(trigger, BankomatType.valueOf(rs.getString("Type")));
						}
						bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
				}
				if(rs.getString("Pos").toLowerCase().equals("2x2u")){
					Location trigger = new Location(world, locX, locY+2, locZ);
					triggerLocs.add(trigger);
					if(nonMulti){
						nonMultiTriggerLocs.add(trigger);
						nonMultiType.put(trigger, BankomatType.valueOf(rs.getString("Type")));
					}
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
					trigger = new Location(world, locX+1, locY+2, locZ);
					triggerLocs.add(trigger);
					if(nonMulti){
						nonMultiTriggerLocs.add(trigger);
						nonMultiType.put(trigger, BankomatType.valueOf(rs.getString("Type")));
					}
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
					trigger = new Location(world, locX, locY+2, locZ+1);
					triggerLocs.add(trigger);
					if(nonMulti){
						nonMultiTriggerLocs.add(trigger);
						nonMultiType.put(trigger, BankomatType.valueOf(rs.getString("Type")));
					}
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
					trigger = new Location(world, locX+1, locY+2, locZ+1);
					triggerLocs.add(trigger);
					if(nonMulti){
						nonMultiTriggerLocs.add(trigger);
						nonMultiType.put(trigger, BankomatType.valueOf(rs.getString("Type")));
					}
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
			}
			}
			BankomatTriggers.update(triggerLocs, nonMultiTriggerLocs, bankomatOfTrigger, nonMultiType);
			return true;
			
		} catch (SQLException e) {
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Select * from " + Config.BankomatsTable());
			return false;
		}
	}
	public static boolean removeBankomat(Location loc){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("Delete from " + Config.BankomatsTable() + " where LocationX=" + loc.getBlockX() + " AND LocationY =" + loc.getBlockY() + " AND LocationZ=" + loc.getBlockZ());
			
			RubinBank.updateBankomatLocs();
			return true;
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString()+ "Query: Delete from " + Config.BankomatsTable() + " where LocationX=" + loc.getBlockX() + " AND LocationY =" + loc.getBlockY() + " AND LocationZ=" + loc.getBlockZ());
			return false;
		}
	}
	public static void addTriggerButton(Location loc, String type){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("Insert into " +  Config.ButtonsTable() + " (LocationX, LocationY, LocationZ, Location World, Type) values(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld().getName() + "\", \"" + type +"\")");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Error: Insert into " +  Config.ButtonsTable() + " (LocationX, LocationY, LocationZ, Location World, Type) values(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld().getName() + "\", \"" + type +"\")");
			RubinBank.log.severe(e.toString());
		}
	}
	public static void updateTriggerButtons(){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("Select * from " +  Config.ButtonsTable());
			
			ArrayList<Location> triggerButtons = new ArrayList<Location>();
			Map<Location, TriggerButtonType> triggerButtonType = new HashMap<Location, TriggerButtonType>();
			while(rs.next()){
				int locX = rs.getInt("LocationX");
				int locY = rs.getInt("LocationY");
				int locZ = rs.getInt("LocationZ");
				World world = Bukkit.getWorld(rs.getString("LocationWorld"));
				TriggerButtonType type = TriggerButtonType.valueOf(rs.getString("Type"));
				Location loc = new Location(world, locX, locY, locZ);
				triggerButtons.add(loc);
				triggerButtonType.put(loc, type);
			}
			TriggerButton.updateTriggerButtons(triggerButtons, triggerButtonType);
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Error: Select * from " +  Config.ButtonsTable());
			RubinBank.log.severe(e.toString());
		}
	}
	public static boolean removeTriggerButton(Location loc){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("Delete from " + Config.ButtonsTable() + " where LocationX=" + loc.getBlockX() + " AND LocationY =" + loc.getBlockY() + " AND LocationZ=" + loc.getBlockZ());
			
			updateTriggerButtons();
			return true;
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString()+ "\nQuery: Delete from " + Config.ButtonsTable() + " where LocationX=" + loc.getBlockX() + " AND LocationY =" + loc.getBlockY() + " AND LocationZ=" + loc.getBlockZ());
			return false;
		}
	}
	public static void accountAction(String p_n, String p_n2, AccountAction action, double amount){
		amount = ((int) amount * 10.0)/10.0;
		boolean p2_is_there = false;
		if(p_n2 != null){
			p2_is_there = true;
		}
		if(action.equals(AccountAction.IN)){
			try{
				RubinBank.getConnection().createStatement().executeUpdate("Update " + Config.AccountsTable() + " set amount=amount+" + Double.toString(amount) + " where user=\"" + p_n + "\"");
				insertAccountStatement(p_n, null, action, amount);
			} catch (SQLException e) {
				RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Update " + Config.AccountsTable() + " set amount=amount+" + Double.toString(amount) + " where user=\"" + p_n + "\"");
			}
		}
		if(action.equals(AccountAction.OUT)){
			try{
				RubinBank.getConnection().createStatement().executeUpdate("Update " + Config.AccountsTable() + " set amount=amount-" + Double.toString(amount) + " where user=\"" + p_n + "\"");
				insertAccountStatement(p_n, null, action, amount);
			} catch (SQLException e) {
				RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Update " + Config.AccountsTable() + " set amount=amount-" + Double.toString(amount) + " where user=\"" + p_n + "\"");
			}
		}
		if(action.equals(AccountAction.TRANSFER) || p2_is_there){
			if(Account.hasEnughMoney(p_n, amount)){
				boolean error_is_1 = true;
				try{
					Statement stmt = RubinBank.getConnection().createStatement();
					
					stmt.executeUpdate("Update " + Config.AccountsTable() + " set amount=amount-" + Double.toString(amount) + " where user=\"" + p_n + "\"");
					error_is_1 = false;
					stmt.executeUpdate("Update " + Config.AccountsTable() + " set amount=amount+" + Double.toString(amount) + " where user=\"" + p_n2 + "\"");
					
					insertAccountStatement(p_n, p_n2, AccountAction.TRANSFER_OUT, amount);
					insertAccountStatement(p_n2, p_n, AccountAction.TRANSFER_IN, amount);
				} catch (SQLException e) {
					if(error_is_1)
						RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Update " + Config.AccountsTable() + " set amount=amount+" + Double.toString(amount) + " where user=\"" + p_n + "\"");
					else
						RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Update " + Config.AccountsTable() + " set amount=amount-" + Double.toString(amount) + " where user=\"" + p_n2 + "\"");
				}
			}
			else{
				Tools.msg(p_n, ChatColor.YELLOW + "Du hast nicht genug Geld!");
			}
		}
		if(action.equals(AccountAction.CREATE)){
			try{
				RubinBank.getConnection().createStatement().executeUpdate("Insert into " + Config.AccountsTable() + " (user, account, amount) values(\"" + p_n + "\", 1, 0) ");
			} catch (SQLException e) {
				RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Insert into " + Config.AccountsTable() + " (user, account, amount) values(\"" + p_n + "\", 1, 0) ");
			}
		}
	}
	public static void insertAccountStatement(String p_n, String p_n2, AccountAction action, double amount){
		if(p_n2 == null)
			p_n2 = "NONE";
		double newamount = Account.getAccountAmount(p_n);
		try{
			RubinBank.getConnection().createStatement().executeUpdate("Insert into " + Config.ActionsTable() + " (user, Action, user2, ActionAmount, newamount, Date) values(\"" + p_n + "\", \"" + action.toString() + "\", \"" + p_n2 + "\", " + Double.toString(amount) + ", " + Double.toString(newamount) + ", NOW())");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Insert into " + Config.ActionsTable() + " (user, Action, user2, ActionAmount, newamount, Date) values(\"" + p_n + "\", \"" + action.toString() + "\", \"" + p_n2 + "\", " + Double.toString(amount) + ", " + Double.toString(newamount) + ", NOW())");
			return;
		}
	}
}

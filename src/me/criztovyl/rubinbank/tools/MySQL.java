package me.criztovyl.rubinbank.tools;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.config.Config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;



public class MySQL{
	public static void addPlayer(Player p){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("insert into "+Config.UsersTable()+" values(default, '"+p.getName()+"', default, default, now())");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "Query: Insert into "+Config.UsersTable()+" values(default, '"+p.getName()+"', default, default, now())");
		}
	}
	public static void updateLastLogin(Player p){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("update "+Config.UsersTable()+" set lastlogin=now() where user='"+p.getName()+"'");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "Query: Update "+Config.UsersTable()+" set lastlogin=now() where user='"+p.getName()+"'");
		}
	}
	public static String[] getLastLogins(){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("select user, lastlogin from "+Config.UsersTable());
			
			ArrayList<String> logs = new ArrayList<String>();
			
			while(rs.next()){
				String user = rs.getString("user");
				Date date = rs.getDate("lastlogin");
				logs.add(user+": "+date.toString()+"\n");
			}
			String[] logsO = new String[logs.size()];
			for(int i = 0; i < logs.size(); i++){
				logsO[i] = logs.get(i);
			}
			return logsO;
		} catch(SQLException e){
			RubinBank.log.severe("SQL Exception:\n" + e.toString() + "Query: Select user, lastlogin from "+Config.UsersTable());
			return null;
		}
	}
	public static boolean isInDB(Player p){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("select * from "+Config.UsersTable());
			
			while(rs.next()){
				if(rs.getString("user").equals(p.getName())){
					return true;
				}
			}
		} catch (SQLException e) {
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "Query: Select * from " + Config.UsersTable());
			return true;
		}
		return false;
	}
	public static void insertBankomat(Location loc, String pos){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("Insert into " + Config.BankomatsTable() + " values(default, " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld().getName() + "\", \"" + pos + "\")");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "Query: " +
					"Insert into " + Config.BankomatsTable() + " values(default, " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld().getName() + "\", \"" + pos +"\")");
		}
		RubinBank.updateBankomatLocs();
	}
	public static boolean updateTriggers(){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("Select * from " + Config.BankomatsTable());
			
			ArrayList<Location> triggerLocs = new ArrayList<Location>();
			Map<Location, Location> bankomatOfTrigger = new HashMap<Location, Location>();
			while(rs.next()){
				int locX = rs.getInt("LocationX");
				int locY = rs.getInt("LocationY");
				int locZ = rs.getInt("LocationZ");
				World world = Bukkit.getWorld(rs.getString("LocationWorld"));
				if(rs.getString("Pos").toLowerCase().equals("up")){
					Location trigger = new Location(world, locX, locY+2, locZ);
					triggerLocs.add(trigger);
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
				}
				if(rs.getString("Pos").toLowerCase().equals("down")){
					Location trigger = new Location(world, locX, locY-1, locZ);
					triggerLocs.add(trigger);
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
				}
				if(rs.getString("Pos").toLowerCase().equals("2x2d")){
						Location trigger = new Location(world, locX, locY-1, locZ);
						triggerLocs.add(trigger);
						bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
						trigger = new Location(world, locX+1, locY-1, locZ);
						triggerLocs.add(trigger);
						bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
						trigger = new Location(world, locX, locY-1, locZ+1);
						triggerLocs.add(trigger);
						bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
						trigger = new Location(world, locX+1, locY-1, locZ+1);
						triggerLocs.add(trigger);
						bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
				}
				if(rs.getString("Pos").toLowerCase().equals("2x2u")){
					Location trigger = new Location(world, locX, locY+2, locZ);
					triggerLocs.add(trigger);
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
					trigger = new Location(world, locX+1, locY+2, locZ);
					triggerLocs.add(trigger);
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
					trigger = new Location(world, locX, locY+2, locZ+1);
					triggerLocs.add(trigger);
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
					trigger = new Location(world, locX+1, locY+2, locZ+1);
					triggerLocs.add(trigger);
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
			}
			}
			BankomatTriggers.update(triggerLocs, bankomatOfTrigger);
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
			
			stmt.executeUpdate("Insert into " +  Config.ButtonsTable() + " values(default, " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld().getName() + "\", \"" + type +"\")");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Error: Insert into " +  Config.ButtonsTable() + " values(default, " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld() + "\", \"" + type +"\")");
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
	public static void accountAction(Player p, Player p2, AccountAction action, double amount){
		boolean p2_is_there = false;
		if(p2 != null){
			p2_is_there = true;
		}
		if(action.equals(AccountAction.IN)){
			try{
				RubinBank.getConnection().createStatement().executeQuery("Update " + Config.UsersTable() + " set amount=amount+" + amount + " where user=\"" + p.getName() + "\"");
				insertAccountStatement(p, null, action, amount);
			} catch (SQLException e) {
				RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Update " + Config.UsersTable() + " set amount=amount+" + amount + " where user=\"" + p.getName() + "\"");
			}
		}
		if(action.equals(AccountAction.OUT)){
			try{
				RubinBank.getConnection().createStatement().executeQuery("Update " + Config.UsersTable() + " set amount=amount-" + amount + " where user=\"" + p.getName() + "\"");
				insertAccountStatement(p, null, action, amount);
			} catch (SQLException e) {
				RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Update " + Config.UsersTable() + " set amount=amount-" + amount + " where user=\"" + p.getName() + "\"");
			}
		}
		if(action.equals(AccountAction.TRANSFER_IN) || p2_is_there){
			boolean error_is_1 = true;
			try{
				Statement stmt = RubinBank.getConnection().createStatement();
				
				stmt.executeQuery("Update " + Config.UsersTable() + " set amount=amount+" + amount + " where user=\"" + p.getName() + "\"");
				error_is_1 = false;
				stmt.executeQuery("Update " + Config.UsersTable() + " set amount=amount-" + amount + " where user=\"" + p2.getName() + "\"");
				
				insertAccountStatement(p, p2, action, amount);
				insertAccountStatement(p2, p, action.opposite(action), amount);
			} catch (SQLException e) {
				if(error_is_1)
					RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Update " + Config.UsersTable() + " set amount=amount+" + amount + " where user=\"" + p.getName() + "\"");
				else
					RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Update " + Config.UsersTable() + " set amount=amount-" + amount + " where user=\"" + p2.getName() + "\"");
			}
		}
		if(action.equals(AccountAction.TRANSFER_OUT) || p2_is_there){
			boolean error_is_1 = true;
			try{
				Statement stmt = RubinBank.getConnection().createStatement();
				
				stmt.executeQuery("Update " + Config.UsersTable() + " set amount=amount-" + amount + " where user=\"" + p.getName() + "\"");
				error_is_1 = false;
				stmt.executeQuery("Update " + Config.UsersTable() + " set amount=amount+" + amount + " where user=\"" + p2.getName() + "\"");
				
				insertAccountStatement(p, p2, action, amount);
				insertAccountStatement(p2, p, action.opposite(action), amount);
			} catch (SQLException e) {
				if(error_is_1)
					RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Update " + Config.UsersTable() + " set amount=amount+" + amount + " where user=\"" + p.getName() + "\"");
				else
					RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Update " + Config.UsersTable() + " set amount=amount-" + amount + " where user=\"" + p2.getName() + "\"");
			}
		}
	}
	public static void insertAccountStatement(Player p, Player p2, AccountAction action, double amount){
		try{
			RubinBank.getConnection().createStatement().executeUpdate("Insert into " + Config.ActionsTable() + " values(default, \"" + p.getName() + "\", \"" + action.toString() + "\", \"" + p2.getName() + "\", " + amount + ")");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString());
			return;
		}
	}
}

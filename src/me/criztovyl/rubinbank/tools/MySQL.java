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
			
			stmt.executeUpdate("insert into "+Config.DataBaseAndTable()+" values(default, '"+p.getName()+"', default, default, now())");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "Query: Insert into "+Config.DataBaseAndTable()+" values(default, '"+p.getName()+"', default, default, now())");
		}
	}
	public static void updateLastLogin(Player p){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("update "+Config.DataBaseAndTable()+" set lastlogin=now() where user='"+p.getName()+"'");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "Query: Update "+Config.DataBaseAndTable()+" set lastlogin=now() where user='"+p.getName()+"'");
		}
	}
	public static String[] getLastLogins(){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("select user, lastlogin from "+Config.DataBaseAndTable());
			
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
			RubinBank.log.severe("SQL Exception:\n" + e.toString() + "Query: Select user, lastlogin from "+Config.DataBaseAndTable());
			return null;
		}
	}
	public static boolean isInDB(Player p){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("select * from "+Config.DataBaseAndTable());
			
			while(rs.next()){
				if(rs.getString("user").equals(p.getName())){
					return true;
				}
			}
		} catch (SQLException e) {
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "Query: Select * from " + Config.DataBaseAndTable());
			return true;
		}
		return false;
	}
	public static void insertBankomat(Location loc, String type, String pos){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("Insert into " + Config.DataBaseAndTable2() + " values(default, " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld().getName() + "\", \"" + type + "\", \"" + pos + "\")");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "Query: " +
					"Insert into " + Config.DataBaseAndTable2() + " values(default, " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld().getName() + "\", \"" + type + "\", \"" + pos +"\")");
		}
		RubinBank.updateBankomatLocs();
	}
	public static boolean updateTriggers(){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("Select * from " + Config.DataBaseAndTable2());
			
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
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: Select * from " + Config.DataBaseAndTable2());
			return false;
		}
	}
	public static boolean removeBankomat(Location loc){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("Delete from " + Config.DataBaseAndTable2() + " where LocationX=" + loc.getBlockX() + " AND LocationY =" + loc.getBlockY() + " AND LocationZ=" + loc.getBlockZ());
			
			RubinBank.updateBankomatLocs();
			return true;
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString()+ "Query: Delete from " + Config.DataBaseAndTable2() + " where LocationX=" + loc.getBlockX() + " AND LocationY =" + loc.getBlockY() + " AND LocationZ=" + loc.getBlockZ());
			return false;
		}
	}
	public static void addTriggerButton(Location loc, String type){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("Insert into " +  Config.DataBaseAndTable3() + " values(default, " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld().getName() + "\", \"" + type +"\")");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Error: Insert into " +  Config.DataBaseAndTable3() + " values(default, " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", \"" + loc.getWorld() + "\", \"" + type +"\")");
			RubinBank.log.severe(e.toString());
		}
	}
	public static void updateTriggerButtons(){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			ResultSet rs = stmt.executeQuery("Select * from " +  Config.DataBaseAndTable3());
			
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
			RubinBank.log.severe("MySQL Error: Select * from " +  Config.DataBaseAndTable3());
			RubinBank.log.severe(e.toString());
		}
	}
	public static boolean removeTriggerButton(Location loc){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("Delete from " + Config.DataBaseAndTable3() + " where LocationX=" + loc.getBlockX() + " AND LocationY =" + loc.getBlockY() + " AND LocationZ=" + loc.getBlockZ());
			
			updateTriggerButtons();
			return true;
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n" + e.toString()+ "Query: Delete from " + Config.DataBaseAndTable3() + " where LocationX=" + loc.getBlockX() + " AND LocationY =" + loc.getBlockY() + " AND LocationZ=" + loc.getBlockZ());
			return false;
		}
	}
}

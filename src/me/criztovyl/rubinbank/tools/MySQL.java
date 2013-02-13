package me.criztovyl.rubinbank.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.criztovyl.clicklesssigns.ClicklessSigns;
import me.criztovyl.clicklesssigns.ClicklessSigns.SignPos;
import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.account.Account;
import me.criztovyl.rubinbank.account.Account.AccountStatement;
import me.criztovyl.rubinbank.config.Config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;



public class MySQL{
	private static Connection con;
	public static void insertBankomat(Location loc, String pos, String location){
		RubinBank.log.info("Inserted Bankomat");
		String query = String.format("INSERT INTO %s (LocationX, LocationY, LocationZ, LocationWorld, Pos, Location) values(%d, %d, %d, '%s', '%s', '%s')",
				Config.BankomatsTable(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), pos.toString(), location);
		con = RubinBank.getCon();
		try{
			con.createStatement().executeUpdate(query);
			con.close();
		}
		catch(SQLException e){
			RubinBank.log.severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
		}
		updateTriggers();
	}
	public static void insertBankomat(String locX, String locY, String locZ, String locWorld, String pos, String location){
		RubinBank.log.info("Inserted Bankomat");
		String query = String.format("INSERT INTO %s (LocationX, LocationY, LocationZ, LocationWorld, Pos, Location) values(%s, %s, %s, '%s', '%s', '%s')",
				Config.BankomatsTable(), locX, locY, locZ,  locWorld, pos.toString(), location);
		try{
			con.createStatement().executeUpdate(query);
			con.close();
		}
		catch(SQLException e){
			RubinBank.log.severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
		}
		updateTriggers();
	}
	public static void insertnoMultiBankomat(Location loc, String pos, SignType type, String location){
		String query = String.format("INSERT INTO %s (LocationX, LocationY, LocationZ, LocationWorld, Pos, Location, Type, Multi) values(%d, %d, %d, '%s', '%s', '%s', '%s', 0)",
				Config.BankomatsTable(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), pos.toString(), location, type.toString());
		con = RubinBank.getCon();
		try{
			con.createStatement().executeUpdate(query);
			con.close();
		}
		catch(SQLException e){
			RubinBank.log.severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
		}
		updateTriggers();
	}
	public static void insertnoMultiBankomat(String locX, String locY, String locZ, String locWorld, String pos, String type, String location){
		RubinBank.log.info("Inserted Bankomat");
		String query = String.format("INSERT INTO %s (LocationX, LocationY, LocationZ, LocationWorld, Pos, Location, Type, Multi) values(%s, %s, %S, '%s', '%s', '%s', '%s', 0)",
				Config.BankomatsTable(), locX, locY, locZ,  locWorld, pos.toString(), location, type.toString());
		con = RubinBank.getCon();
		try{
			con.createStatement().executeUpdate(query);
			con.close();
		}
		catch(SQLException e){
			RubinBank.log.severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
		}
		updateTriggers();
	}
	public static boolean updateTriggers(){
		String query = String.format("SELECT * from %s", Config.BankomatsTable());
		con = RubinBank.getCon();
		try{
			ResultSet rs = con.createStatement().executeQuery(query);
			
			while(rs.next()){
				int locX = rs.getInt("LocationX");
				int locY = rs.getInt("LocationY");
				int locZ = rs.getInt("LocationZ");
				boolean nonMulti = !rs.getBoolean("Multi");
				String Type = rs.getString("Type");
				World world = Bukkit.getWorld(rs.getString("LocationWorld"));
				if(rs.getString("Pos").toLowerCase().equals("up")){
					Location trigger = new Location(world, locX, locY, locZ);
					if(nonMulti){
						Signs.addSign(trigger, SignType.valueOf(Type), SignPos.UP);
					}
					else{
						Signs.addSign(trigger, SignType.CHOOSING, SignPos.UP);
					}
				}
				if(rs.getString("Pos").toLowerCase().equals("down")){
					Location trigger = new Location(world, locX, locY, locZ);
					if(nonMulti){
						Signs.addSign(trigger, SignType.valueOf(Type), SignPos.DOWN);
					}
					else{
						Signs.addSign(trigger, SignType.CHOOSING, SignPos.DOWN);
					}
				}
				/*
				if(rs.getString("Pos").toLowerCase().equals("2x2d")){
						Location trigger = new Location(world, locX, locY-1, locZ);
						triggerLocs.add(trigger);
						if(nonMulti){
							nonMultiTriggerLocs.add(trigger);
							nonMultiType.put(trigger, SignType.valueOf(rs.getString("Type")));
						}
						bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
						trigger = new Location(world, locX+1, locY-1, locZ);
						triggerLocs.add(trigger);
						if(nonMulti){
							nonMultiTriggerLocs.add(trigger);
							nonMultiType.put(trigger, SignType.valueOf(rs.getString("Type")));
						}
						bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
						trigger = new Location(world, locX, locY-1, locZ+1);
						triggerLocs.add(trigger);
						if(nonMulti){
							nonMultiTriggerLocs.add(trigger);
							nonMultiType.put(trigger, SignType.valueOf(rs.getString("Type")));
						}
						bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
						trigger = new Location(world, locX+1, locY-1, locZ+1);
						triggerLocs.add(trigger);
						if(nonMulti){
							nonMultiTriggerLocs.add(trigger);
							nonMultiType.put(trigger, SignType.valueOf(rs.getString("Type")));
						}
						bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
				}
				if(rs.getString("Pos").toLowerCase().equals("2x2u")){
					Location trigger = new Location(world, locX, locY+2, locZ);
					triggerLocs.add(trigger);
					if(nonMulti){
						nonMultiTriggerLocs.add(trigger);
						nonMultiType.put(trigger, SignType.valueOf(rs.getString("Type")));
					}
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
					trigger = new Location(world, locX+1, locY+2, locZ);
					triggerLocs.add(trigger);
					if(nonMulti){
						nonMultiTriggerLocs.add(trigger);
						nonMultiType.put(trigger, SignType.valueOf(rs.getString("Type")));
					}
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
					trigger = new Location(world, locX, locY+2, locZ+1);
					triggerLocs.add(trigger);
					if(nonMulti){
						nonMultiTriggerLocs.add(trigger);
						nonMultiType.put(trigger, SignType.valueOf(rs.getString("Type")));
					}
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
					trigger = new Location(world, locX+1, locY+2, locZ+1);
					triggerLocs.add(trigger);
					if(nonMulti){
						nonMultiTriggerLocs.add(trigger);
						nonMultiType.put(trigger, SignType.valueOf(rs.getString("Type")));
					}
					bankomatOfTrigger.put(trigger, new Location(world, locX, locY, locZ));
			}*/
			}
			con.close();
			return true;
			
		} catch (SQLException e) {
			RubinBank.log.severe("MySQL Exception:\n" + e.toString() + "\nQuery: " + query);
			return false;
		}
	}
	public static boolean removeBankomat(Location loc){
		String query = String.format("Delete from %s where LocationX=%d AND LocationY=%d AND LocationZ=%d AND LocationWorld='%s'", Config.BankomatsTable(),
				loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
		con = RubinBank.getCon();
		try{
			con.createStatement().executeUpdate(query);
			con.close();
			ClicklessSigns.removeSign(loc);
			MySQL.updateTriggers();
			return true;
		}
		catch(SQLException e){
			RubinBank.log.severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
			return false;
		}
	}
	public static void addTriggerButton(Location loc, String type){
		String query = String.format("Insert into %s (LocationX, LocationY, LocationZ, LocationWorld, Type) values(%d, %d, %d, '%s', '%s')", Config.ButtonsTable(),
				loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), type.toString());
		con = RubinBank.getCon();
		try{
			con.createStatement().executeUpdate(query);
			con.close();
		}
		catch(SQLException e){
			RubinBank.log.severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
		}
	}
	public static void updateTriggerButtons(){
		String query = String.format("Select * from %s", Config.ButtonsTable());
		con = RubinBank.getCon();
		try{
			
			ResultSet rs = con.createStatement().executeQuery(query);
			
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
			con.close();
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Error: Select * from " +  Config.ButtonsTable());
			RubinBank.log.severe(e.toString());
		}
	}
	public static boolean removeTriggerButton(Location loc){
			String query = String.format("Delete from %s where LocationX=%d AND LocationY=%d AND LocationZ=%d AND LocationWorld='$s'", Config.BankomatsTable(),
					loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
			con = RubinBank.getCon();
			try{
				con.createStatement().executeUpdate(query);
				con.close();
				return true;
			}
			catch(SQLException e){
				RubinBank.log.severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
				return false;
			}
	}
//	public static void accountAction(String p_n, String p_n2, AccountAction action, double amount){
//		amount = ((int)(amount * 10.0)) / 10.0;
//		boolean p2_is_there = false;
//		if(p_n2 != null){
//			p2_is_there = true;
//		}
//		String query;
//		if(action.equals(AccountAction.IN)){
//			query = String.format("Update %s set amount=amount+%d where user='%s'", Config.AccountsTable(), amount, p_n);
//			RubinBank.getMySQL_().executeUpdate(query);
//			insertAccountStatement(p_n, null, action, amount);
//		}
//		if(action.equals(AccountAction.OUT)){
//			query = String.format("Update %s set amount=amount-%d where user='%s'", Config.AccountsTable(), amount, p_n);
//			RubinBank.getMySQL_().executeUpdate(query);
//			insertAccountStatement(p_n, null, action, amount);
//		}
//		if(action.equals(AccountAction.TRANSFER) || p2_is_there){
//			if(Account.hasEnughMoney(p_n, amount)){
//				query = String.format("Update %s set amount=amount-%d where user='%s'", Config.AccountsTable(), amount, p_n);
//				RubinBank.getMySQL_().executeUpdate(query);
//				query = String.format("Update %s set amount=amount+%d where user='%s'", Config.AccountsTable(), amount, p_n);
//				RubinBank.getMySQL_().executeUpdate(query);
//				insertAccountStatement(p_n, p_n2, AccountAction.TRANSFER_OUT, amount);
//				insertAccountStatement(p_n2, p_n, AccountAction.TRANSFER_IN, amount);
//			}
//			else{
//				Tools.msg(p_n, ChatColor.YELLOW + "Du hast nicht genug Geld!");
//			}
//		}
//		if(action.equals(AccountAction.CREATE)){
//			query = String.format("Insert Into %s (user, account, amount) values('%s', 1, 0)", Config.AccountsTable(), amount, p_n);
//			RubinBank.getMySQL_().executeUpdate(query);
//			insertAccountStatement(p_n, null, action, amount);
//		}
//	}
//	public static void insertAccountStatement(String p_n, String p_n2, AccountAction action, double amount){
//		if(p_n2 == null)
//			p_n2 = "NONE";
//		double newamount = Account.getAccountAmount(p_n);
//		String query = String.format("Insert into %s (user, Action, user2, ActionAmount, newamount, Date) value('%s', '%s', '%s', %d, %d, NOW())",
//				Config.ActionsTable(), p_n, action.toString(), p_n2, amount, newamount);
//		RubinBank.getMySQL_().executeUpdate(query);
//	}
	public static void writeAccountToDB(String owner){
		writeAccountToDB(RubinBank.getBank().getAccount(owner));
	}
	public static void writeAccountToDB(Account account){
		ArrayList<AccountStatement> stmts = account.getStatements();
		con = RubinBank.getCon();
		try{
			Statement stmt = con.createStatement();
			for(int i = 0; i < stmts.size(); i++){
				String owner = stmts.get(i).getOwner();
				String action = stmts.get(i).getType().toString();
				String participant = stmts.get(i).getParticipant();
				String actionamount = Double.toString(stmts.get(i).getActionAmount());
				String newbalance = Double.toString(stmts.get(i).getNewBalance());
				String query = String.format("Insert into %s (user, Action, user2, ActionAmount, newamount, Date) value('%s', %d, '%s', %d, %d, NOW())", 
						Config.StatementsTable(), owner, action, participant, actionamount, newbalance);
				stmt.executeUpdate(query);
			}
			String owner = account.getOwner();
			String balance = Double.toString(account.getBalance());
			stmt.executeUpdate(String.format("Update %s set amount=%d where user='%s'", Config.AccountsTable(), balance, owner));
			con.close();
		} catch (SQLException e) {
			RubinBank.log.severe("Accountc could not wrote to Database: " + e.toString());
		}
	}
	public static java.sql.Date StringToSQLDate(String s) {
		java.sql.Date sqlDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			sqlDate = new java.sql.Date(sdf.parse(s).getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqlDate;
	}
}

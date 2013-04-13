package me.criztovyl.rubinbank.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.criztovyl.clickless.ClicklessPlugin;
import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.account.Account;
import me.criztovyl.rubinbank.bankomat.BankomatType;
import me.criztovyl.rubinbank.bankomat.TriggerPosition;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.signs.Signs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;


@Deprecated
public class MySQL_old{
	private static Connection con;
	public static void insertBankomat(Location loc, String pos, String location){
		RubinBank.getHelper().info("Inserted Bankomat");
		String query = String.format("INSERT INTO %s (LocationX, LocationY, LocationZ, LocationWorld, Pos, Location) values(%d, %d, %d, '%s', '%s', '%s')",
				Config.BankomatsTable(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), pos.toString(), location);
		try{
			con = RubinBank.getHelper().getMySQLHelper().getConnection();
			con.createStatement().executeUpdate(query);
			con.close();
		}
		catch(SQLException e){
			RubinBank.getHelper().severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
		}
		updateTriggers();
	}
	public static void insertBankomat(String locX, String locY, String locZ, String locWorld, String pos, String location){
		String query = String.format("INSERT INTO %s (LocationX, LocationY, LocationZ, LocationWorld, Pos, Location) values(%s, %s, %s, '%s', '%s', '%s')",
				Config.BankomatsTable(), locX, locY, locZ,  locWorld, pos.toString(), location);
		try{
			con = RubinBank.getHelper().getMySQLHelper().getConnection();
			con.createStatement().executeUpdate(query);
			con.close();
		}
		catch(SQLException e){
			RubinBank.getHelper().severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
		}
		updateTriggers();
		RubinBank.getHelper().info("Inserted Bankomat");
	}
	public static void insertnoMultiBankomat(Location loc, String pos, BankomatType type, String location){
		String query = String.format("INSERT INTO %s (LocationX, LocationY, LocationZ, LocationWorld, Pos, Location, Type, Multi) values(%d, %d, %d, '%s', '%s', '%s', '%s', 0)",
				Config.BankomatsTable(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), pos.toString(), location, type.toString());

		try{
			con = RubinBank.getHelper().getMySQLHelper().getConnection();
			con.createStatement().executeUpdate(query);
			con.close();
		}
		catch(SQLException e){
			RubinBank.getHelper().severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
		}
		updateTriggers();
	}
	public static void insertnoMultiBankomat(String locX, String locY, String locZ, String locWorld, String pos, String type, String location){
		RubinBank.getHelper().info("Inserted Bankomat");
		String query = String.format("INSERT INTO %s (LocationX, LocationY, LocationZ, LocationWorld, Pos, Location, Type, Multi) values(%s, %s, %S, '%s', '%s', '%s', '%s', 0)",
				Config.BankomatsTable(), locX, locY, locZ,  locWorld, pos.toString(), location, type.toString());
		try{
			con = RubinBank.getHelper().getMySQLHelper().getConnection();
			con.createStatement().executeUpdate(query);
			con.close();
		}
		catch(SQLException e){
			RubinBank.getHelper().severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
		}
		updateTriggers();
	}
	public static boolean updateTriggers(){
		String query = String.format("SELECT * from %s", Config.BankomatsTable());
		try{
			con = RubinBank.getHelper().getMySQLHelper().getConnection();
			ResultSet rs = con.createStatement().executeQuery(query);
			
			while(rs.next()){
				int locX = rs.getInt("LocationX");
				int locY = rs.getInt("LocationY");
				int locZ = rs.getInt("LocationZ");
				boolean nonMulti = !rs.getBoolean("Multi");
				String Type = rs.getString("Type");
				World world = Bukkit.getWorld(rs.getString("LocationWorld"));
				String place = rs.getString("Place");
				if(rs.getString("Pos").toLowerCase().equals("up")){
					Location trigger = new Location(world, locX, locY, locZ);
					if(nonMulti){
						Signs.addSign(trigger, BankomatType.valueOf(Type), TriggerPosition.UP, place);
					}
					else{
						Signs.addSign(trigger, BankomatType.CHOOSING, TriggerPosition.UP, place);
					}
				}
				if(rs.getString("Pos").toLowerCase().equals("down")){
					Location trigger = new Location(world, locX, locY, locZ);
					if(nonMulti){
						Signs.addSign(trigger, BankomatType.valueOf(Type), TriggerPosition.DOWN, place );
					}
					else{
						Signs.addSign(trigger, BankomatType.CHOOSING, TriggerPosition.DOWN, place);
					}
				}
			}
			con.close();
			return true;
			
		} catch (SQLException e) {
			RubinBank.getHelper().severe("MySQL Exception:\n" + e.toString() + "\nQuery: " + query);
			return false;
		}
	}
	public static boolean removeBankomat(Location loc){
		String query = String.format("Delete from %s where LocationX=%d AND LocationY=%d AND LocationZ=%d AND LocationWorld='%s'", Config.BankomatsTable(),
				loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
		try{
			con = RubinBank.getHelper().getMySQLHelper().getConnection();
			con.createStatement().executeUpdate(query);
			con.close();
			ClicklessPlugin.getClickless().removeClicklessSign(loc);
			MySQL_old.updateTriggers();
			return true;
		}
		catch(SQLException e){
			RubinBank.getHelper().severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
			return false;
		}
	}
	public static void addTriggerButton(Location loc, String type){
		String query = String.format("Insert into %s (LocationX, LocationY, LocationZ, LocationWorld, Type) values(%d, %d, %d, '%s', '%s')", Config.ButtonsTable(),
				loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), type.toString());
		try{
			con = RubinBank.getHelper().getMySQLHelper().getConnection();
			con.createStatement().executeUpdate(query);
			con.close();
		}
		catch(SQLException e){
			RubinBank.getHelper().severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
		}
	}
	public static void updateTriggerButtons(){
		String query = String.format("Select * from %s", Config.ButtonsTable());
		try{
			con = RubinBank.getHelper().getMySQLHelper().getConnection();
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
			RubinBank.getHelper().severe("MySQL Error: Select * from " +  Config.ButtonsTable());
			RubinBank.getHelper().severe(e.toString());
		}
	}
	public static boolean removeTriggerButton(Location loc){
			String query = String.format("Delete from %s where LocationX=%d AND LocationY=%d AND LocationZ=%d AND LocationWorld='$s'", Config.BankomatsTable(),
					loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
			try{
				con = RubinBank.getHelper().getMySQLHelper().getConnection();
				con.createStatement().executeUpdate(query);
				con.close();
				return true;
			}
			catch(SQLException e){
				RubinBank.getHelper().severe(e.toString() + "\n @ MySQL.class query\"" + query + "\"");
				return false;
			}
	}
	public static void writeAccountToDB(String owner){
		writeAccountToDB(RubinBank.getHelper().getBank().getAccount(owner));
	}
	public static void writeAccountToDB(Account account){
		ArrayList<me.criztovyl.rubinbank.account.AccountStatement> stmts = account.getStatements();
		try{
			con = RubinBank.getHelper().getMySQLHelper().getConnection();
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
			RubinBank.getHelper().severe("Accountc could not wrote to Database: " + e.toString());
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

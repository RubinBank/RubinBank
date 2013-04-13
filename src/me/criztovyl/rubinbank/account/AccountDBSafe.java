package me.criztovyl.rubinbank.account;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.tools.DBSafe;
/**
 * A {@link DBSafe} for Accounts
 * @author criztovyl
 *
 */
public class AccountDBSafe implements DBSafe {
	@Override
	public void saveToDatabase(HashMap<String, String> save, Connection con) {
		String query = "";
		try{
			Statement stmt = con.createStatement();
			query = String.format("CREATE TABLE IF NOT EXISTS %s (" +
					"id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
					"owner varchar(20), " +
					"balance double)",
					Config.AccountsTable());
			stmt.executeUpdate(query);
			if(inDB(save.get("owner"), con)){
				query = String.format("Update %s set balance=%s where owner='%s'", 
						Config.AccountsTable(), 
						save.get("balance"), 
						save.get("owner"));
				RubinBank.getHelper().info("[AccDBSafe] Saving Account of " + save.get("owner"));
				stmt.executeUpdate(query);
			}
			else {
				query = String.format("INSERT INTO %s (owner, balance) values('%s', %s)", 
						Config.AccountsTable(), 
						save.get("owner"), 
						save.get("balance"));
				RubinBank.getHelper().info("[AccDBSafe] Inserting Account of " + save.get("owner"));
				stmt.executeUpdate(query);
			}
			con.close();
		} catch (SQLException e) {
			RubinBank.getHelper().severe("Failed to save Account to Database! Error:\n" + e.toString() + "\n@ Query \"" + query + "\"");
			e.printStackTrace();
		}
		
	}
	@Override
	public ArrayList<HashMap<String, String>> loadFromDatabase(Connection con) {
		String query = "";
		ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String,String>>();
		results.clear();
		try{
			Statement stmt = con.createStatement();
			query = String.format("CREATE TABLE IF NOT EXISTS %s (" +
					"id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
					"owner varchar(20), " +
					"balance double)", 
					Config.AccountsTable());
			stmt.executeUpdate(query);
			query = String.format("SELECT * FROM %s", Config.AccountsTable());
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				HashMap<String, String> result = new HashMap<String, String>();
				result.put("owner", rs.getString("owner"));
				result.put("balance", Double.toString(rs.getDouble("balance")));
				results.add(result);
			}
			con.close();
			return results;
		} catch(SQLException e){
			RubinBank.getHelper().severe("Failed to load Account(s) from Database! Error:\n" + e.toString() + "\n@ Query \"" + query + "\"");
			e.printStackTrace();
			return null;
		}
	}
	public boolean inDB(String owner, Connection con) throws SQLException{
		Statement stmt = RubinBank.getHelper().getMySQLHelper().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM " + Config.AccountsTable());
		while(rs.next()){
			if(rs.getString("owner").equals(owner)){
				return true;
			}
		}
		return false;
	}

}

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
 * A DBSafe for Accounts
 * @author criztovyl
 *
 */
public class AccountDBSafe implements DBSafe {

	@Override
	public void saveToDatabase(HashMap<String, String> save, Connection con) {
		String query = "";
		try{
			Statement stmt = con.createStatement();
			query = String.format("CREATE TABLE IF NOT EXISTS %s (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, owner varchar(20), balance double)", Config.AccountsTable());
			stmt.executeUpdate(query);
			query = String.format("SELECT * FROM %s", Config.AccountsTable());
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				if(rs.getString("owner").equals(save.get("owner"))){
					query = String.format("Update %s set balance=%s where owner='%s'", Config.AccountsTable(), save.get("balance"), save.get("owner"));
					RubinBank.log.info("[AccDBSafe] Saving \"" + query + "\"");
					stmt.executeUpdate(query);
					return;
				}
			}
			query = String.format("INSERT INTO %s (owner, balance) values('%s', %s)", Config.AccountsTable(), save.get("owner"), save.get("balance"));
			stmt.executeUpdate(query);
			con.close();
		} catch (SQLException e) {
			RubinBank.log.severe("Failed to save Account to Database! Error:\n" + e.toString() + "\n@ Query \"" + query + "\"");
		}
		
	}
	@Override
	public ArrayList<HashMap<String, String>> loadFromDatabase(Connection con) {
		String query = "";
		ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String,String>>();
		results.clear();
		try{
			Statement stmt = con.createStatement();
			query = String.format("CREATE TABLE IF NOT EXISTS %s (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, owner varchar(20), balance double)", Config.AccountsTable());
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
			RubinBank.log.severe("Failed to load Account(s) from Database! Error:\n" + e.toString() + "\n@ Query \"" + query + "\"");
			return null;
		}
	}

}

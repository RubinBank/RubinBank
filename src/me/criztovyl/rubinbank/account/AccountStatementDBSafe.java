package me.criztovyl.rubinbank.account;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.tools.DBSafe;
/**
 * A {@link DBSafe} for Account Statements
 * @author criztovyl
 *
 */
public class AccountStatementDBSafe implements DBSafe{
		@Override
		public void saveToDatabase(HashMap<String, String> save, Connection con) {
			String query = "";
			try{
				Statement stmt = con.createStatement();
				query = String.format("create table if not exists %s (" +
						"id int not null auto_increment primary key, " +
						"owner text not null, " +
						"action text not null," +
						" participant text, " +
						"actionAmount double, " +
						"newBalance double, " +
						"date text," +
						"place text)", 
						Config.StatementsTable());
				stmt.executeUpdate(query);
				query = String.format("Insert into %s (" +
						"owner, " +
						"action, " +
						"participant, " +
						"actionAmount, " +
						"newBalance, " +
						"date," +
						"place) " +
						"value('%s', '%s', '%s', '%s', '%s', '%s', '%s')", 
						Config.StatementsTable(), 
						save.get("owner"), 
						save.get("action"), 
						save.get("participant"), 
						save.get("actionAmount"), 
						save.get("newBalance"), 
						save.get("date"),
						save.get("place"));
				stmt.executeUpdate(query);
				con.close();
			} catch(SQLException e){
				RubinBank.getHelper().severe("Failed to save AccountStatement to the Database! Error:\n" + e.toString() + "\n@ Query \"" + query +"\"");
			}
		}

		/**
		 * @return null; AccountStatements are not loaded from the Database, not needed In-Game
		 */
		@Override
		public ArrayList<HashMap<String, String>> loadFromDatabase(Connection con) {
			return null;
		}
		public static void checkAndEdit(Connection con) throws SQLException{
			Statement stmt = RubinBank.getHelper().getMySQLHelper().getConnection().createStatement();
			Statement stmt2 = RubinBank.getHelper().getMySQLHelper().getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + Config.StatementsTable());
			ResultSetMetaData meta = rs.getMetaData();
			boolean hasPlace = false;
			for(int i = 1; i <= meta.getColumnCount(); i++){
				if(
					meta.getColumnName(i).equalsIgnoreCase("owner") ||
					meta.getColumnName(i).equalsIgnoreCase("action") ||
					meta.getColumnName(i).equalsIgnoreCase("participant") ||
					meta.getColumnName(i).equalsIgnoreCase("date")
				){
					stmt2.executeUpdate(String.format("ALTER TABLE %s MODIFY %s TEXT", Config.StatementsTable(),
							meta.getColumnName(i)));
				}
				if(meta.getColumnName(i).equalsIgnoreCase("place")){
					hasPlace = true;
				}
			}
			if(!hasPlace){
				stmt2.executeUpdate(String.format("ALTER TABLE %s ADD place TEXT", Config.StatementsTable()));
			}
		}
	}
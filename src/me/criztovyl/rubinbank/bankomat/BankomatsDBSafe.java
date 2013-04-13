package me.criztovyl.rubinbank.bankomat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.RubinBankHelper;
import me.criztovyl.rubinbank.config.Config;
import me.criztovyl.rubinbank.tools.DBSafe;

public class BankomatsDBSafe implements DBSafe {
	private Connection con;
	private RubinBankHelper helper;
	public BankomatsDBSafe() throws SQLException{
		helper = RubinBank.getHelper();
		con = helper.getMySQLHelper().getConnection();
		String query = String.format("CREATE TABLE IF NOT EXISTS %s (%s)",
				Config.BankomatsTable(),
				String.format("id %s, LocationX %s, LocationY %s, LocationZ %s, LocationWorld %s, Type %s, Pos %s, Place %s",
						"INT NOT NULL AUTO_INCREMENT PRIMARY KEY",
						"INT NOT NULL",
						"INT NOT NULL",
						"INT NOT NULL",
						"TEXT NOT NULL",
						"TEXT NOT NULL",
						"TEXT NOT NULL",
						"TEXT"));
		con.createStatement().executeUpdate(query);
	}
	@Override
	public ArrayList<HashMap<String, String>> loadFromDatabase(Connection con) throws SQLException {
		ArrayList<HashMap<String, String>> load = new ArrayList<HashMap<String,String>>();
		ResultSet rs = con.createStatement().executeQuery("SELECT * FROM " + Config.BankomatsTable());
		while(rs.next()){
			HashMap<String, String> args = new HashMap<String, String>();
			args = BankomatArgument.LOCX.saveArg(args, rs.getString("LocationX"));
			args = BankomatArgument.LOCY.saveArg(args, rs.getString("LocationY"));
			args = BankomatArgument.LOCZ.saveArg(args, rs.getString("LocationZ"));
			args = BankomatArgument.LOCWORLD.saveArg(args, rs.getString("LocationWorld"));
			args = BankomatArgument.PLACE.saveArg(args, rs.getString("Place"));
			args = BankomatArgument.TYPE.saveArg(args, rs.getString("Type"));
			args = BankomatArgument.POS.saveArg(args, rs.getString("Pos"));
			load.add(args);
		}
		return load;
	}

	@Override
	public void saveToDatabase(HashMap<String, String> save, Connection con) throws SQLException {
		Statement stmt = con.createStatement();
			String query = String.format("INSERT INTO %s (LocationX, LocationY, LocationZ, LocationWorld, Place, Type, Pos) " +
					"values( %s, %s, %s, '%s', '%s', '%s', '%s')",
					Config.BankomatsTable(),
					BankomatArgument.LOCX.getArg(save),
					BankomatArgument.LOCY.getArg(save),
					BankomatArgument.LOCZ.getArg(save),
					BankomatArgument.LOCWORLD.getArg(save),
					BankomatArgument.PLACE.getArg(save),
					BankomatArgument.TYPE.getArg(save),
					BankomatArgument.POS.getArg(save));
			stmt.executeUpdate(query);
	}
	public void checkAndEditTableCoulumnNames() throws SQLException{
		Statement stmt = RubinBank.getHelper().getMySQLHelper().getConnection().createStatement();
		Statement stmt2 = RubinBank.getHelper().getMySQLHelper().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM " + Config.BankomatsTable());
		ResultSetMetaData meta = rs.getMetaData();
		String query = "";
		for(int i = 1; i <= meta.getColumnCount(); i++){
			String name = meta.getColumnName(i);
			if(name.equalsIgnoreCase("Location")){
				query = String.format("ALTER TABLE %s CHANGE Location Place Text", Config.BankomatsTable());
				stmt2.executeUpdate(query);
			}
			else if(name.equalsIgnoreCase("Multi")){
				query = String.format("ALTER TABLE %s DROP Multi", Config.BankomatsTable());
				stmt2.executeUpdate(query);
			}
		}
	}
	public void checkAndEditTableColumnDefinitions() throws SQLException{
		Statement stmt = RubinBank.getHelper().getMySQLHelper().getConnection().createStatement();
		Statement stmt2 = RubinBank.getHelper().getMySQLHelper().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM " + Config.BankomatsTable());
		ResultSetMetaData meta = rs.getMetaData();
		String query = "";
		for(int i = 1; i <= meta.getColumnCount(); i++){
			if(
					meta.getColumnName(i).equalsIgnoreCase("LocationWorld") ||
					meta.getColumnName(i).equalsIgnoreCase("Pos") ||
					meta.getColumnName(i).equalsIgnoreCase("Type") ||
					meta.getColumnName(i).equalsIgnoreCase("Place")
				)
			{
				if(meta.getColumnType(i) == Types.VARCHAR){
					query = String.format("ALTER TABLE %s MODIFY %s TEXT", Config.BankomatsTable(), 
							meta.getColumnName(i));
					stmt2.executeUpdate(query);
				}
			}
		}
	}
}

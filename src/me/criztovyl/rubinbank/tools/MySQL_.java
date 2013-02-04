package me.criztovyl.rubinbank.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;

public class MySQL_{
	private Connection con;
	private String host;
	private String user;
	private String password;
	private boolean success;
	public MySQL_(String host, String user, String password) {
		try{
			this.host = host;
			this.user = user;
			this.password = password;
			con = DriverManager.getConnection("jdbc:mysql://"+host, user, password);
		} catch (SQLException e) {
			Bukkit.getLogger().severe("MySQL Exception:\n" + e.toString() + "\n@ Construct MySQL Object");
		}
	}
	public void executeUpdate(String update){
		try {
			if(con.isClosed()){
				reopenConnection();
			}
			con.createStatement().executeUpdate(update);
			con.close();
			success = true;
		} catch (SQLException e) {
			Bukkit.getLogger().severe("MySQL Exception:\n" + e.toString() + "\n@ Query:\n" + update);
			success = false;
		}
	}
	public ResultSet executeQuery(String query){
		ResultSet rs;
		try{
			if(con.isClosed()){
				reopenConnection();
			}
			rs = con.createStatement().executeQuery(query);
			success = true;
			return rs;
		} catch (SQLException e) {
			Bukkit.getLogger().severe("MySQL Exception:\n" + e.toString() + "\n@ Execute Query\n" + query);
			success = false;
			return null;
		}
	}
	private void reopenConnection() {
		try{
			this.con = DriverManager.getConnection("jdbc:mysql://"+host, user, password);
			success = true;
		} catch (SQLException e) {
			Bukkit.getLogger().severe("MySQL Exception:\n" + e.toString() + "\n@ Re-open Connection of MySQL Object");
			success = false;
		}
		
	}
	public boolean wasSuccess(){
		return success;
	}
}

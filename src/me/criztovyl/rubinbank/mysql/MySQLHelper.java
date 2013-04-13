package me.criztovyl.rubinbank.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLHelper {
	private Connection con;
	private String dburl, dbuser, dbpassword;
	public MySQLHelper(String dburl, String dbuser, String dbpass) throws SQLException{
		this.dburl = dburl;
		this.dbuser = dbuser;
		this.dbpassword = dbpass;
		con = DriverManager.getConnection(
				this.dburl,
				this.dbuser,
				this.dbpassword);
		con.close();
	}
	public Connection getConnection() throws SQLException{
		if(con.isClosed()){
			con = DriverManager.getConnection(
					dburl,
					dbuser,
					dbpassword);
		}
		return con;
	}
	public void closeCon() throws SQLException{
		con.close();
	}
	public void runSimpleQuery(String query) throws SQLException{
		con.createStatement().executeQuery(query);
	}
}

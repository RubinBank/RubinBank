package RubinBank.account;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

import RubinBank.RubinBank;

import java.sql.Connection;

import config.Config;

public class account {
	private static Player player;
	private static double Amount;
	public account(Player p){
		player = p;
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} catch(ClassNotFoundException e){
			RubinBank.log.severe("Database driver not found!");
		}
		try{
			String url = "jdbc:mysql://"+Config.HostAddress()+"/"+Config.HostDatabase()+"?user="+Config.HostUser()+"&password="+Config.HostPassword();
			Connection con = DriverManager.getConnection(url);
			
			Statement stmt = con.createStatement();
			
			ResultSet resultset = stmt.executeQuery("Select * from "+Config.HostDatabase()+"."+Config.HostTable() + "where player='"+player.getName()+"'");
			
			Amount = resultset.getDouble("Amount");
			
			stmt.close();
			
			con.close();
		} catch(SQLException e){
			RubinBank.log.severe("SQL Exception");
		}
	}
	public Player getPlayer(){
		return player;
	}
	public double getAmount(){
		return Amount;
	}
	public void incraseAccount(double i){
		Amount += i;
	}
	public void decraseAccount(double i){
		Amount -= i;
	}
	public void setAmount(double i){
		Amount = i;
	}
	/*public boolean accountExists(Player p){
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} catch(ClassNotFoundException e){
			RubinBank.log.severe("Treiberklasse nicht gefunden!");
		}
		try{
			Connection con = DriverManager.getConnection(RubinBank.getURL());
		}
		return true;
	}*/
}

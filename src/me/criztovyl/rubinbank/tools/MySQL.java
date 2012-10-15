package me.criztovyl.rubinbank.tools;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.config.Config;

import org.bukkit.entity.Player;



public class MySQL{
	public static void addPlayer(Player p){
		try{
			Connection con = DriverManager.getConnection(RubinBank.getURL());
			Statement stmt = con.createStatement();
			
			stmt.executeUpdate("insert into "+Config.DataBaseAndTable()+" values(default, '"+p.getName()+"', default, default, now())");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n"+e.toString());
		}
	}
	public static void updateLastLogin(Player p){
		try{
			Statement stmt = RubinBank.getConnection().createStatement();
			
			stmt.executeUpdate("update "+Config.DataBaseAndTable()+" set lastlogin=now() where user='"+p.getName()+"'");
		} catch(SQLException e){
			RubinBank.log.severe("MySQL Exception:\n"+e.toString());
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
			RubinBank.log.severe("SQL Exception:\n"+e.toString());
			return null;
		}
	}
}

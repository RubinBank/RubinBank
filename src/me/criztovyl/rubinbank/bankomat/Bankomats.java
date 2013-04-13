package me.criztovyl.rubinbank.bankomat;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;

import me.criztovyl.rubinbank.RubinBank;
import me.criztovyl.rubinbank.config.Config;

public class Bankomats {
	ArrayList<Bankomat> bankomats;
	BankomatsDBSafe safe;
	public Bankomats() throws SQLException{
		bankomats = new ArrayList<Bankomat>();
		safe = new BankomatsDBSafe();
		safe.checkAndEditTableCoulumnNames();
		safe.checkAndEditTableColumnDefinitions();
	}
	public void load() throws SQLException{
		ArrayList<HashMap<String, String>> load = safe.loadFromDatabase(RubinBank.getHelper().getMySQLHelper().getConnection());
		if(load != null)
		for(int i = 0; i < load.size(); i++){
			bankomats.add(new Bankomat(load.get(i)));
			bankomats.get(bankomats.size()-1).createSign();
		}
		else
			RubinBank.getHelper().warning("No Bankomats in Database...");
	}
	public void save() throws SQLException{
		for(int i = 0; i < bankomats.size(); i++){
			if(bankomats.get(i).isNew())
			safe.saveToDatabase(bankomats.get(i).getArgs(), RubinBank.getHelper().getMySQLHelper().getConnection());
		}
	}
	public void addBankomat(Bankomat bankomat){
		bankomats.add(bankomat);
		bankomats.get(bankomats.size()-1).createSign();
	}
	public void removeBankomatByLocation(Location loc){
		for(int i = 0; i < bankomats.size(); i++){
			if(bankomats.get(i).getLoc().equals(loc)){
				bankomats.get(i).removeSign();
				String query = "";
				try {
					Statement stmt = RubinBank.getHelper().getMySQLHelper().getConnection().createStatement();
					query = String.format(
							"DELETE FROM %s WHERE LocationX=%s AND LocationY=%s AND LocationZ=%s AND LocationWorld='%s'",
							Config.BankomatsTable(),
							Integer.toString(loc.getBlockX()),
							Integer.toString(loc.getBlockY()),
							Integer.toString(loc.getBlockZ()),
							loc.getWorld().getUID().toString()); 
					stmt.executeUpdate(query);
				} catch (SQLException e) {
					RubinBank.getHelper().severe("MySQL Error @ Remove Bankomat:\n" + e.toString() + "\n@Query:\n" + query);
				}
				
				bankomats.remove(i);
				i--;
			}
		}
		RubinBank.getHelper().info("Removed Sign...");
	}
}

package me.criztovyl.rubinbank.bankomat;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import me.criztovyl.rubinbank.RubinBankPlugin;
import me.criztovyl.rubinbank.mysql.Reopenable;

import org.bukkit.Location;
/**
 * A Collection of Bankomats
 * @author criztovyl
 *
 */
public class Bankomats {
        private ArrayList<Bankomat> bankomats;
        private BankomatsDBSafe safe;
        private Reopenable reopenable;
        private String table;
        /**
         * Create a new Collection
         * @param reopenable a reopenable Database Connection
         * @param table the table the bankomats should be read from
         * @throws SQLException
         */
        public Bankomats(Reopenable reopenable, String table) throws SQLException{
            this.reopenable = reopenable;
            this.table = table;
            bankomats = new ArrayList<Bankomat>();
            safe = new BankomatsDBSafe(reopenable, table);
            safe.checkAndEditTableCoulumnNames();
            safe.checkAndEditTableColumnDefinitions();
        }
        /**
         * Load all Bankomats from the Database
         * @throws SQLException
         */
        public void load() throws SQLException{
                ArrayList<HashMap<String, String>> load = safe.loadFromDatabase();
                if(load != null)
                for(int i = 0; i < load.size(); i++){
                        bankomats.add(new Bankomat(load.get(i)));
                        bankomats.get(bankomats.size()-1).createSign();
                }
                else
                        RubinBankPlugin.getHelper().warning("No Bankomats in Database...");
        }
        /**
         * Save all Bankomats to the Database
         * @throws SQLException
         */
        public void save() throws SQLException{
                for(int i = 0; i < bankomats.size(); i++){
                        if(bankomats.get(i).isNew())
                        safe.saveToDatabase(bankomats.get(i).getArgs());
                }
        }
        /**
         * Add an Bankomat
         * @param bankomat a new Bankomat
         */
        public void addBankomat(Bankomat bankomat){
                bankomats.add(bankomat);
                bankomats.get(bankomats.size()-1).createSign();
        }
        /**
         * Remove a Bankomat by his Location
         * @param loc the Location of the Bankomat
         */
        public void removeBankomatByLocation(Location loc){
                for(int i = 0; i < bankomats.size(); i++){
                        if(bankomats.get(i).getLoc().equals(loc)){
                                bankomats.get(i).removeSign();
                                String query = "";
                                try {
                                        Connection con = reopenable.getDatabaseConnection();
                                        Statement stmt = con.createStatement();
                                        query = String.format(
                                                        "DELETE FROM %s WHERE LocationX=%s AND LocationY=%s AND LocationZ=%s AND LocationWorld='%s'",
                                                        table,
                                                        Integer.toString(loc.getBlockX()),
                                                        Integer.toString(loc.getBlockY()),
                                                        Integer.toString(loc.getBlockZ()),
                                                        loc.getWorld().getUID().toString()); 
                                        stmt.executeUpdate(query);
                                        con.close();
                                } catch (SQLException e) {
                                        RubinBankPlugin.getHelper().severe("MySQL Error @ Remove Bankomat:\n" + e.toString() + "\n@Query:\n" + query);
                                }
                                
                                bankomats.remove(i);
                                i--;
                        }
                }
                RubinBankPlugin.getHelper().info("Removed Sign...");
        }
}

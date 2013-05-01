package me.criztovyl.rubinbank.account;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import me.criztovyl.rubinbank.RubinBankPlugin;
import me.criztovyl.rubinbank.mysql.Reopenable;
import me.criztovyl.rubinbank.tools.DBSafe;
/**
 * A {@link DBSafe} for Accounts
 * @author criztovyl
 *
 */
public class AccountDBSafe implements DBSafe {
    private Reopenable reopenable;
    private String table;
    /**
     * A new DBSafe for Accounts
     * @param reopenable a reopenable Database Connection
     * @param table the table the account should be saved to
     */
    public AccountDBSafe(Reopenable reopenable, String table){
        this.reopenable = reopenable;
        this.table = table;
    }
    @Override
    public void saveToDatabase(HashMap<String, String> save) {
        String query = "";
        try{
            Connection con = reopenable.getDatabaseConnection();
            Statement stmt = con.createStatement();
            query = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                            "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                            "owner varchar(20), " +
                            "balance double)",
                            table);
            stmt.executeUpdate(query);
            if(inDB(save.get("owner"))){
                    query = String.format("Update %s set balance=%s where owner='%s'", 
                                    table, 
                                    save.get("balance"), 
                                    save.get("owner"));
                    RubinBankPlugin.getHelper().info("[AccDBSafe] Saving Account of " + save.get("owner"));
                    stmt.executeUpdate(query);
            }
            else {
                    query = String.format("INSERT INTO %s (owner, balance) values('%s', %s)", 
                                    table, 
                                    save.get("owner"), 
                                    save.get("balance"));
                    RubinBankPlugin.getHelper().info("[AccDBSafe] Inserting Account of " + save.get("owner"));
                    stmt.executeUpdate(query);
            }
            con.close();
        } catch (SQLException e) {
                RubinBankPlugin.getHelper().severe("Failed to save Account to Database! Error:\n" + e.toString() + "\n@ Query \"" + query + "\"");
                e.printStackTrace();
        }
    }
    @Override
    public ArrayList<HashMap<String, String>> loadFromDatabase() {
        String query = "";
        ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String,String>>();
        results.clear();
        try{
            Connection con = reopenable.getDatabaseConnection();
            Statement stmt = con.createStatement();
            query = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                            "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                            "owner varchar(20), " +
                            "balance double)", 
                            table);
            stmt.executeUpdate(query);
            query = String.format("SELECT * FROM %s", table);
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
                RubinBankPlugin.getHelper().severe("Failed to load Account(s) from Database! Error:\n" + e.toString() + "\n@ Query \"" + query + "\"");
                e.printStackTrace();
                return null;
        }
    }
    /**
     * Check if an account is already in the Database
     * @param owner the account owner
     * @return true if is in the Database, otherwise false
     * @throws SQLException
     */
    public boolean inDB(String owner) throws SQLException{
        Connection con = reopenable.getDatabaseConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
        while(rs.next()){
            if(rs.getString("owner").equals(owner)){
                return true;
        }
        }
        con.close();
        return false;
    }

}

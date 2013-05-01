package me.criztovyl.rubinbank.account;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import me.criztovyl.rubinbank.RubinBankPlugin;
import me.criztovyl.rubinbank.mysql.Reopenable;
import me.criztovyl.rubinbank.tools.DBSafe;
/**
 * A {@link DBSafe} for Account Statements
 * @author criztovyl
 *
 */
public class AccountStatementDBSafe implements DBSafe{
    private Reopenable reopenable;
    private String table;
    /**
     * A new DBSafe for AccountStatements
     * @param reopenable A {@link Reopenable} Database Connection
     * @param table the table the statements should saved to
     */
    public AccountStatementDBSafe(Reopenable reopenable, String table){
        this.reopenable = reopenable;
        this.table = table;
    }
                @Override
                public void saveToDatabase(HashMap<String, String> save) {
                        String query = "";
                        try{
                            Connection con = reopenable.getDatabaseConnection();
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
                                            table);
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
                                            table, 
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
                                RubinBankPlugin.getHelper().severe("Failed to save AccountStatement to the Database! Error:\n" + e.toString() + "\n@ Query \"" + query +"\"");
                        }
                }

                /**
                 * @return null; AccountStatements are not loaded from the Database, not needed In-Game
                 */
                @Override
                public ArrayList<HashMap<String, String>> loadFromDatabase() {
                        return null;
                }
                /**
                 * Check the Table
                 * @throws SQLException
                 */
                public void checkAndEdit() throws SQLException{
                    Connection con = reopenable.getDatabaseConnection();
                    Statement stmt = con.createStatement();
                    Statement stmt2 = con.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
                    ResultSetMetaData meta = rs.getMetaData();
                    boolean hasPlace = false;
                    for(int i = 1; i <= meta.getColumnCount(); i++){
                            if(
                                    meta.getColumnName(i).equalsIgnoreCase("owner") ||
                                    meta.getColumnName(i).equalsIgnoreCase("action") ||
                                    meta.getColumnName(i).equalsIgnoreCase("participant") ||
                                    meta.getColumnName(i).equalsIgnoreCase("date")
                            ){
                                    stmt2.executeUpdate(
                                            String.format("ALTER TABLE %s MODIFY %s TEXT", table,
                                                    meta.getColumnName(i)));
                            }
                            if(meta.getColumnName(i).equalsIgnoreCase("place")){
                                    hasPlace = true;
                            }
                    }
                    if(!hasPlace){
                            stmt2.executeUpdate(
                                    String.format("ALTER TABLE %s ADD place TEXT", table));
                    }
                    con.close();
                }
        }
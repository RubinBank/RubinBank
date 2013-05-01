package me.criztovyl.rubinbank.bankomat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import me.criztovyl.rubinbank.mysql.Reopenable;
import me.criztovyl.rubinbank.tools.DBSafe;
/**
 * A DBSafe for Bankomats
 * @author criztovyl
 *
 */
public class BankomatsDBSafe implements DBSafe {
        private Reopenable reopenable;
        private String table;
        /**
         * Create a new DBSafe
         * @param reopenable a reopenable Database Connection
         * @param table the table the bankomats should be writte to or read from
         * @throws SQLException
         */
        public BankomatsDBSafe(Reopenable reopenable, String table) throws SQLException{
                this.reopenable = reopenable;
                this.table = table;
                createIfNotExists();
                checkAndEditTableCoulumnNames();
                checkAndEditTableColumnDefinitions();
        }
        @Override
        public ArrayList<HashMap<String, String>> loadFromDatabase() throws SQLException {
                ArrayList<HashMap<String, String>> load = new ArrayList<HashMap<String,String>>();
                Connection con = reopenable.getDatabaseConnection();
                ResultSet rs = con.createStatement().executeQuery("SELECT * FROM " + table);
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
                con.close();
                return load;
        }

        @Override
        public void saveToDatabase(HashMap<String, String> save) throws SQLException {
            Connection con = reopenable.getDatabaseConnection();
                Statement stmt = con.createStatement();
                        String query = String.format("INSERT INTO %s (LocationX, LocationY, LocationZ, LocationWorld, Place, Type, Pos) " +
                                        "values( %s, %s, %s, '%s', '%s', '%s', '%s')",
                                        table,
                                        BankomatArgument.LOCX.getArg(save),
                                        BankomatArgument.LOCY.getArg(save),
                                        BankomatArgument.LOCZ.getArg(save),
                                        BankomatArgument.LOCWORLD.getArg(save),
                                        BankomatArgument.PLACE.getArg(save),
                                        BankomatArgument.TYPE.getArg(save),
                                        BankomatArgument.POS.getArg(save));
                        stmt.executeUpdate(query);
                        con.close();
        }
        /**
         * Check the Table
         * @throws SQLException
         */
        public void checkAndEditTableCoulumnNames() throws SQLException{
            Connection con = reopenable.getDatabaseConnection();
                Statement stmt = con.createStatement();
                Statement stmt2 = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
                ResultSetMetaData meta = rs.getMetaData();
                String query = "";
                for(int i = 1; i <= meta.getColumnCount(); i++){
                        String name = meta.getColumnName(i);
                        if(name.equalsIgnoreCase("Location")){
                                query = String.format("ALTER TABLE %s CHANGE Location Place Text", table);
                                stmt2.executeUpdate(query);
                        }
                        else if(name.equalsIgnoreCase("Multi")){
                                query = String.format("ALTER TABLE %s DROP Multi", table);
                                stmt2.executeUpdate(query);
                        }
                }
                con.close();
        }
        /**
         * Check the Table
         * @throws SQLException
         */
        public void checkAndEditTableColumnDefinitions() throws SQLException{
                Connection con = reopenable.getDatabaseConnection();
                Statement stmt = con.createStatement();
                Statement stmt2 = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
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
                                        query = String.format("ALTER TABLE %s MODIFY %s TEXT", table, 
                                                        meta.getColumnName(i));
                                        stmt2.executeUpdate(query);
                                }
                        }
                }
                con.close();
        }
        /**
         * Check the Table
         * @throws SQLException
         */
        private void createIfNotExists() throws SQLException {
            Connection con = reopenable.getDatabaseConnection();
            Statement stmt = con.createStatement();
            String query = String.format("CREATE TABLE IF NOT EXISTS %s (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
            		"LocationX INT, LocationY INT, LocationZ INT, LocationWorld TEXT, Pos TEXT, Type TEXT, Place TEXT)", table);
            stmt.executeUpdate(query);
            con.close();
        }
}

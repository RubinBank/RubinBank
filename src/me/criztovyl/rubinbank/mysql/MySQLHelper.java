package me.criztovyl.rubinbank.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLHelper {
        private String dburl, dbuser, dbpassword;
        public MySQLHelper(String dburl, String dbuser, String dbpass) throws SQLException{
                this.dburl = dburl;
                this.dbuser = dbuser;
                this.dbpassword = dbpass;
                DriverManager.getConnection(
                                this.dburl,
                                this.dbuser,
                                this.dbpassword);
        }
        public Connection getConnection() throws SQLException{
                return DriverManager.getConnection(
                        dburl,
                        dbuser,
                        dbpassword);
        }
        public void runSimpleQuery(String query) throws SQLException{
                getConnection().createStatement().executeQuery(query);
        }
}

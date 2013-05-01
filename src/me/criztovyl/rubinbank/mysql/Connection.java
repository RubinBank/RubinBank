package me.criztovyl.rubinbank.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection implements Reopenable{
    private String database_address, database_user, database_password;
    public Connection(String database_address, String database_user, String database_password){
        this.database_address = database_address;
        this.database_password = database_password;
        this.database_user = database_user;
    }
    @Override
    public java.sql.Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(database_address, database_user, database_password);
    }

}

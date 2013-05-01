package me.criztovyl.rubinbank.mysql;

import java.sql.Connection;
import java.sql.SQLException;

public interface Reopenable {
        public Connection getDatabaseConnection() throws SQLException;
}

package me.criztovyl.rubinbank.tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * An interface for saving classes to a MySQL Database
 * @author criztovyl
 *
 */
public interface DBSafe {
    /**
     * Save to Database
     * @param save A Map with the values that should be saved
     */
    void saveToDatabase(HashMap<String, String> save) throws SQLException;
    /**
     * @return A List of Maps containing the saved values.
     */
    ArrayList<HashMap<String, String>> loadFromDatabase() throws SQLException;
}

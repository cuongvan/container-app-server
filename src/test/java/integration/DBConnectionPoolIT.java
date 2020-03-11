/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration;

import helpers.DBHelper;
import org.junit.Test;

/**
 *
 * @author cuong
 */
public class DBConnectionPoolIT {
    @Test
    public void insert_null() {
        DBHelper.createTables();
        DBHelper.prepareStmt("INSERT INTO app_info(app_id, app_name, ava_url) VALUES(?, ?, ?)", stmt -> {
            stmt.setString(1, "a");
            stmt.setString(2, "a");
            stmt.setString(3, null);
            stmt.executeUpdate();
        });
    }
}

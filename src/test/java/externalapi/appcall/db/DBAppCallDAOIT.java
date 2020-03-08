/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package externalapi.appcall.db;

import externalapi.db.DB;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cuong
 */
public class DBAppCallDAOIT {
    DBAppCallDAO dao = new DBAppCallDAO(DB.pool);
    
    public DBAppCallDAOIT() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void create_call() {
        
    }
}

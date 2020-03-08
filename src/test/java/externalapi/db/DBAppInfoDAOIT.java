/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package externalapi.db;

import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppStatus;
import externalapi.appinfo.models.AppType;
import externalapi.appinfo.models.SupportLanguage;
import externalapi.appinfo.db.DBAppInfoDAO;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author cuong
 */
public class DBAppInfoDAOIT {
    DBAppInfoDAO dao = new DBAppInfoDAO(DB.pool);
    
    @BeforeClass
    public static void setUpClass() {
        DB.createTables();
    }
    
    @AfterClass
    public static void cleanup() {
        DB.dropTables();
    }
    
    @Before
    public void setUp() {
        DB.clearAllRows();
    }
    
    @After
    public void tearDown() {
    }
    

    @Test
    public void add_then_retrieve() {
        AppInfo insert = newApp()
            .setAppId("100")
            ;
        
        dao.createApp(insert);
        
        AppInfo getApp = dao.getById("100");
        assertEquals(insert.setStatus(AppStatus.CREATED), getApp);
    }
    
    @Test
    public void delete_app() {
        AppInfo app = newApp()
            .setAppId("100");
        
        dao.createApp(app);
        dao.deleteById(app.getAppId());
        assertNull(dao.getById(app.getAppId()));
        
    }
    
    public static AppInfo newApp() {
        return new AppInfo()
            .setAppId("10000")
            .setAppName("show number of rows in csv resource")
            .setType(AppType.BATCH)
            .setLanguage(SupportLanguage.PYTHON)
            ;
    }
}

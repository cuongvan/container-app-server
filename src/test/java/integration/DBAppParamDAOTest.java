/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration;

import externalapi.appinfo.db.DBAppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appparam.models.AppParam;
import externalapi.appparam.models.ParamType;
import externalapi.appparam.models.db.DBAppParamDAO;
import helpers.DBHelper;
import static integration.DBAppInfoDAOIT.newApp;
import java.util.Arrays;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author cuong
 */
public class DBAppParamDAOTest {
    DBAppInfoDAO appInfoDAO = new DBAppInfoDAO(DBHelper.getPool());
    DBAppParamDAO appParamDAO = new DBAppParamDAO(DBHelper.getPool());
    
    @BeforeClass
    public static void setupClass() {
        DBHelper.createTables();
    }
    
    @AfterClass
    public static void afterClass() {
        DBHelper.dropTables();
    }
    
    @Before
    public void setUp() {
        DBHelper.clearAllRows();
    }

    @Test
    public void test_update_params() {
        AppInfo app = newApp();
        List<AppParam> params = Arrays.asList(
            new AppParam()
                .setAppId(app.getAppId())
                .setName("algorithm")
                .setType(ParamType.KEY_VALUE)
                .setLabel("Algorithm"),
            new AppParam()
                .setAppId(app.getAppId())
                .setName("file2anonymize")
                .setType(ParamType.FILE)
                .setLabel("File to anonymize")
        );
        
        appInfoDAO.createApp(app);
        appParamDAO.updateParams(app.getAppId(), params);
        List<AppParam> params2 = appParamDAO.getAppParams(app.getAppId());
        assertEquals(params2, params);
        System.out.println(params2);
    }
}

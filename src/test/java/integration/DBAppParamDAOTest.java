/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration;

import externalapi.appinfo.DBAppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appparam.models.AppParam;
import externalapi.appparam.models.ParamType;
import externalapi.appparam.DBAppParamDAO;
import helpers.DBHelper;
import static integration.DBAppInfoDAOTest.newApp;
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
    
    @Before
    public void setUp() {
        DBHelper.truncateTables();
    }

    @Test
    public void test_update_params() {
        AppInfo app = newApp();
        String appId = appInfoDAO.createApp(app);
        
        List<AppParam> params = Arrays.asList(
            new AppParam()
                .setName("algorithm")
                .setType(ParamType.KEY_VALUE)
                .setLabel("Algorithm"),
            new AppParam()
                .setName("file2anonymize")
                .setType(ParamType.FILE)
                .setLabel("File to anonymize")
        );
        appParamDAO.updateParams(appId, params);
        
        List<AppParam> params2 = appParamDAO.getAppParams(appId);
        
        
        assertEquals(params2, params);
    }
}

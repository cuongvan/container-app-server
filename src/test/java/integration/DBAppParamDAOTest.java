/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration;

import externalapi.appinfo.DBAppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppParam;
import externalapi.appinfo.models.ParamType;
import helpers.DBHelper;
import java.util.Arrays;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;
import static integration.CreateAppTest.makeNewApp;

/**
 *
 * @author cuong
 */
public class DBAppParamDAOTest {
    DBAppInfoDAO appInfoDAO = new DBAppInfoDAO(DBHelper.getPool());
    
    @Before
    public void setUp() {
        DBHelper.truncateTables();
    }

    @Test
    public void test_update_params() {
        AppInfo app = makeNewApp();
        String appId = appInfoDAO.createApp(app);
        
        List<AppParam> params = Arrays.asList(
            AppParam.builder()
                .setName("algorithm")
                .setType(ParamType.KEY_VALUE)
                .setLabel("Algorithm").build(),
            AppParam.builder()
                .setName("file2anonymize")
                .setType(ParamType.FILE)
                .setLabel("File to anonymize").build()
        );
        appInfoDAO.updateParams(appId, params);
        
        List<AppParam> params2 = appInfoDAO.getAppParams(appId);
        
        
        assertEquals(params2, params);
    }
}

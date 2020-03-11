/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration;

import externalapi.appcall.db.DBAppCallDAO;
import externalapi.appcall.models.FileParam;
import externalapi.appcall.models.KeyValueParam;
import externalapi.appinfo.db.DBAppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appparam.models.AppParam;
import externalapi.appparam.models.ParamType;
import externalapi.appparam.models.db.DBAppParamDAO;
import helpers.DB;
import static integration.DBAppInfoDAOIT.newApp;
import java.util.Arrays;
import java.util.List;
import org.junit.*;
import org.junit.BeforeClass;

/**
 *
 * @author cuong
 */
public class DBAppCallDAOTest {
    DBAppInfoDAO appInfoDao = new DBAppInfoDAO(DB.pool);
    DBAppParamDAO appParamDao = new DBAppParamDAO(DB.pool);
    DBAppCallDAO appCallDAO = new DBAppCallDAO(DB.pool);
    
    @BeforeClass
    public static void setupClass() {
        DB.createTables();
    }
    
    @AfterClass
    public static void afterClass() {
        //DB.dropTables();
    }
    
    @Before
    public void setUp() {
        DB.clearAllRows();
    }

    @Test
    public void create_call() {
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
        
        String callId = "9999";
        
        appInfoDao.createApp(app);
        appParamDao.updateParams(app.getAppId(), params);
        appCallDAO.createNewCall(callId, app.getAppId(), DBAppCallDAO.ANONYMOUS_USER,
            Arrays.asList(new KeyValueParam("algorithm", "k-anonymity")),
            Arrays.asList(new FileParam("file2anonymize", "/tmp/aaa"))
        );
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration;

import externalapi.appcall.DBAppCallDAO;
import externalapi.appcall.models.FileParam;
import externalapi.appcall.models.KeyValueParam;
import externalapi.appinfo.DBAppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appparam.models.AppParam;
import externalapi.appparam.models.ParamType;
import externalapi.appparam.models.DBAppParamDAO;
import helpers.DBHelper;
import static integration.DBAppInfoDAOTest.newApp;
import java.util.Arrays;
import java.util.List;
import org.junit.*;

/**
 *
 * @author cuong
 */
public class DBAppCallDAOTest {
    DBAppInfoDAO appInfoDao = new DBAppInfoDAO(DBHelper.getPool());
    DBAppParamDAO appParamDao = new DBAppParamDAO(DBHelper.getPool());
    DBAppCallDAO appCallDAO = new DBAppCallDAO(DBHelper.getPool());
    
    @Before
    public void setUp() {
        DBHelper.truncateTables();
    }

    @Test
    public void create_call() {
        AppInfo app = newApp();
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
        
        
        String appId = appInfoDao.createApp(app);
        appParamDao.updateParams(appId, params);
        
        String callId = "9999";
        appCallDAO.createNewCall(callId, appId, DBAppCallDAO.ANONYMOUS_USER,
            Arrays.asList(new KeyValueParam("algorithm", "k-anonymity")),
            Arrays.asList(new FileParam("file2anonymize", "/tmp/aaa"))
        );
    }
}

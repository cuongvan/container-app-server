/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration;

import externalapi.appcall.AppCallDAO;
import externalapi.appcall.DBAppCallDAO;
import externalapi.appcall.models.FileParam;
import externalapi.appcall.models.KeyValueParam;
import externalapi.appinfo.DBAppInfoDAO;
import externalapi.appinfo.models.AppInfo;
import externalapi.appinfo.models.AppParam;
import externalapi.appinfo.models.ParamType;
import helpers.DBHelper;
import java.util.Arrays;
import java.util.List;
import org.junit.*;
import static integration.CreateAppTest.makeNewApp;

/**
 *
 * @author cuong
 */
public class DBAppCallDAOTest {
    DBAppInfoDAO appInfoDao = new DBAppInfoDAO(DBHelper.getPool());
    AppCallDAO appCallDAO = new DBAppCallDAO(DBHelper.getPool());
    
    @Before
    public void setUp() {
        DBHelper.truncateTables();
    }

    @Test
    public void create_call() {
        AppInfo app = makeNewApp();
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
        
        
        String appId = appInfoDao.createApp(app);
        appInfoDao.updateParams(appId, params);
        
        appCallDAO.createNewCall(appId, DBAppCallDAO.ANONYMOUS_USER,
            Arrays.asList(new KeyValueParam("algorithm", "k-anonymity")),
            Arrays.asList(new FileParam("file2anonymize", "/tmp/aaa"))
        );
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import externalapi.appinfo.models.AppInfo;
import java.io.IOException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author cuong
 */
public class AppInfoTest {
    @Test
    public void serialize_to_underscore() throws JsonProcessingException {
        AppInfo app = new AppInfo();
        String s = new ObjectMapper().writeValueAsString(app);
        System.out.println(s);
    }
    
    @Test
    public void deserialize() throws IOException {
        //String s = "{\"app_id\":\"100\",\"app_name\":null,\"type\":null,\"slug_name\":null,\"image\":null,\"owner\":null,\"description\":null,\"host_port\":0,\"container_port\":0,\"language\":null,\"status\":null,\"ava_url\":null}";
        String s = "{\"app_id\":\"100\"}";
        AppInfo obj = new ObjectMapper().readValue(s, AppInfo.class);
        assertEquals("100", obj.getAppId());
    }
}

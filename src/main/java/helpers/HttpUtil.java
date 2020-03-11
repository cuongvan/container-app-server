/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author cuong
 */
public class HttpUtil {
    public static void post(String urlString, Object jsonBody) throws IOException {
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            
            con.setDoOutput(true);
            String jsonString = new ObjectMapper().writeValueAsString(jsonBody);
            OutputStream out = con.getOutputStream();
            out.write(jsonString.getBytes());
            // out.flush();
            // out.close();
            
            con.setConnectTimeout(2000);
            con.setReadTimeout(2000);
            con.getResponseCode(); // wait for completion
            con.disconnect();
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}

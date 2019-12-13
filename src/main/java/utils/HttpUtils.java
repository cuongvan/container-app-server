/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import jetty_embed.Conf;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author cuong
 */
public class HttpUtils {

    private static CloseableHttpClient client = HttpClients.createDefault();

    public static CloseableHttpResponse post(String url, Object body) throws IOException {
        try {
            HttpPost post = new HttpPost(url);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(body);
            post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            return client.execute(post);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            throw new RuntimeException("dev error");
        }
    }
}

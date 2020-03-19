package test_lib.httprequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class httprequest_test {
    
    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
        String postEndpoint = "http://dummy.restapiexample.com/api/v1/create";
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(postEndpoint);

		httpPost.setHeader("Content-type", "application/json");


		String inputJson = "{\n" +
				"  \"name\": \"tammy133\",\n" +
				"  \"salary\": \"5000\",\n" +
				"  \"age\": \"20\"\n" +
				"}";

		StringEntity stringEntity = new StringEntity(inputJson);
		httpPost.setEntity(stringEntity);

		System.out.println("Executing request " + httpPost.getRequestLine());

		try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            
        }
    }
}

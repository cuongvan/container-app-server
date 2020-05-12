package notify;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpFinishNotifier {
    public static final String CKAN_WEBHOOK_HOST = "http://127.0.0.1:5002";
    private ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    public void executeDone(String callId) {
        String postEndpoint = CKAN_WEBHOOK_HOST + "/execute/done";
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(postEndpoint);

		//httpPost.setHeader("Content-type", "application/json");
		httpPost.setHeader("Content-type", "text/plain");


        try {
            StringEntity stringEntity;
            stringEntity = new StringEntity(callId);
            httpPost.setEntity(stringEntity);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return;
        }
        
		try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
        } catch (IOException ex) {
            System.out.println(">> webhook server not connectable");
        }
    }
}

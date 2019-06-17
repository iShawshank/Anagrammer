package Anagrammer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;

import static org.junit.Assert.*;

public class WordControllerTest {

    private HttpClient httpClient = HttpClientBuilder.create().build();

    @Before
    public void setUp() {

        try {
            HttpPost request = new HttpPost("http://localhost:3000/words.json");
            StringEntity params = new StringEntity("{\"words\" => [\"read\", \"dear\", \"dare\"] }");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
        } catch (Exception ex) {
            System.out.println("Error in WordControllerTest setup: " + ex.getMessage());
        }
    }

    @After
    public void tearDown() {
        try {
            HttpDelete request = new HttpDelete("http://localhost:3000/words.json");
            HttpResponse response = httpClient.execute(request);
        } catch (Exception ex) {
            System.out.println("Error in WordControllerTest tearDown: " + ex.getMessage());
        }
    }

    @Test
    public void addWords() {
        try {
            HttpPost request = new HttpPost("http://localhost:3000/words.json");
            StringEntity params = new StringEntity("{\"words\" => [\"ready\", \"deary\", \"yeard\"] }");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);

            assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED);
        } catch (Exception ex) {
            System.out.println("Error in WordControllerTest setup: " + ex.getMessage());
        }
    }

    @Test
    public void getAnagrams() {

        String actualResponse = "";

        try {
            HttpPost request = new HttpPost("http://localhost:3000/anagrams/read.json");
            HttpResponse response = httpClient.execute(request);

            assertTrue(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);

            HttpEntity entity = response.getEntity();
            actualResponse = EntityUtils.toString(entity, "UTF-8");

        } catch (Exception ex) {
            System.out.println("Error in WordControllerTest setup: " + ex.getMessage());
        }

        assertNotNull(actualResponse);

        String expectedResponse = "{\"anagrams\": [\"dare\", \"dear\"]}";

        // Format expectedResponse
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(expectedResponse).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        expectedResponse = gson.toJson(json);

        // assert both responses are equal
        assertTrue(expectedResponse.equals(actualResponse));
    }


}
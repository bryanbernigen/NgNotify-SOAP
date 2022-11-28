package ngnotify.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class HTTP {
    public static void main(String[] args) throws IOException, InterruptedException {
        HTTP http = new HTTP();
        if(http.newSubscription(1, 1)){
            System.out.println("Success");
        } else {
            System.out.println("Failed");
        }
    }

    public boolean newSubscription(Integer creator_id,Integer subscriber_id) throws IOException, InterruptedException {
        HashMap values = new HashMap<String, String>() {{
            put("subscriber_id", String.valueOf(subscriber_id));
            put ("creator_id", String.valueOf(creator_id));
        }};

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writeValueAsString(values);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/api/SubscriptionAPI/newsubscription"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
        if(response.body().contains("\"status\":true")){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean acceptSubscription(Integer creator_id, Integer user_id) throws IOException, InterruptedException {
        return this.updateSubscription(creator_id, user_id, "ACCEPTED");
    }

    public boolean rejectSubscription(Integer creator_id, Integer user_id) throws IOException, InterruptedException {
        return this.updateSubscription(creator_id, user_id, "REJECTED");
    }

    public boolean updateSubscription(Integer creator_id, Integer subscriber_id ,String status) throws IOException, InterruptedException {
        HashMap values = new HashMap<String, String>() {{
            put("subscriber_id", String.valueOf(subscriber_id));
            put ("creator_id", String.valueOf(creator_id));
            put ("status", status);
        }};

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writeValueAsString(values);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/api/SubscriptionAPI/updatesubscription"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
        if(response.body().contains("\"status\":true")){
            return true;
        }
        else{
            return false;
        }
    }

    
}
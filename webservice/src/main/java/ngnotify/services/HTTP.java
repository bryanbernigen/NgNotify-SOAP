package ngnotify.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.text.StyledEditorKit.BoldAction;

public class HTTP {
    public static void main(String[] args) throws IOException, InterruptedException {
        HTTP http = new HTTP();
        if(http.newSubscription(1, 1, "ini image")){
            System.out.println("Success");
        } else {
            System.out.println("Failed");
        }
    }

    public boolean newSubscription(Integer creator_id,Integer subscriber_id, String image_path) throws IOException, InterruptedException {
        HashMap values = new HashMap<String, String>() {{
            put("subscriber_id", String.valueOf(subscriber_id));
            put ("creator_id", String.valueOf(creator_id));
            put("image_path", image_path);
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

    public Vector<String> getAdminEmails() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3000/subscription/adminemails"))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        Vector<String> emails = new Vector<String>();
        String[] resp = response.body().split("\"");
        boolean flag_email = false;
        boolean flag_titik_dua = false;

        for (String string : resp) {
            if(flag_email && flag_titik_dua){
                flag_email = false;
                flag_titik_dua = false;
                emails.add(string);
            }
            if(string.equals("email")){
                flag_email = true;
            }
            if(flag_email && string.equals(":")){
                flag_titik_dua = true;
            }
        }

        for (String email : emails) {
            System.out.println(email);
        }
            
        return emails;
    }
}
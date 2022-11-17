package ngnotify.services;

import javax.xml.ws.Endpoint;

public class App {
    public static void main(String[] args) {
        // Publish di 0.0.0.0 biar bisa jalan di docker
        // Bukanya tetep di localhost:8080
        Endpoint.publish("http://0.0.0.0:8080/webservice/ngnotify", new Ngnotify());
        System.out.println("Hello World!");
    }
}

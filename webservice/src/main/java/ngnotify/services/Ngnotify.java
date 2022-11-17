package ngnotify.services;

import javax.jws.WebService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

@WebService(endpointInterface = "ngnotify.services.NgnotifyInterface")
public class Ngnotify implements NgnotifyInterface {
    @Override
    public String getNgnotify(String name) {
        return "Hello " + name;
    }
}

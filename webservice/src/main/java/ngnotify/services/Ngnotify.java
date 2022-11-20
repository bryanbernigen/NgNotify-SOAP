package ngnotify.services;

import java.sql.ResultSet;
import java.sql.Timestamp;

import javax.jws.WebService;
import javax.print.DocFlavor.READER;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.mysql.cj.xdevapi.Statement;

@WebService(endpointInterface = "ngnotify.services.NgnotifyInterface")
public class Ngnotify implements NgnotifyInterface {
    private DB db;

    Ngnotify() {
        this.db = new DB();
    }

    public void addLog(String description, String ip, String endpoint) throws Exception {
        this.db.prepareStatement("INSERT INTO loggings VALUES (default, ?, ?, ?, ?)");
        this.db.bind(1, description);
        this.db.bind(2, ip);
        this.db.bind(3, endpoint);
        this.db.bind(4, new Timestamp(System.currentTimeMillis()));
        this.db.executeUpdate();
    }

    @Override
    public String newSubscription(String ip, int creator_id, int subscriber_id){
        try {
            //Lihat apakah subscription request sudah pernah ada
            this.db.prepareStatement("SELECT * FROM subscriptions WHERE creator_id = ? AND subscriber_id = ? AND status NOT LIKE 'REJECTED'");
            this.db.bind(1, creator_id);
            this.db.bind(2, subscriber_id);
            ResultSet result = this.db.executeQuery();
            if (result.next()) {
                if (result.getString("status").equals("PENDING")) {
                    return "Pending subscription";
                } else {
                    return "Subscription already exists";
                }
            }

            //Masukkan Subscription
            this.db.startTransaction();
            this.addLog("New subscription request from "+subscriber_id+" to "+creator_id+" using "+ip, ip, "newSubscription");

            this.db.prepareStatement("INSERT INTO subscriptions VALUES (?, ?, ?)");
            this.db.bind(1, creator_id);
            this.db.bind(2, subscriber_id);
            this.db.bind(3, "PENDING");
            this.db.executeUpdate();
            this.db.commitTransaction();
            return "Subscription request sent, waiting for approval";
        } catch (Exception e) {
            System.out.println(e);
            try {
                this.db.rollbackTransaction();
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return "Subscription request failed, please try again";
    }

    @Override
    public String[] getSubscriptionList(String ip, String status) {
        try {
            //Masukkan Log
            this.db.startTransaction();
            this.addLog("Get subscription list with status "+status+" using "+ip, ip, "getSubscriptionList");

            //Ambil data
            this.db.prepareStatement("SELECT * FROM subscriptions WHERE status LIKE ?");
            this.db.bind(1, status);
            ResultSet result = this.db.executeQuery();

            //Hitung Jumlah Data yang ada
            int count = 0;
            while (result.next()) {
                count++;
            }

            //Buat array sebesar data untuk diisi hasil query
            String[] list = new String[count];
            count = 0;
            result.beforeFirst();
            while(result.next()){
                list[count] = result.getString("creator_id") + ";" + result.getString("subscriber_id") + ";" + result.getString("status");
                count++;
            }
            this.db.commitTransaction();
            return list;
        } catch (Exception e) {
            System.out.println(e);
            try {
                this.db.rollbackTransaction();
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}

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
    public String newSubscription(String ip, int creator_id, int subscriber_id) {
        try {
            // Lihat apakah subscription request sudah pernah ada
            this.db.prepareStatement("SELECT * FROM subscriptions WHERE creator_id = ? AND subscriber_id = ?");
            this.db.bind(1, creator_id);
            this.db.bind(2, subscriber_id);
            ResultSet result = this.db.executeQuery();
            if (result.next()) {
                if (result.getString("status").equals("PENDING")) {
                    return "Pending subscription";
                } else if (result.getString("status").equals("ACCEPTED")) {
                    return "Already subscribed";
                } else { // JIKA PERNAH REJECTED
                    try {
                        this.db.startTransaction();
                        this.addLog("Resend subscription request from " + subscriber_id + " to " + creator_id + " using " + ip,
                                ip,
                                "newSubscription");
                        
                        this.db.prepareStatement(
                                "UPDATE subscriptions SET status = 'PENDING' WHERE creator_id = ? AND subscriber_id = ?");
                        this.db.bind(1, creator_id);
                        this.db.bind(2, subscriber_id);
                        this.db.executeUpdate();
                        this.db.commitTransaction();
                        return "Subscription request sent";
                    } catch (Exception e) {
                        System.out.println(e);
                        try {
                            this.db.rollbackTransaction();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    return "Subscription request failed, please try again";
                }
            }

            // Jika belum ada subcription request, maka buat Subscription request baru
            this.db.startTransaction();
            this.addLog("New subscription request from " + subscriber_id + " to " + creator_id + " using " + ip, ip,
                    "newSubscription");

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
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return "Subscription request failed, please try again";
    }

    @Override
    public String[] getSubscriptionList(String ip, String status) {
        try {
            // Masukkan Log
            this.db.startTransaction();
            this.addLog("Get subscription list with status " + status + " using " + ip, ip, "getSubscriptionList");

            // Ambil data
            this.db.prepareStatement("SELECT * FROM subscriptions WHERE status LIKE ?");
            this.db.bind(1, status);
            ResultSet result = this.db.executeQuery();

            // Hitung Jumlah Data yang ada
            int count = 0;
            while (result.next()) {
                count++;
            }

            // Buat array sebesar data untuk diisi hasil query
            String[] list = new String[count];
            count = 0;
            result.beforeFirst();
            while (result.next()) {
                list[count] = result.getString("creator_id") + ";" + result.getString("subscriber_id") + ";"
                        + result.getString("status");
                count++;
            }
            this.db.commitTransaction();
            return list;
        } catch (Exception e) {
            System.out.println(e);
            try {
                this.db.rollbackTransaction();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String acceptSubscription(String ip, int creator_id, int subscriber_id) {
        try {
            this.db.startTransaction();

            // Cek apakah ada request yang sesuai
            this.db.prepareStatement(
                    "SELECT * FROM subscriptions WHERE creator_id = ? AND subscriber_id = ? AND status LIKE 'PENDING'");
            this.db.bind(1, creator_id);
            this.db.bind(2, subscriber_id);
            ResultSet result = this.db.executeQuery();
            if (!result.next()) {
                this.db.rollbackTransaction();
                return "No pending subscription request";
            }

            // Masukkan Log
            this.addLog("Accept subscription request from user " + subscriber_id + " to penyanyi " + creator_id
                    + " using " + ip, ip, "acceptSubscription");

            // Mengubah status subscription dari PENDING menjadi ACCEPTED
            this.db.prepareStatement(
                    "UPDATE subscriptions SET status = 'ACCEPTED' WHERE creator_id = ? AND subscriber_id = ?");
            this.db.bind(1, creator_id);
            this.db.bind(2, subscriber_id);
            this.db.executeUpdate();
            // TODO CALLBACK REST BUAT UPDATE DATA SUBSCRIBER
            this.db.commitTransaction();
            return "Subscription request accepted";
        } catch (Exception e) {
            System.out.println(e);
            try {
                this.db.rollbackTransaction();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return "Subscription request failed, please try again";
    }

    @Override
    public String rejectSubscription(String ip, int creator_id, int subscriber_id) {
        try {
            this.db.startTransaction();

            // Cek apakah ada request yang sesuai
            this.db.prepareStatement(
                    "SELECT * FROM subscriptions WHERE creator_id = ? AND subscriber_id = ? AND status LIKE 'PENDING'");
            this.db.bind(1, creator_id);
            this.db.bind(2, subscriber_id);
            ResultSet result = this.db.executeQuery();
            if (!result.next()) {
                this.db.rollbackTransaction();
                return "No pending subscription request";
            }

            // Masukkan Log
            this.addLog("Reject subscription request from user " + subscriber_id + " to penyanyi " + creator_id
                    + " using " + ip, ip, "rejectSubscription");

            // Mengubah status subscription dari PENDING menjadi REJECTED
            this.db.prepareStatement(
                    "UPDATE subscriptions SET status = 'REJECTED' WHERE creator_id = ? AND subscriber_id = ?");
            this.db.bind(1, creator_id);
            this.db.bind(2, subscriber_id);
            this.db.executeUpdate();
            // TODO CALLBACK REST BUAT UPDATE DATA SUBSCRIBER
            this.db.commitTransaction();
            return "Subscription request rejected";
        } catch (Exception e) {
            System.out.println(e);
            try {
                this.db.rollbackTransaction();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return "Subscription request failed, please try again";
    }

    @Override
    public String checkStatus(String ip, int creator_id, int subscriber_id) {
        try {
            this.db.startTransaction();

            // Cek apakah ada request yang sesuai
            this.db.prepareStatement("SELECT * FROM subscriptions WHERE creator_id = ? AND subscriber_id = ?");
            this.db.bind(1, creator_id);
            this.db.bind(2, subscriber_id);
            ResultSet result = this.db.executeQuery();
            if (!result.next()) {
                this.db.rollbackTransaction();
                return "No subscription request";
            }

            // Masukkan Log
            this.addLog("Check newest subscription status from user " + subscriber_id + " to penyanyi " + creator_id
                    + " using " + ip, ip, "checkStatus");

            String status = result.getString("status");
            this.db.commitTransaction();
            return status;
        } catch (Exception e) {
            System.out.println(e);
            try {
                this.db.rollbackTransaction();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return "Subscription request failed, please try again";
    }
}

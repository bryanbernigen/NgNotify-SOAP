package ngnotify.services;

import java.io.Console;
import java.sql.ResultSet;
import java.sql.Timestamp;

import javax.jws.WebService;

//MAILING
import java.util.Properties;
import java.util.Vector;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

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
    public String newSubscription(String auth ,String ip, int creator_id, int subscriber_id, String image_path) {
        if(!auth.equals("ngnotifyrest") && !auth.equals("ngnotifyvanilla")){
            try {
                // this.addLog("Unauthorized access to NewSubscription from "+ip, ip, "newSubscription");
                return "fail";
            } catch (Exception e) {
                e.printStackTrace();
            }
            // return "Unauthorized API access";
            return "fail";
        }
        System.out.println("newSubscription");
        try {
            // Lihat apakah subscription request sudah pernah ada
            this.db.prepareStatement("SELECT * FROM subscriptions WHERE creator_id = ? AND subscriber_id = ?");
            this.db.bind(1, creator_id);
            this.db.bind(2, subscriber_id);
            ResultSet result = this.db.executeQuery();
            if (result.next()) {
                if (result.getString("status").equals("PENDING")) {
                    // return "Pending subscription";
                    return "fail";
                } else if (result.getString("status").equals("ACCEPTED")) {
                    // return "Already subscribed";
                    return "fail";
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
                        HTTP http = new HTTP();
                        if(!http.updateSubscription(creator_id, subscriber_id, "PENDING")){
                            // throw new Exception("Failed to send subscription request to creator");
                            return "fail";
                        }
                        this.db.commitTransaction();
                        try {
                            Vector<String> emails = http.getAdminEmails();
                            String[] emailsArray = new String[emails.size()];
                            emailsArray = emails.toArray(emailsArray);
                            SendEmail sendEmail = new SendEmail();
                            sendEmail.send(emailsArray, "User "+ subscriber_id);
                         } catch (Exception e) {
                            System.out.println(e);
                         }
                        return "Subscription request sent";
                    } catch (Exception e) {
                        System.out.println(e);
                        try {
                            this.db.rollbackTransaction();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    // return "Subscription request failed, please try again";
                    return "fail";
                }
            }

            // Jika belum ada subcription request, maka buat Subscription request baru
            this.db.startTransaction();
            this.addLog("New subscription request from " + subscriber_id + " to " + creator_id + " using " + ip, ip,
                    "newSubscription");

            this.db.prepareStatement("INSERT INTO subscriptions VALUES (?, ?, ?, ?)");
            this.db.bind(1, creator_id);
            this.db.bind(2, subscriber_id);
            this.db.bind(3, "PENDING");
            this.db.bind(4, image_path);
            this.db.executeUpdate();
            HTTP http = new HTTP();
            if(!http.newSubscription(creator_id, subscriber_id, image_path)){
                // throw new Exception("Failed to send new subscription request to creator");
                return "fail";
            }
            this.db.commitTransaction();
            try {
                Vector<String> emails = http.getAdminEmails();
                String[] emailsArray = new String[emails.size()];
                emailsArray = emails.toArray(emailsArray);
                SendEmail sendEmail = new SendEmail();
                sendEmail.send(emailsArray, "User " + subscriber_id);
            } catch (Exception e) {
                System.out.println(e);
            }
            return "Subscription request sent, waiting for approval";
        } catch (Exception e) {
            System.out.println(e);
            try {
                this.db.rollbackTransaction();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        // return "Subscription request failed, please try again";
        return "fail";
    }

    @Override
    public String[] getSubscriptionList(String auth, String ip, String status) {
        if(!auth.equals("ngnotifyrest") && !auth.equals("ngnotifyvanilla")){
            try {
                this.addLog("Unauthorized access to GetSubscriptionList from "+ip, ip, "getSubscriptionList");
            } catch (Exception e) {
                e.printStackTrace();
            }
            // return new String[]{"Unauthorized API Access"};
            return new String[]{"fail"};
        }
        System.out.println("getSubscriptionList");
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
        // return null;
        return new String[]{"fail"};
    }

    @Override
    public String[] getSingleUserSubscriptionList(String auth, String ip, int subscriber_id) {
        if(!auth.equals("ngnotifyrest") && !auth.equals("ngnotifyvanilla")){
            try {
                // this.addLog("Unauthorized access to GetSingleUserSubscriptionList from "+ip, ip, "getSingleUserSubscriptionList");
                return new String[]{"fail"};
            } catch (Exception e) {
                e.printStackTrace();
            }
            // return new String[]{"Unauthorized API Access"};
            return new String[]{"fail"};
        }
        System.out.println("getSingleUserSubscriptionList");
        try {
            // Masukkan Log
            this.db.startTransaction();
            this.addLog("Get user "+subscriber_id+" subscription list  using " + ip, ip, "getSubscriptionList");

            // Ambil data
            this.db.prepareStatement("SELECT creator_id,status FROM subscriptions WHERE subscriber_id = ?");
            this.db.bind(1, subscriber_id);
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
                list[count] = result.getString("creator_id") + ";" + result.getString("status") ;
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
        // return null;
        return new String[]{"fail"};
    }

    @Override
    public String acceptSubscription(String auth, String ip, int creator_id, int subscriber_id) {
        if(!auth.equals("ngnotifyrest") && !auth.equals("ngnotifyvanilla")){
            try {
                this.addLog("Unauthorized access to AcceptSubscription from "+ip, ip, "acceptSubscription");
            } catch (Exception e) {
                e.printStackTrace();
            }
            // return "Unauthorized API Access";
            return "fail";
        }
        if(!auth.equals("ngnotifyrest") && !auth.equals("ngnotifyvanilla")){
            // return "Unauthorized API Access";
            return "fail";
        }
        System.out.println("acceptSubscription");
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
                // return "No pending subscription request";
                return "fail";
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
            HTTP http = new HTTP();
            if(!http.updateSubscription(creator_id, subscriber_id, "ACCEPTED")){
                // throw new Exception("Failed to send subscription request to creator");
                return "fail";
            }
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
        // return "Subscription request failed, please try again";
        return "fail";
    }

    @Override
    public String rejectSubscription(String auth, String ip, int creator_id, int subscriber_id) {
        if(!auth.equals("ngnotifyrest") && !auth.equals("ngnotifyvanilla")){
            try {
                this.addLog("Unauthorized access to RejectSubscription from "+ip, ip, "rejectSubscription");
            } catch (Exception e) {
                e.printStackTrace();
            }
            // return "Unauthorized API Access";
            return "fail";
        }
        System.out.println("rejectSubscription");
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
                // return "No pending subscription request";
                return "fail";
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
            HTTP http = new HTTP();
            if(!http.updateSubscription(creator_id, subscriber_id, "REJECTED")){
                // throw new Exception("Failed to send subscription request to creator");
                return "fail";
            }
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
        // return "Subscription request failed, please try again";
        return "fail";
    }

    @Override
    public String checkStatus(String auth, String ip, int creator_id, int subscriber_id) {
        if(!auth.equals("ngnotifyrest") && !auth.equals("ngnotifyvanilla")){
            try {
                this.addLog("Unauthorized access to CheckStatus from "+ip, ip, "checkStatus");
            } catch (Exception e) {
                e.printStackTrace();
            }
            // return "Unauthorized API Access";
            return "fail";
        }
        System.out.println("checkStatus");
        try {
            this.db.startTransaction();

            // Cek apakah ada request yang sesuai
            this.db.prepareStatement("SELECT * FROM subscriptions WHERE creator_id = ? AND subscriber_id = ?");
            this.db.bind(1, creator_id);
            this.db.bind(2, subscriber_id);
            ResultSet result = this.db.executeQuery();
            if (!result.next()) {
                this.db.rollbackTransaction();
                // return "No subscription request";
                return "fail";
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
        // return "Subscription request failed, please try again";
        return "fail";
    }
}

package ngnotify.services;

import java.sql.*;

public class DB {
    private Connection connection;
    private static String DB_URL = "jdbc:mysql://blid5cnndai9kyahpm2o-mysql.services.clever-cloud.com:3306/blid5cnndai9kyahpm2o";
    private static String DB_Username = "umj9pyxoqvtoisov";
    private static String DB_Password = "w1IOm772jyrPw2PAT5SM";

    public DB(){
        try{
            System.out.println("Connecting to MySQL DB...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_Username, DB_Password);
            System.out.println("Database connected");
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Error on connecting to DB");
        }
    }

    public ResultSet executeQuery(String sqlformattedquery) throws SQLException {
        Statement statement = this.connection.createStatement();
        return statement.executeQuery(sqlformattedquery);
    }
}

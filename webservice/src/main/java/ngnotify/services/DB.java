package ngnotify.services;

import java.sql.*;

import javax.xml.transform.Templates;

public class DB {
    private Connection connection;
    private PreparedStatement statement;
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

    public void startTransaction() throws SQLException {
        this.connection.setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException {
        this.connection.commit();
        this.connection.setAutoCommit(true);
    }

    public void rollbackTransaction() throws SQLException {
        this.connection.rollback();
        this.connection.setAutoCommit(true);
    }

    public void prepareStatement(String sqlQuery) throws SQLException {
        this.statement = this.connection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    public void bind(int index, String value) throws SQLException {
        this.statement.setString(index, value);
    }

    public void bind(int index, int value) throws SQLException {
        this.statement.setInt(index, value);
    }

    public void bind(int index, Timestamp value) throws SQLException {
        this.statement.setTimestamp(index, value);
    }

    public ResultSet executeQuery() throws SQLException {
        return this.statement.executeQuery();
    }

    public int executeUpdate() throws SQLException {
        return this.statement.executeUpdate();
    }
}

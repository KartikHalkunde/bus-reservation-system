import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection{
    public static Connection getConnection() throws SQLException {
        Properties pp = new Properties();
        try(FileInputStream fis = new FileInputStream("src/db.properties")) {
            pp.load(fis);
        }catch(IOException e){
            System.out.println("Error: Could not find the db.properties file!");
            e.printStackTrace();
            return null;
        }

        String URL = pp.getProperty("db.url");
        String USER = pp.getProperty("db.user");
        String PASS = pp.getProperty("db.password");
        
        return DriverManager.getConnection(URL,USER,PASS);
    }
}
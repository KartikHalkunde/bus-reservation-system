import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection{
    private static Properties cachedProps;

    public static Connection getConnection() throws SQLException {
        Properties pp = getCachedProperties();

        String URL = pp.getProperty("db.url");
        String USER = pp.getProperty("db.user");
        String PASS = pp.getProperty("db.password");
        
        if(URL == null || USER == null || PASS == null) {
            throw new SQLException("Missing required database properties");
        }
        
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static synchronized Properties getCachedProperties() throws SQLException {
        if (cachedProps != null) {
            return cachedProps;
        }

        Properties pp = new Properties();
        try (InputStream fis = DbConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (fis == null) {
                throw new IOException("db.properties not found in classpath");
            }
            pp.load(fis);
        } catch (IOException e) {
            throw new SQLException("Failed to load database configuration: " + e.getMessage(), e);
        }

        cachedProps = pp;
        return cachedProps;
    }
}
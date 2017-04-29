package ru.testing.client.common;

import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.objects.*;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.common.properties.DefaultProperties;

import java.io.*;
import java.sql.*;

/**
 * H2 database with application settings
 */
public class DataBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBase.class);
    private static final String APP_FOLDER = ".ws.client";
    private static final String DB_TYPE = "jdbc:h2";
    private static final String DB_NAME = "data.v%s";
    private static final String CREATE_SQL_SCRIPT = "create.db.sql";
    private static String dbPath;
    private static DataBase instance;
    private Connection connection;

    static {

        // Add database driver to class path
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.error("Error load sqlite jdbc driver: {}", e.getMessage());
            System.exit(1);
        }

        // Setup paths
        AppProperties properties = AppProperties.getAppProperties();
        String appFolder = String.format("%s/%s/", System.getProperty("user.home"), APP_FOLDER);
        dbPath = String.format("%s/%s", appFolder, String.format(DB_NAME, properties.getDbVersion()));

        // Check app folder exist
        File path = new File(appFolder);
        boolean settingPathExists = path.exists() || path.mkdirs();

        // Create
        if (settingPathExists && !new File(dbPath + ".h2.db").exists()) {
            getInstance().createTables();
        }
    }

    /**
     * Get database class instance
     *
     * @return DataBase
     */
    public synchronized static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    /**
     * Get global application settings
     *
     * @return Settings
     */
    public Settings getSettings() {
        Settings settings = null;
        try (Connection connection = getConnection()) {
            Statement s = connection.createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM global_settings");
            while (r.next()) {
                settings = new Settings(
                        r.getInt("font_size"),
                        r.getBoolean("text_wrap"),
                        r.getBoolean("auto_scroll")
                );
            }
        } catch (SQLException e) {
            LOGGER.error("Error get global settings: {}", e.getMessage());
        }
        return settings;
    }

    /**
     * Set current settings state
     *
     * @param settings Settings
     * @return boolean status
     */
    public boolean setSettings(Settings settings) {
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE global_settings SET " +
                    "font_size=?, text_wrap=?, auto_scroll=?");
            ps.setInt(1, settings.getFontSize());
            ps.setBoolean(2, settings.isTextWrap());
            ps.setBoolean(3, settings.isAutoScroll());
            ps.executeUpdate();
            return true;
        }catch (SQLException e) {
            LOGGER.error("Error set current settings state: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Create database connection
     *
     * @return Connection
     * @throws SQLException DriverManager get connection
     */
    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(String.format("%s:%s", DB_TYPE, dbPath), "sa", "");
        }
        return connection;
    }

    /**
     * Create default tables in database
     */
    private void createTables() {
        try (final Connection connection = getConnection()) {

            // Default properties values
            DefaultProperties properties = DefaultProperties.getInstance();

            // Create tables
            LOGGER.debug("Create tables in database ...");
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(CREATE_SQL_SCRIPT);
            RunScript.execute(connection, new InputStreamReader(is));

            // Insert default settings values
            LOGGER.debug("Insert default settings ...");
            PreparedStatement pss = connection.prepareStatement("INSERT INTO global_settings " +
                    "(font_size, text_wrap, auto_scroll) " +
                    "VALUES (?, ?, ?)");
            pss.setInt(1, properties.getMsgFontSize());
            pss.setBoolean(2, properties.isMsgWrap());
            pss.setBoolean(4, properties.isAutoScroll());
            pss.executeUpdate();

            LOGGER.debug("Database create successful");
        } catch (SQLException e) {
            LOGGER.error("Error create default tables: {}", e.getMessage());
        }
    }
}

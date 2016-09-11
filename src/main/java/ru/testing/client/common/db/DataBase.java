package ru.testing.client.common.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.db.objects.Profile;
import ru.testing.client.common.db.objects.Settings;
import ru.testing.client.common.properties.AppProperties;
import ru.testing.client.common.properties.DefaultProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class DataBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBase.class);
    private static final String APP_FOLDER = ".ws.client";
    private static final String DB_TYPE = "jdbc:sqlite";
    private static final String DB_NAME = "data.v%s.db";
    private static final String CREATE_SQL_SCRIPT = "create.db.sql";
    private static String dbPath;
    private static DataBase instance;
    private Connection connection;

    static {

        // Add database driver to class path
        try {
            Class.forName("org.sqlite.JDBC");
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
        if (settingPathExists && !new File(dbPath).exists()) {
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
            settings = new Settings(
                    r.getInt("font_size"),
                    r.getInt("text_wrap") == 1,
                    r.getInt("json_pretty") == 1,
                    r.getString("json_regex")
            );
        } catch (SQLException e) {
            LOGGER.error("Error get global settings: {}", e.getMessage());
        }
        return settings;
    }

    /**
     * Get current profile id
     *
     * @return int
     */
    public int getCurrentProfile() {
        try (Connection connection = getConnection()) {
            Statement s = connection.createStatement();
            ResultSet r = s.executeQuery("SELECT current_profile_id FROM global_settings");
            return r.getInt("current_profile_id");
        } catch (SQLException e) {
            LOGGER.error("Error get current profile from global settings: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Get profile data by id
     *
     * @param id int
     * @return Profile
     */
    public Profile getProfile(int id) {
        Profile profile = null;
        try (Connection connection = getConnection()) {
            Statement s = connection.createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM profiles WHERE id = " + id);
            profile = new Profile(
                    r.getInt("id"),
                    r.getString("name"),
                    r.getString("url"),
                    r.getInt("auto_scroll") == 1,
                    r.getInt("bar_show") == 1,
                    r.getInt("filter_show") == 1,
                    r.getInt("filter_on") == 1
            );
        } catch (SQLException e) {
            LOGGER.error("Error get profile id `{}` from database", id, e.getMessage());
        }
        return profile;
    }

    /**
     * Get all profiles from database
     * @return List<Profile>
     */
    public List<Profile> getProfiles() {
        List<Profile> profiles = new ArrayList<>();
        try (Connection connection = getConnection()) {
            Statement s = connection.createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM profiles");
            while (r.next()) {
                profiles.add(
                        new Profile(
                                r.getInt("id"),
                                r.getString("name"),
                                r.getString("url"),
                                r.getInt("auto_scroll") == 1,
                                r.getInt("bar_show") == 1,
                                r.getInt("filter_show") == 1,
                                r.getInt("filter_on") == 1
                        )
                );
            }
        } catch (SQLException e) {
            LOGGER.error("Error get profiles data", e.getMessage());
        }
        return profiles;
    }

    /**
     * Create database connection
     *
     * @return Connection
     * @throws SQLException DriverManager get connection
     */
    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(String.format("%s:%s", DB_TYPE, dbPath));
        }
        return connection;
    }

    /**
     * Create default tables in database
     */
    private void createTables() {
        try (Connection connection = getConnection()) {
            Statement s = connection.createStatement();

            // Create tables script
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(CREATE_SQL_SCRIPT);
            String script = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));

            // Default properties values
            DefaultProperties properties = DefaultProperties.getInstance();

            // Create tables
            LOGGER.debug("Create tables in database ...");
            s.executeUpdate(script);

            // Insert default settings values
            LOGGER.debug("Insert default settings ...");
            PreparedStatement gs = connection.prepareStatement("INSERT INTO global_settings " +
                    "(font_size, text_wrap, json_pretty, json_regex, current_profile_id) " +
                    "VALUES(?, ?, ?, ?, ?)");
            gs.setInt(1, properties.getMsgFontSize());
            gs.setInt(2, properties.isMsgWrap() ? 1 : 0);
            gs.setInt(3, properties.isMsgJsonPretty() ? 1 : 0);
            gs.setString(4, properties.getMsgJsonPrettyReplaceRegex());
            gs.setInt(5, 0);
            gs.executeUpdate();

            // Insert default profile values
            LOGGER.debug("Insert default profile ...");
            PreparedStatement p = connection.prepareStatement("INSERT INTO profiles " +
                    "(id , name, url, auto_scroll, bar_show, filter_show, filter_on) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?)");
            p.setInt(1, 0);
            p.setString(2, properties.getProfileName());
            p.setString(3, properties.getProfileWsUrl());
            p.setInt(4, properties.isProfileAutoScroll() ? 1 : 0);
            p.setInt(5, properties.isProfileShowBar() ? 1 : 0);
            p.setInt(6, properties.isProfileShowFilter() ? 1 : 0);
            p.setInt(7, properties.isProfileFilterOn() ? 1 : 0);
            p.executeUpdate();

            LOGGER.debug("Database create successful");
        } catch (SQLException e) {
            LOGGER.error("Error create default tables: {}", e.getMessage());
        }
    }
}
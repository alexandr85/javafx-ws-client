package ru.testing.client.common.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.db.objects.Header;
import ru.testing.client.common.db.objects.Profile;
import ru.testing.client.common.db.objects.ProfileName;
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
     * Set current settings state
     *
     * @param settings Settings
     * @return boolean status
     */
    public boolean setSettings(Settings settings) {
        try (Connection connection = getConnection()) {
            PreparedStatement gs = connection.prepareStatement("UPDATE global_settings SET " +
                    "font_size=?, text_wrap=?, json_pretty=?, json_regex=?");
            gs.setInt(1, settings.getFontSize());
            gs.setInt(2, settings.isTextWrap() ? 1 : 0);
            gs.setInt(3, settings.isJsonPretty() ? 1 : 0);
            gs.setString(4, settings.getJsonRegex());
            gs.executeUpdate();
            return true;
        }catch (SQLException e) {
            LOGGER.error("Error set current settings state: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Set selected current profile id
     *
     * @param id int
     * @return boolean status
     */
    public boolean setCurrentProfileId(int id) {
        try (Connection connection = getConnection()) {
            PreparedStatement gs = connection.prepareStatement("UPDATE global_settings SET current_profile_id=?");
            gs.setInt(1, id);
            gs.executeUpdate();
            return true;
        }catch (SQLException e) {
            LOGGER.error("Error set selected profile: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Add new profile data in database
     *
     * @param profile Profile
     * @return boolean status
     */
    public int addProfile(Profile profile) {
        try (Connection connection = getConnection()) {
            PreparedStatement p = connection.prepareStatement("INSERT INTO profiles " +
                    "(name, url, auto_scroll, bar_show, filter_show, filter_on) " +
                    "VALUES (?, ?, ?, ?, ?, ?)");
            p.setString(1, profile.getName());
            p.setString(2, profile.getUrl());
            p.setInt(3, profile.isAutoScroll() ? 1 : 0);
            p.setInt(4, profile.isBarShow() ? 1 : 0);
            p.setInt(5, profile.isFilterShow() ? 1 : 0);
            p.setInt(6, profile.isFilterOn() ? 1 : 0);
            int status = p.executeUpdate();
            if (status == 0) {
                throw new SQLException("Profile not insert");
            }
            Statement statement = connection.createStatement();
            ResultSet r = statement.executeQuery("SELECT last_insert_rowid()");
            return r.getInt(1);
        } catch (SQLException e) {
            LOGGER.error("Error add profile: {}", e.getMessage());
            return 0;
        }
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
            LOGGER.error("Error get profile id `{}`: {}", id, e.getMessage());
        }
        return profile;
    }

    /**
     * Get profiles name with id
     * @return List<ProfileName>
     */
    public List<ProfileName> getProfilesName() {
        List<ProfileName> profiles = new ArrayList<>();
        try (Connection connection = getConnection()) {
            Statement s = connection.createStatement();
            ResultSet r = s.executeQuery("SELECT id, name FROM profiles");
            while (r.next()) {
                profiles.add(
                        new ProfileName(
                                r.getInt("id"),
                                r.getString("name")
                        )
                );
            }
        } catch (SQLException e) {
            LOGGER.error("Error get profiles data: {}", e.getMessage());
        }
        return profiles;
    }

    /**
     * Remove selected profile
     *
     * @param profileId int
     * @return boolean status
     */
    public boolean removeProfile(int profileId) {
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM profiles WHERE id = ?");
            ps.setInt(1, profileId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error remove profile id `{}` from database", profileId, e.getMessage());
            return false;
        }
    }

    /**
     * Get headers list from database
     *
     * @param profileId int
     * @return List<Header>
     */
    public List<Header> getHeaders(int profileId) {
        List<Header> headers = new ArrayList<>();
        try (Connection connection = getConnection()) {
            Statement s = connection.createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM headers WHERE profile_id = " + profileId);
            while (r.next()) {
                headers.add(new Header(r.getString("name"), r.getString("value")));
            }
        } catch (SQLException e) {
            LOGGER.error("Error get headers: {}", e.getMessage());
        }
        return headers;
    }

    /**
     * Add headers in database
     *
     * @param profileId int
     * @param headers List<Header>
     */
    public void addHeaders(int profileId, List<Header> headers) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO headers (profile_id, name, value) VALUES (?, ?, ?)");
            for (Header header : headers) {
                ps.setInt(1, profileId);
                ps.setString(2, header.getName());
                ps.setString(3, header.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            LOGGER.error("Error add headers: {}", e.getMessage());
        }
    }

    /**
     * Remove headers by profile id
     *
     * @param profileId int
     */
    public void removeHeaders(int profileId) {
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM headers WHERE profile_id = ?");
            ps.setInt(1, profileId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error remove headers: {}", e.getMessage());
        }
    }

    /**
     * Add filter list in database
     *
     * @param profileId int
     * @param filterList List<String>
     */
    public void addFilterList(int profileId, List<String> filterList) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO filter (profile_id, value) VALUES (?, ?)");
            for (String filter : filterList) {
                ps.setInt(1, profileId);
                ps.setString(2, filter);
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            LOGGER.error("Error add filter: {}", e.getMessage());
        }
    }

    /**
     * Remove filter list by profile id
     *
     * @param profileId int
     */
    public void removeFilterList(int profileId) {
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM filter WHERE profile_id = ?");
            ps.setInt(1, profileId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error remove filter: {}", e.getMessage());
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
                    "VALUES (?, ?, ?, ?, ?)");
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
                    "VALUES (?, ?, ?, ?, ?, ?, ?)");
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

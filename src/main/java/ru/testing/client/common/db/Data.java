package ru.testing.client.common.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.db.objects.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Data {

    private static final Logger LOGGER = LoggerFactory.getLogger(Data.class);
    private static final String APP_FOLDER = ".ws.client";
    private static final String DB_TYPE = "jdbc:sqlite";
    private static final String DB_NAME = "data.db";
    private static String dbPath;
    private static Data instance;
    private Connection connection;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            String settingDirPath = String.format("%s/%s/", System.getProperty("user.home"), APP_FOLDER);
            File path = new File(settingDirPath);
            boolean settingPathExists = path.exists() || path.mkdirs();
            dbPath = String.format("%s/%s", settingDirPath, DB_NAME);
            if (settingPathExists && !new File(dbPath).exists()) {
                getData().createDefaultTables();
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error("Error load sqlite jdbc driver: {}", e.getCause());
        }
    }

    public synchronized static Data getData() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    /**
     * Set new session info in database
     *
     * @param s Session
     * @return int last ros id
     */
    public int setSession(Session s) {
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO sessions (name, url, filter_on, filter_show, auto_scroll, bar_show) " +
                    String.format("values('%s','%s', %s, %s, %s, %s)",
                            s.getName(),
                            s.getUrl(),
                            s.getFilterOn() ? 1 : 0,
                            s.getFilterShow() ? 1 : 0,
                            s.getAutoScroll() ? 1 : 0,
                            s.getBarShow() ? 1 : 0)
            );
            ResultSet insert = statement.executeQuery("SELECT max(id) FROM sessions");
            return insert.getInt(1);
        } catch (SQLException e) {
            LOGGER.error("Error set new session in database: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Get all sessions id and name from session database
     */
    public List<Session> getSessions() {
        List<Session> sessions = new ArrayList<>();
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT id, name FROM sessions ORDER BY id");
            while (result.next()) {
                sessions.add(new Session(result.getInt("id"), result.getString("name")));
            }
        } catch (SQLException e) {
            LOGGER.error("Error get sessions from database: error cod {}", e.getErrorCode());
        }
        return sessions;
    }

    /**
     * Get session by id from session database
     *
     * @param id Integer
     */
    public Session getSession(int id) {
        Session session = null;
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            ResultSet r = statement.executeQuery("SELECT * FROM sessions WHERE id = " + id);
            session = new Session(r.getString("name"),
                    r.getString("url"),
                    r.getBoolean("filter_on"),
                    r.getBoolean("filter_show"),
                    r.getBoolean("auto_scroll"),
                    r.getBoolean("bar_show"));
            session.setFilters(getFilters(id));
            session.setHeaders(getHeaders(id));
            session.setRxMessages(getRxMessages(id));
            session.setTxMessages(getTxMessages(id));
        } catch (SQLException e) {
            LOGGER.error("Error get session by id {} from database: error cod {}", id, e.getErrorCode());
        }
        return session;
    }

    /**
     * Delete selected session by id from database
     *
     * @param id int
     */
    public void deleteSession(int id) {
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM sessions WHERE id = " + id);
        } catch (SQLException e) {
            LOGGER.error("Error delete session by id {} from database: error cod {}", id, e.getErrorCode());
        }
    }

    /**
     * Set filters list in database
     *
     * @param filters   List<String>
     * @param sessionId int
     */
    public void setFilters(List<String> filters, int sessionId) {
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            for (String value : filters) {
                statement.executeUpdate("INSERT INTO filters (session_id, value) " +
                        String.format("values(%s,'%s')", sessionId, value));
            }
        } catch (SQLException e) {
            LOGGER.error("Error set filters list in database: {}", e.getMessage());
        }
    }

    /**
     * Set headers list in database
     *
     * @param headers   List<Filter>
     * @param sessionId int
     */
    public void setHeaders(List<Header> headers, int sessionId) {
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            for (Header header : headers) {
                statement.executeUpdate("INSERT INTO headers (session_id, name, value) " +
                        String.format("values(%s,'%s', '%s')", sessionId, header.getName(), header.getValue()));
            }
        } catch (SQLException e) {
            LOGGER.error("Error set headers list in database: {}", e.getMessage());
        }
    }

    /**
     * Set txMessages list in database
     *
     * @param txMessages List<Filter>
     * @param sessionId  int
     */
    public void setTxMessages(List<String> txMessages, int sessionId) {
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            for (String txMessage : txMessages) {
                statement.executeUpdate("INSERT INTO tx_messages (session_id, value) " +
                        String.format("values(%s,'%s')", sessionId, txMessage));
            }
        } catch (SQLException e) {
            LOGGER.error("Error set txMessages list in database: {}", e.getMessage());
        }
    }

    /**
     * Set rxMessages list in database
     *
     * @param rxMessages List<Filter>
     * @param sessionId  int
     */
    public void setRxMessages(List<RxMessage> rxMessages, int sessionId) {
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            for (RxMessage rxMessage : rxMessages) {
                statement.executeUpdate("INSERT INTO rx_messages (session_id, time, value) " +
                        String.format("values(%s,'%s','%s')", sessionId, rxMessage.getTime(), rxMessage.getValue()));
            }
        } catch (SQLException e) {
            LOGGER.error("Error set rxMessages list in database: {}", e.getMessage());
        }
    }

    /**
     * Get filters from database by session id
     *
     * @param sessionId int
     * @return List<String>
     */
    private List<String> getFilters(int sessionId) {
        List<String> filters = new ArrayList<>();
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT value FROM filters WHERE session_id = " + sessionId + " ORDER BY id");
            while (result.next()) {
                filters.add(result.getString("value"));
            }
        } catch (SQLException e) {
            LOGGER.error("Error get filters from database: error cod {}", e.getErrorCode());
        }
        return filters;
    }

    /**
     * Get headers from database by session id
     *
     * @param sessionId int
     * @return List<Header>
     */
    private List<Header> getHeaders(int sessionId) {
        List<Header> headers = new ArrayList<>();
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT name, value FROM headers WHERE session_id = " + sessionId + " ORDER BY id");
            while (result.next()) {
                headers.add(new Header(sessionId, result.getString("name"), result.getString("value")));
            }
        } catch (SQLException e) {
            LOGGER.error("Error get headers from database: error cod {}", e.getErrorCode());
        }
        return headers;
    }

    /**
     * Get received messages from database by session id
     *
     * @param sessionId int
     * @return List<RxMessage>
     */
    private List<RxMessage> getRxMessages(int sessionId) {
        List<RxMessage> rxMessages = new ArrayList<>();
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT time, value FROM rx_messages WHERE session_id = " + sessionId + " ORDER BY id");
            while (result.next()) {
                rxMessages.add(new RxMessage(sessionId, result.getString("time"), result.getString("value")));
            }
        } catch (SQLException e) {
            LOGGER.error("Error get received messages from database: error cod {}", e.getErrorCode());
        }
        return rxMessages;
    }

    /**
     * Get transmitted messages from database by session id
     *
     * @param sessionId int
     * @return List<TxMessage>
     */
    private List<TxMessage> getTxMessages(int sessionId) {
        List<TxMessage> txMessages = new ArrayList<>();
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT value FROM tx_messages WHERE session_id = " + sessionId + " ORDER BY id");
            while (result.next()) {
                txMessages.add(new TxMessage(sessionId, result.getString("value")));
            }
        } catch (SQLException e) {
            LOGGER.error("Error get transmitted messages from database: error cod {}", e.getErrorCode());
        }
        return txMessages;
    }

    /**
     * Create database connection
     *
     * @return Connection
     * @throws SQLException DriverManager get connection
     */
    private Connection createConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(String.format("%s:%s", DB_TYPE, dbPath));
        }
        return connection;
    }

    /**
     * Create default empty tables in database
     */
    private void createDefaultTables() {
        try (Connection connection = createConnection()) {
            Statement statement = connection.createStatement();
            LOGGER.debug("Creating default tables ... ");
            statement.executeUpdate("CREATE TABLE sessions (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT, url TEXT, filter_on TEXT, filter_show TEXT, auto_scroll TEXT, bar_show TEXT)");
            statement.executeUpdate("CREATE TABLE headers (id INTEGER PRIMARY KEY AUTOINCREMENT, session_id INTEGER, name TEXT, value TEXT)");
            statement.executeUpdate("CREATE TABLE filters (id INTEGER PRIMARY KEY AUTOINCREMENT, session_id INTEGER, value TEXT)");
            statement.executeUpdate("CREATE TABLE tx_messages (id INTEGER PRIMARY KEY AUTOINCREMENT, session_id INTEGER, value TEXT)");
            statement.executeUpdate("CREATE TABLE rx_messages (id INTEGER PRIMARY KEY AUTOINCREMENT, session_id INTEGER, time TEXT, value TEXT)");
            statement.executeUpdate("CREATE TABLE auto_messages (id INTEGER PRIMARY KEY AUTOINCREMENT, session_id INTEGER, value TEXT)");
            LOGGER.debug("Default tables created successful");
            LOGGER.debug("Insert default session");
            setSession(new Session("default", "wss://echo.websocket.org", false, false, true, true));
        } catch (SQLException e) {
            LOGGER.error("Error create default tables: {}", e.getMessage());
        }
    }
}

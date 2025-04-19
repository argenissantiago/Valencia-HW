package movielist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The {@code DatabaseManager} class handles all database operations for the Movie List.
 * Included are methods for connecting to a database, executing SQL queries and updates,
 * retrieving user/movie data, and loading data into UserManager.
 * <p>
 * This class follows the singleton pattern.
 */
public class DatabaseManager {
    private String url;
    private String username;
    private String password;

    private Connection connection;
    private static DatabaseManager instance;

    /**
     * Connects to the database using the provided credentials and database details.
     *
     * @param host     the database host (ex."localhost")
     * @param port     the database port (ex."3306")
     * @param dbName   the name of the database
     * @param user     the MySQL username
     * @param password the MySQL password
     * @return true if the connection is successful, false otherwise
     */

    public boolean connect(String host, String port, String dbName, String user, String password) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false&serverTimezone=UTC";
        this.username = user;
        this.password = password;

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database successfully: " + url);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            return false;
        }
    }


    /**
     * Returns current database connection.
     *
     * @return the active {@code Connection} object
     */

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Reconnected to database: " + url);
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        }

        return connection;
    }


    // Closes database connection
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Executes SELECT SQL query and returns result.
     *
     * @param query the SQL SELECT query to execute
     * @return a {@code ResultSet} containing the query results
     * @throws SQLException if query execution fails
     */

    public ResultSet executeQuery(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    /**
     * Executes an SQL statement (INSERT, UPDATE, or DELETE).
     *
     * @param query the SQL statement to execute
     * @return the number of rows affected
     * @throws SQLException if update execution fails
     */

    public int executeUpdate(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeUpdate(query);
    }

    /**
     * Retrieves user ID for a given username.
     *
     * @param username the username to search for
     * @return the user's ID
     * @throws SQLException if the user is not found or a database error occurs
     */

    public int getUserIdFromUsername(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("User not found in database: " + username);
                }
            }
        }
    }

    /**
     * Returns the singleton instance of {@code DatabaseManager}.
     *
     * @return the single {@code DatabaseManager} instance
     */

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Retrieves a list of movie titles associated with user.
     *
     * @param username the username whose favorite movies should be retrieved
     * @return a list of movie titles
     */

    public List<String> getMoviesForUser(String username) {
        List<String> movies = new ArrayList<>();
        String sql = "SELECT title FROM movies WHERE user_id = (SELECT id FROM users WHERE username = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movies.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving movies for user: " + e.getMessage());
        }

        return movies;
    }

    /**
     * Loads all users from database and adds them to a {@code UserManager} instance.
     *
     * @return a populated {@code UserManager} with users from the database
     * @throws SQLException if a database error occurs
     */

    public UserManager loadUsers() throws SQLException {
        UserManager userManager = new UserManager(this);

        String sql = "SELECT * FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                String pin = rs.getString("pin");
                String email = rs.getString("email");
                int age = rs.getInt("age");

                User user = new User(username, pin, email, age);
                userManager.addUser(user);
            }
        }

        return userManager;
    }

    /**
     * Loads user's favorite movies from the database and adds them
     * to the corresponding {@code User} object in the {@code UserManager}.
     *
     * @param userManager the {@code UserManager} containing users to populate with movies
     * @throws SQLException if a database error occurs
     */

    public void loadMoviesIntoUsers(UserManager userManager) throws SQLException {
        String sql = "SELECT u.username, m.title FROM movies m JOIN users u ON m.user_id = u.id";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                String title = rs.getString("title");

                User user = userManager.getUser(username);
                if (user != null) {
                    user.addFavoriteMovie(title);
                }
            }
        }
    }

}

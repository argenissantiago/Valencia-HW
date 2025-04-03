package movielist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DatabaseManager {
    private Connection connection;
    private static DatabaseManager instance;

    // Opens the database connection using user-supplied credentials
    public boolean connect(String host, String port, String dbName, String user, String password) {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false&serverTimezone=UTC";

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            return false;
        }
    }





    // Getter for the connection
    public Connection getConnection() {
        return connection;
    }

    // Closes the connection when the app is exiting
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Disconnected from database.");
            } catch (SQLException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            }
        }
    }

    // Example helper method for SELECT queries
    public ResultSet executeQuery(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    // Example helper method for INSERT, UPDATE, DELETE
    public int executeUpdate(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeUpdate(query);
    }
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

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
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

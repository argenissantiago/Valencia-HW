package movielist;

import java.sql.*;
import java.util.*;

public class UserManager {
    private Map<String, User> userDatabase = new HashMap<>();
    private DatabaseManager db;

    public UserManager(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Loads all users from the database into memory.
     */
    public void loadAllUsers() {
        userDatabase.clear();

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                String username = rs.getString("username");
                String pin = rs.getString("pin");
                String email = rs.getString("email");
                int age = rs.getInt("age");

                User user = new User(username, pin, email, age);

                // Load favorite movies for the user
                try (PreparedStatement ps = conn.prepareStatement("SELECT title FROM movies WHERE user_id = ?")) {
                    ps.setInt(1, rs.getInt("id"));
                    ResultSet movieRs = ps.executeQuery();

                    while (movieRs.next()) {
                        user.addFavoriteMovie(movieRs.getString("title"));
                    }
                }

                userDatabase.put(username, user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new user to the database and memory.
     */
    public boolean addUser(User user) {
        if (!isUsernameAvailable(user.getUserId())) return false;

        String sql = "INSERT INTO users (username, pin, email, age) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getHashedPin());
            ps.setString(3, user.getEmail());
            ps.setInt(4, user.getAge());
            ps.executeUpdate();

            userDatabase.put(user.getUserId(), user);  // cache in memory too
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets a user from the in-memory database (populated by loadAllUsers).
     */
    public User getUser(String userId) {
        return userDatabase.get(userId);
    }

    public boolean isUsernameAvailable(String username) {
        return !userDatabase.containsKey(username);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userDatabase.values());
    }

    public boolean removeUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                userDatabase.remove(username);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void loadUsersFromDatabase() {
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            Connection conn = db.getConnection();

            // Step 1: Load users
            String userQuery = "SELECT * FROM users";
            Statement userStmt = conn.createStatement();
            ResultSet userRs = userStmt.executeQuery(userQuery);

            while (userRs.next()) {
                int id = userRs.getInt("id");
                String username = userRs.getString("username");
                String pin = userRs.getString("pin");
                String email = userRs.getString("email");
                int age = userRs.getInt("age");

                User user = new User(username, pin, email, age);
                userDatabase.put(username, user);

                // Step 2: Load favorite movies for this user
                String movieQuery = "SELECT title FROM movies WHERE user_id = ?";
                PreparedStatement movieStmt = conn.prepareStatement(movieQuery);
                movieStmt.setInt(1, id);
                ResultSet movieRs = movieStmt.executeQuery();

                while (movieRs.next()) {
                    String movie = movieRs.getString("title");
                    user.addFavoriteMovie(movie);
                }
                movieStmt.close();
            }

            userStmt.close();
            System.out.println("Users and their movies successfully loaded from the database.");
        } catch (SQLException e) {
            System.err.println("Error loading users from database: " + e.getMessage());
        }
    }
    public List<User> getAllUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            Connection conn = db.getConnection();

            // Fetch all users
            String userQuery = "SELECT * FROM users";
            PreparedStatement userStmt = conn.prepareStatement(userQuery);
            ResultSet userResults = userStmt.executeQuery();

            while (userResults.next()) {
                String username = userResults.getString("username");
                String pin = userResults.getString("pin");
                String email = userResults.getString("email");
                int age = userResults.getInt("age");
                int userId = userResults.getInt("id");

                User user = new User(username, pin, email, age);

                // Fetch movies for each user
                String movieQuery = "SELECT title FROM movies WHERE user_id = ?";
                PreparedStatement movieStmt = conn.prepareStatement(movieQuery);
                movieStmt.setInt(1, userId);
                ResultSet movieResults = movieStmt.executeQuery();

                while (movieResults.next()) {
                    user.addFavoriteMovie(movieResults.getString("title"));
                }

                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users from database: " + e.getMessage());
        }

        return users;
    }

}

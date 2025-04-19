package movielist;

import java.io.*;
import java.util.*;
import java.sql.*;

/**
 * The {@code LoginManager} class handles user login and registration
 * for the Movie List app. It supports signing in, signing up, loading
 * users from a file, and saving user data to a file.
 * <p>
 * This class utilizes a local, in-memory database of users using a {@code Map},
 * and interacts with the user through console prompts.
 */

public class LoginManager {
    private Map<String, User> userDatabase;
    private Scanner scanner;
    private User currentUser;

    /**
     * Constructs a {@code LoginManager} with empty user database
     * and initializes a scanner.
     */

    public LoginManager() {
        this.userDatabase = new HashMap<>();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prompts user for filename and loads users from said file.
     * If successful, it stores them in the in-memory database.
     * Catches and displays file-related errors.
     */

    public void loadUsersFromFile() {
        System.out.print("Enter the filename: ");
        String filename = scanner.nextLine();
        try {
            loadUsersFromFile(filename);
            System.out.println("Users loaded successfully and stored in the database.");
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    /**
     * Loads users from a specified file for testing purposes.
     *
     * @param filename Name of the file to load.
     * @throws IOException if file cannot be read.
     */
    public void loadUsersFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) { // UserID, PIN, Email, Age
                    User user = new User(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));

                    // Load movies if available
                    if (parts.length == 5 && !parts[4].equalsIgnoreCase("None")) {
                        String[] movies = parts[4].split(";");
                        for (String movie : movies) {
                            user.addFavoriteMovie(movie);
                        }
                    }
                    // Store user in the database
                    userDatabase.put(parts[0], user);
                }
            }
        }
    }

    /**
     * Prompts user for ID and PIN to authenticate and sign in.
     * If credentials are valid, returns the corresponding {@code User} object.
     *
     * @return the signed-in {@code User}, or {@code null} if authentication fails
     */

    public User signIn(UserManager userManager) {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter 4‑digit PIN: ");
        String pin = scanner.nextLine().trim();

        // 1) Input validation
        if (!userManager.isValidLoginInput(username, pin)) {
            System.out.println("Invalid input. Please enter a non‑blank username and a 4‑digit PIN.");
            return null;
        }

        // 2) Connect (and check for null)
        DatabaseManager db = DatabaseManager.getInstance();
        Connection conn = db.getConnection();
        if (conn == null) {
            System.err.println("Failed to obtain database connection.");
            return null;
        }

        final String USER_SQL = "SELECT pin, email, age, id FROM users WHERE username = ?";
        final String MOVIES_SQL = "SELECT title FROM movies WHERE user_id = ?";

        String dbPin, email;
        int age, userId;

        // ——— FIRST QUERY: fetch PIN + user info ———
        try (PreparedStatement ps = conn.prepareStatement(USER_SQL)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("User not found.");
                    return null;
                }
                dbPin = rs.getString("pin");
                email = rs.getString("email");
                age = rs.getInt("age");
                userId = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Could not log in: " + e.getMessage());
            return null;
        }

        // 3) PIN check
        if (!dbPin.equals(pin)) {
            System.out.println("Incorrect PIN.");
            return null;
        }

        // ——— SECOND QUERY: load favorite movies ———
        List<String> movies = new ArrayList<>();
        try (PreparedStatement ps2 = conn.prepareStatement(MOVIES_SQL)) {
            ps2.setInt(1, userId);
            try (ResultSet rs2 = ps2.executeQuery()) {
                while (rs2.next()) {
                    movies.add(rs2.getString("title"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading favorite movies: " + e.getMessage());
            // we can still proceed without movies
        }

        // 4) Build User & cache
        User user = new User(username, pin, email, age);
        user.setDatabaseId(userId);
        movies.forEach(user::addFavoriteMovie);
        userManager.addUser(user);

        System.out.println("Login successful!");
        return user;
    }


    /**
     * Prompts user to create a new account, including ID, PIN, email, and age.
     * Validates the input and adds the user to the database if successful.
     * Also saves the user to a file for persistence.
     */

    public void signUp() {
        System.out.print("Enter Username: ");
        String id = scanner.nextLine().trim();

        if (id.isEmpty() || userDatabase.containsKey(id)) {
            System.out.println("Invalid or taken username. Try again.");
            scanner = new Scanner(System.in); // flush buffer
            return;
        }

        System.out.print("Enter a 4-digit PIN: ");
        String pin = scanner.nextLine().trim();
        if (pin.length() != 4 || !pin.matches("\\d{4}")) {
            System.out.println("Invalid PIN format. Please enter exactly 4 digits.");
            scanner = new Scanner(System.in); // flush buffer
            return;
        }

        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty() || !email.contains("@")) {
            System.out.println("Invalid email. Try again.");
            scanner = new Scanner(System.in); // flush buffer
            return;
        }

        int age;
        while (true) {
            System.out.print("Enter Age: ");
            String ageInput = scanner.nextLine();
            try {
                age = Integer.parseInt(ageInput);
                if (age < 0) {
                    System.out.println("Age cannot be negative. Try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid age format. Please enter a number.");
            }
        }

        User newUser = new User(id, pin, email, age);
        userDatabase.put(id, newUser);
        saveUserToFile(newUser);

        System.out.println("Account created! Please log in.");
    }

    /**
     * Logs out current user by clearing the {@code currentUser} reference.
     */

    public void logout() {
        System.out.println("Logging out...");
        currentUser = null;
    }

    /**
     * Saves a user to a file.
     *
     * @param user The user to be saved.
     */
    public void saveUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(user.getUserId() + "," + user.getHashedPin() + "," + user.getEmail() + "," + user.getAge() + "," +
                    (user.getFavoriteMovies().isEmpty() ? "None" : String.join(";", user.getFavoriteMovies())));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving user to file.");
        }
    }

    /**
     * Returns current in-memory user database.
     *
     * @return a {@code Map} containing user IDs mapped to {@code User} objects
     */

    public Map<String, User> getUserDatabase() {
        return userDatabase;
    }

    public boolean authenticate(String id, String pin) {
        if (id == null || pin == null || pin.length() != 4 || !pin.matches("\\d+")) {
            return false;
        }

        User user = userDatabase.get(id);
        return user != null && user.getHashedPin().equals(pin);
    }

    public static boolean validateUser(User user) {
        return user != null &&
                user.getUserId() != null && !user.getUserId().isBlank() &&
                user.getHashedPin() != null && user.getHashedPin().matches("\\d{4}") &&
                user.getEmail() != null && !user.getEmail().isBlank() &&
                user.getAge() >= 0;
    }

}

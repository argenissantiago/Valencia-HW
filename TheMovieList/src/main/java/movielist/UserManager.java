package movielist;

import java.io.*;
import java.util.*;

public class UserManager {
    private Map<String, User> userDatabase;

    public UserManager() {
        this.userDatabase = new HashMap<>();
    }

    public Map<String, User> getUsers() {
        return userDatabase;
    }

    /**
     * Loads users from a specified file.
     * @param filename The name of the file to load.
     * @throws IOException if file cannot be read.
     */
    public void loadUsersFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    User user = new User(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));

                    if (parts.length == 5 && !parts[4].equalsIgnoreCase("None")) {
                        String[] movies = parts[4].split(";");
                        for (String movie : movies) {
                            user.addFavoriteMovie(movie);
                        }
                    }

                    addUser(user); // Store user in memory
                }
            }
        }
    }

    /**
     * Retrieves a user by their ID.
     * @param userId The ID of the user to find.
     * @return The User object if found, or null if not found.
     */
    public User getUser(String userId) {
        return userDatabase.get(userId);
    }

    /**
     * Adds a new user to the system.
     * @param user The user to add.
     * @return true if added successfully, false if the username already exists.
     */
    public boolean addUser(User user) {
        if (!userDatabase.containsKey(user.getUserId())) {
            userDatabase.put(user.getUserId(), user);
            return true;
        }
        return false;
    }

    /**
     * Gets a list of all users in the system.
     * @return A list of users.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userDatabase.values());
    }

    /**
     * Checks if a username is available.
     * @param userId The username to check.
     * @return true if the username is available, false if taken.
     */
    public boolean isUsernameAvailable(String userId) {
        return !userDatabase.containsKey(userId);
    }

    /**
     * Removes a user from the system.
     * @param userId The user ID to remove.
     * @return true if the user was removed, false if not found.
     */
    public boolean removeUser(String userId) {
        if (userDatabase.containsKey(userId)) {
            userDatabase.remove(userId);
            return true;
        }
        return false;
    }
}

package movielist;

import java.util.*;
import java.sql.*;

/**
 * Manages movie lists for users, allowing them to create, view, update, and delete movies.
 * Also includes a feature to find movie matches with other users.
 */
public class MovieManager {
    private DatabaseManager dbManager;
    private Scanner scanner;
    private UserManager userManager;

    /**
     * Constructor to initialize MovieManager with a reference to UserManager.
     * @param userManager The user manager instance.
     */
    public MovieManager(UserManager userManager, DatabaseManager dbManager) {
        this.userManager = userManager;
        this.dbManager = dbManager;
    }


    /**
     * Allows a user to create a list of up to 10 favorite movies.
     * @param user The user creating the list.
     */
    public void createMovieList(User user) {
        System.out.println("Enter up to 10 favorite movies:");
        while (user.getFavoriteMovies().size() < 10) {
            System.out.print("Enter a movie: ");
            String movie = scanner.nextLine().trim();
            if (!movie.isEmpty()) {
                boolean added = user.addFavoriteMovie(movie);
                if (!added) {
                    System.out.println("Your movie list is already full.");
                    break;
                }
            } else {
                System.out.println("Invalid input. Please enter a valid movie name.");
            }

            if (user.getFavoriteMovies().size() == 10) {
                System.out.println("Your movie list is now full (10/10 movies).");
                break;
            }

            System.out.print("Add another? (Y/N): ");
            if (!scanner.nextLine().equalsIgnoreCase("Y")) break;
        }
    }

    /**
     * Displays a user's movie list and provides options to update or delete a movie.
     * @param user The user viewing their movie list.
     */
    public void viewMovieList(User user) {
        if (user.getFavoriteMovies().isEmpty()) {
            System.out.println("Your movie list is empty. Create one first.");
            return;
        }

        System.out.println("\nYour Movie List:");
        displayMovies(user);

        System.out.println("\nSelect an option:");
        System.out.println("1. Update a Movie");
        System.out.println("2. Delete a Movie");
        System.out.println("3. Return to Home Menu");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                updateMovie(user);
                break;
            case "2":
                deleteMovie(user);
                break;
            case "3":
                return;
            default:
                System.out.println("Invalid option. Returning to home menu.");
        }
    }

    /**
     * Adds a movie to the user's favorite list.
     * @param user The user adding the movie.
     * @param movie The name of the movie to add.
     * @return true if added successfully, false if the list is full.
     */
    public boolean addMovie(User user, String movie) {
        if (user.getFavoriteMovies().size() >= 20) return false;

        boolean added = user.addFavoriteMovie(movie);
        if (added) {
            try {
                DatabaseManager db = DatabaseManager.getInstance();
                Connection conn = db.getConnection();
                String sql = "INSERT INTO movies (user_id, title) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, db.getUserIdFromUsername(user.getUserId()));
                stmt.setString(2, movie);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error adding movie to database: " + e.getMessage());
                return false;
            }
        }
        return added;
    }



    /**
     * Updates a movie in the user's list.
     * @param user The user whose movie will be updated.
     * @param index The index to update.
     * @param newMovie The new movie name.
     * @return true if successful, false if the index is invalid.
     */
    public boolean updateMovie(User user, int index, String newMovie) {
        if (index < 0 || index >= user.getFavoriteMovies().size()) return false;

        String oldMovie = user.getFavoriteMovies().get(index);
        user.updateMovie(index, newMovie); // update in-memory list

        try {
            DatabaseManager db = DatabaseManager.getInstance();
            Connection conn = db.getConnection();
            String sql = "UPDATE movies SET title = ? WHERE user_id = ? AND title = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newMovie);
            stmt.setInt(2, db.getUserIdFromUsername(user.getUserId()));
            stmt.setString(3, oldMovie);
            int rowsUpdated = stmt.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating movie in database: " + e.getMessage());
            return false;
        }
    }



    /**
     * Deletes a movie from the user's list.
     * @param user The user whose movie will be deleted.
     * @param index The index of the movie to delete.
     * @return true if successful, false if the index is invalid.
     */
    public boolean deleteMovie(User user, int index) {
        if (index < 0 || index >= user.getFavoriteMovies().size()) return false;

        String movieTitle = user.getFavoriteMovies().get(index);
        user.deleteMovie(index); // remove from in-memory list

        try {
            DatabaseManager db = DatabaseManager.getInstance();
            Connection conn = db.getConnection();
            String sql = "DELETE FROM movies WHERE user_id = ? AND title = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, db.getUserIdFromUsername(user.getUserId()));
            stmt.setString(2, movieTitle);
            int rowsDeleted = stmt.executeUpdate();

            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting movie from database: " + e.getMessage());
            return false;
        }
    }



    /**
     * Finds similarity scores between a user and others based on favorite movies.
     * @param user The user searching for movie matches.
     * @return A map of user IDs to similarity scores.
     */
    public Map<String, Double> calculateSimilarityScore(User user) {
        Map<String, Double> similarityScores = new HashMap<>();

        for (User otherUser : userManager.getAllUsersFromDatabase()) {
            if (!otherUser.equals(user) && !otherUser.getFavoriteMovies().isEmpty()) {
                List<String> userMovies = dbManager.getMoviesForUser(user.getUserId());
                List<String> otherMovies = dbManager.getMoviesForUser(otherUser.getUserId());

                // Normalize movie titles before comparison
                userMovies.replaceAll(String::toLowerCase);
                otherMovies.replaceAll(String::toLowerCase);

                // Find common movies
                userMovies.retainAll(otherMovies);

                if (!userMovies.isEmpty()) {
                    double matchPercentage = (double) userMovies.size() / Math.max(user.getFavoriteMovies().size(), otherUser.getFavoriteMovies().size()) * 100;
                    similarityScores.put(otherUser.getUserId(), matchPercentage);
                }
            }
        }
        return similarityScores;
    }

    /**
     * Finds and displays movie matches for a given user.
     * @param user The user searching for movie matches.
     */
    public void movieMatches(User user) {
        Map<String, Double> similarityScores = calculateSimilarityScore(user);

        if (similarityScores.isEmpty()) {
            System.out.println("No movie matches found.");
            return;
        }

        System.out.println("\nMovie Matches:");
        similarityScores.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()) // Sort by highest match percentage
                .forEach(entry -> System.out.println("Match with " + entry.getKey() + " - Similarity Score: " + String.format("%.2f", entry.getValue()) + "%"));
    }

    /**
     * Displays a user's movie list with numbering.
     * @param user The user whose movie list is displayed.
     */
    private void displayMovies(User user) {
        List<String> movies = user.getFavoriteMovies();
        for (int i = 0; i < movies.size(); i++) {
            System.out.println((i + 1) + ". " + movies.get(i));
        }
    }

    /**
     * Gets a valid movie index from user input.
     * @param maxIndex The maximum valid index.
     * @return The selected index (0-based), or -1 if input is invalid.
     */
    private int getValidIndex(int maxIndex) {
        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice < 0 || choice >= maxIndex) {
                System.out.println("Invalid choice. Returning to home menu.");
                return -1;
            }
            return choice;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }

    /**
     * Overloaded method for updating a movie, handles user input.
     * @param user The user updating their movie list.
     */
    public void updateMovie(User user) {
        if (user.getFavoriteMovies().isEmpty()) {
            System.out.println("Your movie list is empty. Nothing to update.");
            return;
        }

        displayMovies(user);
        int index = getValidIndex(user.getFavoriteMovies().size());
        if (index == -1) return;

        System.out.print("Enter the new movie name: ");
        String newMovie = scanner.nextLine().trim();

        if (!newMovie.isEmpty()) {
            String oldMovie = user.getFavoriteMovies().get(index);
            user.updateMovie(index, newMovie);

            try {
                DatabaseManager db = DatabaseManager.getInstance();
                Connection conn = db.getConnection();
                String sql = "UPDATE movies SET title = ? WHERE user_id = ? AND title = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, newMovie);
                stmt.setInt(2, db.getUserIdFromUsername(user.getUserId()));
                stmt.setString(3, oldMovie);
                stmt.executeUpdate();
                System.out.println("Movie updated successfully!");
            } catch (SQLException e) {
                System.err.println("Error updating movie in database: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid input. Update canceled.");
        }
    }


    /**
     * Overloaded method for deleting a movie, handles user input.
     * @param user The user deleting a movie.
     */
    public void deleteMovie(User user) {
        if (user.getFavoriteMovies().isEmpty()) {
            System.out.println("Your movie list is empty. Nothing to delete.");
            return;
        }

        displayMovies(user);
        int index = getValidIndex(user.getFavoriteMovies().size());
        if (index == -1) return;

        String movieToDelete = user.getFavoriteMovies().get(index);
        user.deleteMovie(index);

        try {
            DatabaseManager db = DatabaseManager.getInstance();
            Connection conn = db.getConnection();
            String sql = "DELETE FROM movies WHERE user_id = ? AND title = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, db.getUserIdFromUsername(user.getUserId()));
            stmt.setString(2, movieToDelete);
            stmt.executeUpdate();
            System.out.println("Movie removed successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting movie from database: " + e.getMessage());
        }
    }


    public void loadUserMoviesFromDatabase() {
        Connection conn = DatabaseConnector.getConnection();
        if (conn == null) {
            System.out.println("No database connection.");
            return;
        }

        String query = "SELECT u.username, m.title FROM users u JOIN movies m ON u.id = m.user_id";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String username = rs.getString("username");
                String movieTitle = rs.getString("title");

                User user = userManager.getUser(username);
                if (user != null) {
                    user.addFavoriteMovie(movieTitle);
                }
            }
            System.out.println("Movies loaded from database.");
        } catch (SQLException e) {
            System.out.println("Error loading movies from DB: " + e.getMessage());
        }
    }

    public boolean addMovieToDatabase(User user, String title) {
        Connection conn = DatabaseConnector.getConnection();
        if (conn == null) return false;

        String insert = "INSERT INTO movies (user_id, title) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setInt(1, user.getDatabaseId()); // This must be the user’s ID from the DB
            stmt.setString(2, title);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to add movie to DB: " + e.getMessage());
            return false;
        }
    }
    public boolean updateMovieInDatabase(User user, String oldTitle, String newTitle) {
        Connection conn = DatabaseConnector.getConnection();
        if (conn == null) return false;

        String update = "UPDATE movies SET title = ? WHERE user_id = ? AND title = ?";

        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, newTitle);
            stmt.setInt(2, user.getDatabaseId());
            stmt.setString(3, oldTitle);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.out.println("Failed to update movie: " + e.getMessage());
            return false;
        }
    }
    public boolean deleteMovieFromDatabase(User user, String title) {
        Connection conn = DatabaseConnector.getConnection();
        if (conn == null) return false;

        String delete = "DELETE FROM movies WHERE user_id = ? AND title = ?";

        try (PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setInt(1, user.getDatabaseId());
            stmt.setString(2, title);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            System.out.println("Failed to delete movie: " + e.getMessage());
            return false;
        }
    }
    public void loadUserMoviesFromDB(User user) {
        List<String> movies = dbManager.getMoviesForUser(user.getUserId());
        for (String movie : movies) {
            user.addFavoriteMovie(movie);
        }
    }

}

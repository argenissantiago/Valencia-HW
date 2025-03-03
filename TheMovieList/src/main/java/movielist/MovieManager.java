package movielist;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages movie lists for users, allowing them to create, view, update, and delete movies.
 * Also includes a feature to find movie matches with other users.
 */
public class MovieManager {
    private Scanner scanner;
    private Map<String, User> userDatabase;

    /**
     * Constructor to initialize MovieManager with a user database.
     * @param userDatabase The database of users.
     */
    public MovieManager(Map<String, User> userDatabase) {
        this.scanner = new Scanner(System.in);
        this.userDatabase = userDatabase;
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
                updateMovie(user); // Call overloaded method that handles user input
                break;
            case "2":
                deleteMovie(user); // Call overloaded method that handles user input
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
        return user.addFavoriteMovie(movie);
    }

    /**
     * Updates a movie in the user's list.
     * @param user The user whose movie will be updated.
     * @param index The index to update.
     * @param newMovie The new movie name.
     * @return true if successful, false if the index is invalid.
     */
    public boolean updateMovie(User user, int index, String newMovie) {
        return user.updateMovie(index, newMovie);
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
            boolean updated = updateMovie(user, index, newMovie);
            if (updated) {
                System.out.println("Movie updated successfully!");
            } else {
                System.out.println("Update failed. Please try again.");
            }
        } else {
            System.out.println("Invalid input. Update canceled.");
        }
    }

    /**
     * Deletes a movie from the user's list.
     * @param user The user whose movie will be deleted.
     * @param index The index of the movie to delete.
     * @return true if successful, false if the index is invalid.
     */
    public boolean deleteMovie(User user, int index) {
        return user.deleteMovie(index);
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

        boolean deleted = deleteMovie(user, index);
        if (deleted) {
            System.out.println("Movie removed successfully!");
        } else {
            System.out.println("Deletion failed. Please try again.");
        }
    }

    /**
     * Finds similarity scores between a user and others based on favorite movies.
     * @param user The user searching for movie matches.
     * @return A map of user IDs to similarity scores.
     */
    public Map<String, Double> calculateSimilarityScore(User user) {
        Map<String, Double> similarityScores = new HashMap<>();

        for (User otherUser : userDatabase.values()) {
            if (!otherUser.equals(user) && !otherUser.getFavoriteMovies().isEmpty()) {
                List<String> commonMovies = new ArrayList<>(user.getFavoriteMovies());
                commonMovies.retainAll(otherUser.getFavoriteMovies());

                if (!commonMovies.isEmpty()) {
                    double matchPercentage = (double) commonMovies.size() / Math.min(user.getFavoriteMovies().size(), otherUser.getFavoriteMovies().size()) * 100;
                    similarityScores.put(otherUser.getUserId(), matchPercentage);
                }
            }
        }
        return similarityScores;
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
     * Finds and displays movie matches for a given user.
     * This method is a wrapper around calculateSimilarityScore() to maintain compatibility with Main.java.
     * @param user The user searching for movie matches.
     */
    public void movieMatches(User user) {
        Map<String, Double> similarityScores = calculateSimilarityScore(user);

        if (similarityScores.isEmpty()) {
            System.out.println("No movie matches found.");
            return;
        }

        System.out.println("\nMovie Matches:");
        for (Map.Entry<String, Double> entry : similarityScores.entrySet()) {
            System.out.println("Match with " + entry.getKey() + " - Similarity Score: " + String.format("%.2f", entry.getValue()) + "%");
        }
    }

}

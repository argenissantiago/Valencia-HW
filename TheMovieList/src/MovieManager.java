import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MovieManager {
    private Scanner scanner;
    private Map<String, User> userDatabase;

    public MovieManager(Map<String, User> userDatabase) {
        this.scanner = new Scanner(System.in);
        this.userDatabase = userDatabase;
    }

    /**
     * Allows the user to create a movie list by adding up to 10 movies.
     */
    public void createMovieList(User user) {
        System.out.println("Enter up to 10 favorite movies:");
        while (user.getFavoriteMovies().size() < 10) {
            System.out.print("Enter a movie: ");
            String movie = scanner.nextLine();
            user.addFavoriteMovie(movie);

            if (user.getFavoriteMovies().size() == 10) {
                System.out.println("Your movie list is now full (10/10 movies).");
                break;
            }

            System.out.print("Add another? (Y/N): ");
            if (!scanner.nextLine().equalsIgnoreCase("Y")) break;
        }
    }

    /**
     * Displays the user's movie list and provides options to update or delete a movie.
     */
    public void viewMovieList(User user) {
        if (user.getFavoriteMovies().isEmpty()) {
            System.out.println("Your movie list is empty. Create one first.");
            return;
        }

        System.out.println("\nYour Movie List:");
        List<String> movies = user.getFavoriteMovies();
        for (int i = 0; i < movies.size(); i++) {
            System.out.println((i + 1) + ". " + movies.get(i));
        }

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
     * Allows the user to update a movie in their list.
     */
    public void updateMovie(User user) {
        List<String> movies = user.getFavoriteMovies();
        if (movies.isEmpty()) {
            System.out.println("Your movie list is empty. Nothing to update.");
            return;
        }

        System.out.println("Select a movie number to update:");
        for (int i = 0; i < movies.size(); i++) {
            System.out.println((i + 1) + ". " + movies.get(i));
        }

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice < 0 || choice >= movies.size()) {
                System.out.println("Invalid choice. Returning to home menu.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        System.out.print("Enter the new movie name: ");
        String newMovie = scanner.nextLine();
        user.updateMovie(choice, newMovie);
        System.out.println("Movie updated successfully!");
    }

    /**
     * Allows the user to delete a movie from their list.
     */
    public void deleteMovie(User user) {
        List<String> movies = user.getFavoriteMovies();
        if (movies.isEmpty()) {
            System.out.println("Your movie list is empty. Nothing to delete.");
            return;
        }

        System.out.println("Select a movie number to delete:");
        for (int i = 0; i < movies.size(); i++) {
            System.out.println((i + 1) + ". " + movies.get(i));
        }

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice < 0 || choice >= movies.size()) {
                System.out.println("Invalid choice. Returning to home menu.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        user.deleteMovie(choice);
        System.out.println("Movie removed successfully!");
    }

    /**
     * Custom feature: Finds movie matches with other users and calculates a similarity percentage.
     */
    public void movieMatches(User user) {
        if (user.getFavoriteMovies().isEmpty()) {
            System.out.println("You have no favorite movies. Add some before searching for matches.");
            return;
        }

        System.out.println("\n Checking all users and their movies...");

        boolean foundMatch = false;

        for (User otherUser : userDatabase.values()) {
            if (!otherUser.equals(user) && !otherUser.getFavoriteMovies().isEmpty()) {
                // Copy movie list before modifying it
                List<String> commonMovies = new ArrayList<>(user.getFavoriteMovies());
                commonMovies.retainAll(otherUser.getFavoriteMovies()); // Find common movies

                if (!commonMovies.isEmpty()) {
                    double matchPercentage = (double) commonMovies.size() / Math.min(user.getFavoriteMovies().size(), otherUser.getFavoriteMovies().size()) * 100;
                    System.out.println("Movie match with " + otherUser.getUserId() + ": " + commonMovies);
                    System.out.println("Similarity Score: " + String.format("%.2f", matchPercentage) + "%");
                    foundMatch = true;
                }
            }
        }

        if (!foundMatch) {
            System.out.println("No movie matches found.");
        }
    }
}

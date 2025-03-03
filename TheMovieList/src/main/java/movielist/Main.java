package movielist;
import java.util.Scanner;

/**
 * Entry point for the movie list application.
 * Manages user interactions, authentication, and navigation.
 */

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static LoginManager loginManager = new LoginManager();
    private static MovieManager movieManager = new MovieManager(loginManager.getUserDatabase());

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Sign In");
            System.out.println("2. Sign Up");
            System.out.println("3. Load Users from File");
            System.out.println("4. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    User user = loginManager.signIn();
                    if (user != null) {
                        homePage(user);
                    }
                    break;
                case "2":
                    loginManager.signUp();
                    break;
                case "3":
                    loginManager.loadUsersFromFile(); // User enters filename, and data is stored in memory
                    break;
                case "4":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }


    private static void homePage(User user) {
        while (true) {
            System.out.println("\nThe Movie List");
            System.out.println("1. View/Update My Movie List");
            System.out.println("2. Create My Movie List");
            System.out.println("3. View My Movie Matches");
            System.out.println("4. Log Out");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    movieManager.viewMovieList(user);
                    break;
                case "2":
                    movieManager.createMovieList(user);
                    break;
                case "3":
                    movieManager.movieMatches(user);
                    break;
                case "4":
                    loginManager.logout();
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}

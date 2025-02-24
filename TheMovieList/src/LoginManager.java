import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoginManager {
    private Map<String, User> userDatabase;
    private Scanner scanner;
    private User currentUser;

    public LoginManager() {
        this.userDatabase = new HashMap<>();
        this.scanner = new Scanner(System.in);
    }

    public void loadUsersFromFile() {
        System.out.print("Enter the filename: ");
        String filename = scanner.nextLine();

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

                    // Store user in the database immediately
                    userDatabase.put(parts[0], user);
                }
            }
            System.out.println("Users loaded successfully and stored in the database.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }



    public User signIn() {
        System.out.print("Enter ID: ");
        String id = scanner.nextLine();

        if (!userDatabase.containsKey(id)) {
            System.out.println("User not found. Try again.");
            return null;
        }

        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        User user = userDatabase.get(id);
        if (user.getHashedPin().equals(pin)) {
            System.out.println("Login successful!");
            return user;
        } else {
            System.out.println("Invalid credentials. Try again.");
            return null;
        }
    }

    public void signUp() {
        System.out.print("Enter Username: ");
        String id = scanner.nextLine();

        if (userDatabase.containsKey(id)) {
            System.out.println("Username already taken. Try again.");
            return;
        }

        System.out.print("Enter a 4-digit PIN: ");
        String pin = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Age: ");
        int age = Integer.parseInt(scanner.nextLine());

        if (pin.length() == 4 && pin.matches("\\d+")) {
            User newUser = new User(id, pin, email, age);
            userDatabase.put(id, newUser);

            saveUserToFile(newUser);

            System.out.println("Account created! Please log in.");
        } else {
            System.out.println("Invalid PIN format. Try again.");
        }
    }

    public void logout() {
        System.out.println("Logging out...");
        currentUser = null;
    }

    public void saveUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(user.getUserId() + "," + user.getHashedPin() + "," + user.getEmail() + "," + user.getAge() + "," +
                    (user.getFavoriteMovies().isEmpty() ? "None" : String.join(";", user.getFavoriteMovies())));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving user to file.");
        }
    }

    public Map<String, User> getUserDatabase() {
        return userDatabase;
    }
}

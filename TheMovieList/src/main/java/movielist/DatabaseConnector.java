package movielist;

import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

/**
 * The {@code DatabaseConnector} class establishes a connection
 * to a MySQL database via user input.
 * <p>
 * A static method initiates the connection and retrieves it
 * throughout the application.
 */
public class DatabaseConnector {
    private static Connection connection;

    /**
     * Returns the current established connection, or {@code null}
     * if no connection has been established.
     *
     * @return the current database connection
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Prompts the user for connection details (host, port, database name,
     * username, and password) and attempts to establish
     * a connection to the specified database.
     * <p>
     * Displays an alert indicating success or failure.
     *
     * @return the established database connection, or {@code null} if the connection fails
     */
    public static Connection connect() {
        try {
            String host = prompt("Enter MySQL host (e.g., localhost):");
            if (host == null) return null;

            String port = prompt("Enter MySQL port (default is 3306):");
            if (port == null || port.isEmpty()) port = "3306";

            String database = prompt("Enter database name:");
            if (database == null) return null;

            String username = prompt("Enter MySQL username:");
            if (username == null) return null;

            String password = prompt("Enter MySQL password:");
            if (password == null) return null;

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC";
            connection = DriverManager.getConnection(url, username, password);
            showAlert(AlertType.INFORMATION, "Success", "Connected to database successfully.");
            return connection;

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Connection Error", "Could not connect: " + e.getMessage());
            return null;
        }
    }

    /**
     * Prompts the user to enter input and returns the input.
     * Returns {@code null} if the input is empty.
     *
     * @param message the message to display in the dialog
     * @return the user's input, or {@code null} if empty
     */
    private static String prompt(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Database Connection");
        dialog.setHeaderText(message);
        Optional<String> result = dialog.showAndWait();
        return result.filter(s -> !s.trim().isEmpty()).orElse(null);
    }

    /**
     * Displays an alert with the type, title, and content.
     *
     * @param type    the type of alert (e.g., INFORMATION, ERROR)
     * @param title   the title of the alert window
     * @param content the message content of the alert
     */
    private static void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

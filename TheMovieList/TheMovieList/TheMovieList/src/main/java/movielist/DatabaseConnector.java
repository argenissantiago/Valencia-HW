package movielist;

import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class DatabaseConnector {
    private static Connection connection;

    public static Connection getConnection() {
        return connection;
    }

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

    private static String prompt(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Database Connection");
        dialog.setHeaderText(message);
        Optional<String> result = dialog.showAndWait();
        return result.filter(s -> !s.trim().isEmpty()).orElse(null);
    }

    private static void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

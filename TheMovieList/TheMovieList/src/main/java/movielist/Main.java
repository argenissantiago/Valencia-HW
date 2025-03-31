package movielist;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;



public class Main extends Application {
    private static UserManager userManager;
    private static MovieManager movieManager;
    private static DatabaseManager databaseManager;

    public static void main(String[] args) {

        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        TextInputDialog hostDialog = new TextInputDialog("localhost");
        hostDialog.setTitle("Database Connection");
        hostDialog.setHeaderText("Enter MySQL Host:");
        String host = hostDialog.showAndWait().orElse("").trim();

        TextInputDialog userDialog = new TextInputDialog("root");
        userDialog.setTitle("Database Connection");
        userDialog.setHeaderText("Enter MySQL Username:");
        String dbUser = userDialog.showAndWait().orElse("").trim();

        TextInputDialog passDialog = new TextInputDialog();
        passDialog.setTitle("Database Connection");
        passDialog.setHeaderText("Enter MySQL Password:");
        String dbPass = passDialog.showAndWait().orElse("").trim();

        try {
            // Connect to DB and load data
            databaseManager.connect(host, dbUser, dbPass);
            userManager = databaseManager.loadUsers();
            databaseManager.loadMoviesIntoUsers(userManager);

            movieManager = new MovieManager(userManager, databaseManager);
            UserGUI.setManagers(userManager, movieManager);

            UserGUI userGUI = new UserGUI();
            userGUI.promptDatabaseConnection(primaryStage);
        } catch (Exception e) {
            showError("Startup Error", "Failed to connect or load data: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

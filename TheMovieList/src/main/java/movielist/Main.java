package movielist;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private static UserManager userManager;
    private static MovieManager movieManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize UserManager and MovieManager
        userManager = new UserManager();
        movieManager = new MovieManager(userManager);

        // Set managers in UserGUI before launching it
        UserGUI.setManagers(userManager, movieManager);

        // Create an instance of UserGUI and launch it
        UserGUI userGUI = new UserGUI();
        userGUI.start(primaryStage);
    }
}

package movielist;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The {@code MovieListGUI} class provides a JavaFX-based user interface
 * that allows users to view, add, update, and delete their favorite movies.
 * <p>
 * This GUI displays a list of the user's movies and interacts
 * with the {@code MovieManager} to execute changes.
 */

public class MovieListGUI extends Application {
    private User user;
    private MovieManager movieManager;
    private ListView<String> movieListView;
    private ObservableList<String> movieList;

    /**
     * Constructs a new {@code MovieListGUI} for the user and movie manager.
     *
     * @param user         the user whose movie list will be displayed and altered
     * @param movieManager the movie manager that handles movie operations
     */

    public MovieListGUI(User user, MovieManager movieManager) {
        this.user = user;
        this.movieManager = movieManager;
    }

    /**
     * Initializes and displays the JavaFX user interface for managing the user's movie list.
     *
     * @param primaryStage the primary window for this JavaFX application
     */

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(user.getUserId() + "'s Movie List");

        movieList = FXCollections.observableArrayList(user.getFavoriteMovies());
        movieListView = new ListView<>(movieList);

        TextField movieInput = new TextField();
        movieInput.setPromptText("Enter a movie...");

        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        Button backButton = new Button("Back");

        addButton.setOnAction(e -> addMovie(movieInput));
        updateButton.setOnAction(e -> updateMovie(movieInput));
        deleteButton.setOnAction(e -> deleteMovie());
        backButton.setOnAction(e -> primaryStage.close());

        HBox inputBox = new HBox(10, movieInput, addButton);
        inputBox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(10, updateButton, deleteButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, movieListView, inputBox, buttonBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Adds a new movie to the user's list if the input is valid and not a duplicate.
     *
     * @param movieInput the text field containing the movie name to add
     */

    private void addMovie(TextField movieInput) {
        String movie = movieInput.getText().trim();
        if (!movie.isEmpty() && movieManager.addMovie(user, movie)) {
            movieList.add(movie);
            movieInput.clear();
        } else {
            showAlert("Movie list is full or duplicate movie.");
        }
    }

    /**
     * Updates the selected movie in the list with the new movie name from the input field.
     *
     * @param movieInput the text field containing the new movie name
     */

    private void updateMovie(TextField movieInput) {
        int selectedIndex = movieListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String newMovie = movieInput.getText().trim();
            if (!newMovie.isEmpty() && movieManager.updateMovie(user, selectedIndex, newMovie)) {
                movieList.set(selectedIndex, newMovie);
                movieInput.clear();
            } else {
                showAlert("Invalid update.");
            }
        } else {
            showAlert("Select a movie to update.");
        }
    }

    /**
     * Deletes the selected movie from the user's list.
     */

    private void deleteMovie() {
        int selectedIndex = movieListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            movieManager.deleteMovie(user, selectedIndex);
            movieList.remove(selectedIndex);
        } else {
            showAlert("Select a movie to delete.");
        }
    }

    /**
     * Displays a warning alert.
     *
     * @param message the content message to show in the alert
     */

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Launches the movie list GUI in a new window for the user.
     *
     * @param user         the user whose movie list will be displayed
     * @param movieManager the movie manager that will handle movie operations
     */


    public static void launchGUI(User user, MovieManager movieManager) {
        new MovieListGUI(user, movieManager).start(new Stage());
    }
}

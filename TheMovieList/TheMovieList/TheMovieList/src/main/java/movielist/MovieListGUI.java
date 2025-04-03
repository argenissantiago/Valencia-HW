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

public class MovieListGUI extends Application {
    private User user;
    private MovieManager movieManager;
    private ListView<String> movieListView;
    private ObservableList<String> movieList;

    public MovieListGUI(User user, MovieManager movieManager) {
        this.user = user;
        this.movieManager = movieManager;
    }

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

    private void addMovie(TextField movieInput) {
        String movie = movieInput.getText().trim();
        if (!movie.isEmpty() && movieManager.addMovie(user, movie)) {
            movieList.add(movie);
            movieInput.clear();
        } else {
            showAlert("Movie list is full or duplicate movie.");
        }
    }

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

    private void deleteMovie() {
        int selectedIndex = movieListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            movieManager.deleteMovie(user, selectedIndex);
            movieList.remove(selectedIndex);
        } else {
            showAlert("Select a movie to delete.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void launchGUI(User user, MovieManager movieManager) {
        new MovieListGUI(user, movieManager).start(new Stage());
    }
}

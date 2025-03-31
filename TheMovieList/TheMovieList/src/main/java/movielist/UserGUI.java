package movielist;

        import javafx.application.Application;
        import javafx.geometry.Insets;
        import javafx.scene.Scene;
        import javafx.scene.control.*;
        import javafx.scene.layout.VBox;
        import javafx.scene.layout.HBox;
        import javafx.stage.Stage;
        import javafx.stage.FileChooser;
        import javafx.beans.property.SimpleStringProperty;
        import javafx.beans.property.SimpleIntegerProperty;
        import java.io.File;
        import java.io.IOException;
        import java.util.Map;
        import javafx.scene.control.TextInputDialog;
        import javafx.scene.control.TableView;
        import javafx.scene.control.Alert;
        import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;



public class UserGUI extends Application {
    // UserManager and MovieManager handle user data and movie lists
    private static UserManager userManager = new UserManager(DatabaseManager.getInstance());
    private static MovieManager movieManager = new MovieManager(userManager, DatabaseManager.getInstance());

    /**
     * Allows external classes to set UserManager and MovieManager instances.
     * This ensures that data persists across the application.
     */
    public static void setManagers(UserManager um, MovieManager mm) {
        userManager = um;
        movieManager = mm;
    }
    /**
     * Starts the JavaFX application by displaying the login screen.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Connect to Database");
        promptDatabaseConnection(primaryStage);
    }


    private void handleFileLoad(Stage primaryStage) {
        VBox fileLoadLayout = new VBox(15);
        fileLoadLayout.setPadding(new Insets(20));
        fileLoadLayout.setAlignment(javafx.geometry.Pos.CENTER);

        Label instructionLabel = new Label("Enter file path or click 'Browse' to select:");
        TextField filePathField = new TextField();
        filePathField.setPromptText("Enter file path here...");

        Button browseButton = new Button("Browse");
        Button loadButton = new Button("Load File");
        Button backButton = new Button("Back to Sign-In");

        // File chooser for browsing files
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select User Data File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        // Load the file when user enters path manually or selects it
        loadButton.setOnAction(e -> {
            String filePath = filePathField.getText().trim();
            if (filePath.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a file path.");
                return;
            }

            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid file path. Please enter a valid file location.");
                return;
            }

            userManager.loadUsersFromDatabase();
            showFileLoadConfirmation(primaryStage);

        });

        backButton.setOnAction(e -> showLoginScreen(primaryStage));

        fileLoadLayout.getChildren().addAll(instructionLabel, filePathField, browseButton, loadButton, backButton);
        Scene fileLoadScene = new Scene(fileLoadLayout, 400, 250);
        primaryStage.setScene(fileLoadScene);
    }



    private void updateUserTable() {
        Stage stage = new Stage();
        stage.setTitle("Loaded Users");

        TableView<User> table = new TableView<>();
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserId()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<User, Integer> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAge()).asObject());

        table.getColumns().addAll(usernameCol, emailCol, ageCol);
        table.getItems().addAll(userManager.getAllUsers());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> stage.close());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(table, closeButton);

        stage.setScene(new Scene(layout, 450, 350));
        stage.show();
    }
    /**
     * Handles user login and navigates to the user dashboard.
     */
    private void handleLogin(Stage primaryStage, String username, String pin) {
        if (username.isEmpty() || pin.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Username and PIN cannot be empty.");
            return;
        }

        try {
            DatabaseManager db = DatabaseManager.getInstance();
            Connection conn = db.getConnection();
            String sql = "SELECT * FROM users WHERE username = ? AND pin = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, pin);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Recreate User object and store it in memory
                String email = rs.getString("email");
                int age = rs.getInt("age");
                User user = new User(username, pin, email, age);
                userManager.addUser(user);  // Re-populate local memory with this user

                // Load their favorite movies from the database
                int userId = rs.getInt("id");
                sql = "SELECT title FROM movies WHERE user_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userId);
                ResultSet movieResults = stmt.executeQuery();
                while (movieResults.next()) {
                    user.addFavoriteMovie(movieResults.getString("title"));
                }

                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + username + "!");
                openUserDashboard(primaryStage, user);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials. Try again.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not log in: " + e.getMessage());
        }
    }

    /**
     * Handles user sign-up process, including email and age input validation.
     */
    private void handleSignUp(Stage primaryStage, String username, String pin) {
        if (userManager == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "UserManager is not initialized.");
            return;
        }

        if (username.isEmpty() || pin.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "Username and PIN cannot be empty.");
            return;
        }

        if (pin.length() != 4 || !pin.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid PIN", "PIN must be exactly 4 digits.");
            return;
        }

        if (userManager.isUsernameAvailable(username)) {
            TextInputDialog emailDialog = new TextInputDialog();
            emailDialog.setTitle("Sign Up");
            emailDialog.setHeaderText("Enter your email:");
            String email = emailDialog.showAndWait().orElse("").trim();

            TextInputDialog ageDialog = new TextInputDialog();
            ageDialog.setTitle("Sign Up");
            ageDialog.setHeaderText("Enter your age:");
            String ageInput = ageDialog.showAndWait().orElse("").trim();

            try {
                int age = Integer.parseInt(ageInput);
                if (age < 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Age", "Age cannot be negative.");
                    return;
                }

                // Save user in the database
                try {
                    DatabaseManager db = DatabaseManager.getInstance();
                    Connection conn = db.getConnection();
                    String sql = "INSERT INTO users (username, pin, email, age) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, username);
                    stmt.setString(2, pin);
                    stmt.setString(3, email);
                    stmt.setInt(4, age);
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save user to database: " + ex.getMessage());
                    return;
                }

                // Add to memory
                User newUser = new User(username, pin, email, age);
                userManager.addUser(newUser);

                showAlert(Alert.AlertType.INFORMATION, "Account Created", "Your account has been created! Please log in.");
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Age", "Please enter a valid number.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "Username already taken.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void openUserDashboard(Stage primaryStage, User user) {
        VBox dashboard = new VBox(10);
        dashboard.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Welcome, " + user.getUserId() + "!");

        Button viewMoviesButton = new Button("View Movie List");
        Button findMatchesButton = new Button("Find Movie Matches");
        Button logoutButton = new Button("Log Out");

        viewMoviesButton.setOnAction(e -> openMovieList(primaryStage, user));
        findMatchesButton.setOnAction(e -> showMovieMatches(primaryStage, user));
        logoutButton.setOnAction(e -> primaryStage.setScene(createLoginScene(primaryStage)));

        dashboard.getChildren().addAll(welcomeLabel, viewMoviesButton, findMatchesButton, logoutButton);

        Scene dashboardScene = new Scene(dashboard, 350, 300);
        primaryStage.setScene(dashboardScene);
    }
    private void openMovieList(Stage primaryStage, User user) {
        Stage movieStage = new Stage();
        movieStage.setTitle(user.getUserId() + "'s Movie List");

        TableView<String> table = new TableView<>();
        TableColumn<String, String> movieCol = new TableColumn<>("Movie Title");
        movieCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        table.getColumns().add(movieCol);
        table.getItems().addAll(user.getFavoriteMovies());

        // Buttons
        Button addMovieButton = new Button("Add Movie");
        Button updateMovieButton = new Button("Update Movie");
        Button deleteMovieButton = new Button("Delete Movie");
        Button closeButton = new Button("Close");

        // Button Actions
        addMovieButton.setOnAction(e -> addMovie(user, table));
        updateMovieButton.setOnAction(e -> updateMovie(user, table));
        deleteMovieButton.setOnAction(e -> deleteMovie(user, table));
        closeButton.setOnAction(e -> movieStage.close());

        // Layout
        HBox buttonLayout = new HBox(10, addMovieButton, updateMovieButton, deleteMovieButton, closeButton);
        buttonLayout.setPadding(new Insets(10));
        buttonLayout.setAlignment(javafx.geometry.Pos.CENTER);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(table, buttonLayout);

        Scene scene = new Scene(layout, 450, 350);
        movieStage.setScene(scene);
        movieStage.show();
    }
    private void showMovieMatches(Stage primaryStage, User user) {
        Stage matchStage = new Stage();
        matchStage.setTitle("Movie Matches for " + user.getUserId());

        TableView<Map.Entry<String, Double>> table = new TableView<>();
        TableColumn<Map.Entry<String, Double>, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));

        TableColumn<Map.Entry<String, Double>, String> scoreCol = new TableColumn<>("Similarity Score (%)");
        scoreCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().getValue())));

        table.getColumns().addAll(userCol, scoreCol);

        // Fetch similarity scores and add to table
        Map<String, Double> matches = movieManager.calculateSimilarityScore(user);
        table.getItems().addAll(matches.entrySet());

        // Close Button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> matchStage.close());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(table, closeButton);

        Scene scene = new Scene(layout, 400, 350);
        matchStage.setScene(scene);
        matchStage.show();
    }
    private Scene createLoginScene(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label pinLabel = new Label("PIN:");
        PasswordField pinField = new PasswordField();

        Button loginButton = new Button("Sign In");
        Button signUpButton = new Button("Sign Up");
        Button loadFileButton = new Button("Load Users from File");

        loginButton.setOnAction(e -> handleLogin(primaryStage, usernameField.getText(), pinField.getText()));
        signUpButton.setOnAction(e -> handleSignUp(primaryStage, usernameField.getText(), pinField.getText()));
        loadFileButton.setOnAction(e -> handleFileLoad(primaryStage));

        layout.getChildren().addAll(usernameLabel, usernameField, pinLabel, pinField, loginButton, signUpButton, loadFileButton);

        return new Scene(layout, 350, 300);
    }
    private void addMovie(User user, TableView<String> table) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Movie");
        dialog.setHeaderText("Enter a movie name:");
        String newMovie = dialog.showAndWait().orElse("").trim();

        if (!newMovie.isEmpty()) {
            boolean added = movieManager.addMovie(user, newMovie);
            if (added) {
                table.getItems().setAll(user.getFavoriteMovies());
            } else {
                showAlert(Alert.AlertType.ERROR, "Movie List Full", "You can only have 10 movies.");
            }
        }
    }

    private void updateMovie(User user, TableView<String> table) {
        String selectedMovie = table.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showAlert(Alert.AlertType.WARNING, "No Movie Selected", "Select a movie to update.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedMovie);
        dialog.setTitle("Update Movie");
        dialog.setHeaderText("Enter a new movie name:");
        String newMovie = dialog.showAndWait().orElse("").trim();

        if (!newMovie.isEmpty()) {
            int index = user.getFavoriteMovies().indexOf(selectedMovie);
            boolean success = movieManager.updateMovie(user, index, newMovie);
            if (success) {
                table.getItems().setAll(user.getFavoriteMovies());
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update movie.");
            }
        }
    }

    private void deleteMovie(User user, TableView<String> table) {
        String selectedMovie = table.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showAlert(Alert.AlertType.WARNING, "No Movie Selected", "Select a movie to delete.");
            return;
        }

        int index = user.getFavoriteMovies().indexOf(selectedMovie);
        boolean success = movieManager.deleteMovie(user, index);
        if (success) {
            table.getItems().setAll(user.getFavoriteMovies());
        } else {
            showAlert(Alert.AlertType.ERROR, "Delete Failed", "Could not remove movie.");
        }
    }

    /**
     * Displays the login/sign-up screen.
     */
    private void showLoginScreen(Stage primaryStage) {
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label pinLabel = new Label("PIN:");
        PasswordField pinField = new PasswordField();

        Button loginButton = new Button("Sign In");
        Button signUpButton = new Button("Sign Up");
        Button loadFileButton = new Button("Load Users from File");
        Button exitButton = new Button("Exit");

        loginButton.setOnAction(e -> handleLogin(primaryStage, usernameField.getText(), pinField.getText()));
        signUpButton.setOnAction(e -> handleSignUp(primaryStage, usernameField.getText(), pinField.getText()));
        loadFileButton.setOnAction(e -> handleFileLoad(primaryStage));
        exitButton.setOnAction(e -> System.exit(0)); // Exits the program

        loginLayout.getChildren().addAll(usernameLabel, usernameField, pinLabel, pinField, loginButton, signUpButton, loadFileButton, exitButton);
        Scene loginScene = new Scene(loginLayout, 350, 350);
        primaryStage.setScene(loginScene);
    }
    /**
     * Displays a confirmation message after successfully loading users.
     */
    private void showFileLoadConfirmation(Stage primaryStage) {
        VBox messageLayout = new VBox(15);
        messageLayout.setPadding(new Insets(20));
        messageLayout.setAlignment(javafx.geometry.Pos.CENTER);

        Label confirmationLabel = new Label("Users have been added.");
        Button returnButton = new Button("Return to Sign-In Screen");
        returnButton.setOnAction(e -> showLoginScreen(primaryStage));

        messageLayout.getChildren().addAll(confirmationLabel, returnButton);
        Scene confirmationScene = new Scene(messageLayout, 350, 200);
        primaryStage.setScene(confirmationScene);
    }
    public void promptDatabaseConnection(Stage primaryStage) {
        VBox dbLayout = new VBox(10);
        dbLayout.setPadding(new Insets(20));

        TextField hostField = new TextField("localhost");
        hostField.setPromptText("MySQL Host (e.g. localhost)");

        TextField userField = new TextField("root");
        userField.setPromptText("MySQL Username");

        PasswordField passField = new PasswordField();
        passField.setPromptText("MySQL Password");

        Button connectButton = new Button("Connect");

        Label statusLabel = new Label();

        connectButton.setOnAction(e -> {
            String host = hostField.getText().trim();
            String user = userField.getText().trim();
            String pass = passField.getText();

            if (host.isEmpty() || user.isEmpty()) {
                statusLabel.setText("Host and username are required.");
                return;
            }

            try {
                DatabaseManager db = DatabaseManager.getInstance();
                db.connect(host, user, pass);

                userManager.loadUsersFromDatabase();
                showLoginScreen(primaryStage);
            } catch (Exception ex) {
                statusLabel.setText("Connection failed: " + ex.getMessage());
            }
        });

        dbLayout.getChildren().addAll(
                new Label("Enter MySQL Connection Details:"),
                hostField, userField, passField,
                connectButton, statusLabel
        );

        Scene scene = new Scene(dbLayout, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }




}

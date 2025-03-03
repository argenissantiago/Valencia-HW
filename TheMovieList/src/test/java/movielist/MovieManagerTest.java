package movielist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MovieManager class.
 * Ensures proper functionality of CRUD operations and similarity score calculations.
 */
class MovieManagerTest {
    private MovieManager movieManager;
    private User testUser;
    private User anotherUser;
    private Map<String, User> userDatabase;
    private LoginManager loginManager;

    /**
     * Sets up the test environment before each test.
     * Initializes a movie manager, test users, and a mock database.
     */
    @BeforeEach
    void setUp() {
        userDatabase = new HashMap<>();
        testUser = new User("jeremy", "1111", "ihatekieran@succession.com", 45);
        anotherUser = new User("timmy", "2222", "godwhen@oscars.com", 27);
        userDatabase.put(testUser.getUserId(), testUser);
        userDatabase.put(anotherUser.getUserId(), anotherUser);
        movieManager = new MovieManager(userDatabase);
        loginManager = new LoginManager();
    }

    /** CREATE Tests */

    @Test
    void testAddMovie() {
        boolean result = movieManager.addMovie(testUser, "Dune");
        assertTrue(result, "Movie should be added successfully.");
        assertEquals(1, testUser.getFavoriteMovies().size(), "Movie list should contain 1 movie.");
    }

    @Test
    void testAddMovieLimit() {
        for (int i = 0; i < 10; i++) {
            movieManager.addMovie(testUser, "Movie " + i);
        }
        boolean result = movieManager.addMovie(testUser, "Extra Movie");
        assertFalse(result, "Adding an 11th movie should fail.");
        assertEquals(10, testUser.getFavoriteMovies().size(), "Movie list should contain exactly 10 movies.");
    }

    /** READ Tests */

    @Test
    void testViewMovieList() {
        movieManager.addMovie(testUser, "Dune");
        List<String> movies = testUser.getFavoriteMovies();
        assertEquals(1, movies.size(), "Movie list should have 1 movie.");
        assertEquals("Dune", movies.get(0), "Movie should match.");
    }

    @Test
    void testViewEmptyMovieList() {
        List<String> movies = testUser.getFavoriteMovies();
        assertTrue(movies.isEmpty(), "Movie list should be empty.");
    }

    /** UPDATE Tests */

    @Test
    void testUpdateMovie() {
        movieManager.addMovie(testUser, "Old Movie");
        boolean result = movieManager.updateMovie(testUser, 0, "New Movie");
        assertTrue(result, "Movie should be updated successfully.");
        assertEquals("New Movie", testUser.getFavoriteMovies().get(0), "Updated movie should match new name.");
    }

    @Test
    void testUpdateMovieInvalidIndex() {
        boolean result = movieManager.updateMovie(testUser, 5, "Won't Work");
        assertFalse(result, "Updating an invalid index should fail.");
    }

    /** DELETE Tests */

    @Test
    void testDeleteMovie() {
        movieManager.addMovie(testUser, "To Be Deleted");
        boolean result = movieManager.deleteMovie(testUser, 0);
        assertTrue(result, "Movie should be deleted successfully.");
        assertEquals(0, testUser.getFavoriteMovies().size(), "Movie list should be empty after deletion.");
    }

    @Test
    void testDeleteMovieInvalidIndex() {
        boolean result = movieManager.deleteMovie(testUser, 5);
        assertFalse(result, "Deleting an invalid index should fail.");
    }

    /** SIMILARITY SCORE Tests */

    @Test
    void testCalculateSimilarityScore() {
        movieManager.addMovie(testUser, "Dune");
        movieManager.addMovie(anotherUser, "Dune");

        Map<String, Double> similarity = movieManager.calculateSimilarityScore(testUser);
        assertTrue(similarity.containsKey("timmy"), "There should be a match with timmy.");
        assertEquals(100.0, similarity.get("timmy"), 0.01, "Similarity should be 100%.");
    }

    @Test
    void testCalculateSimilarityScoreNoMatch() {
        movieManager.addMovie(testUser, "Wicked");
        movieManager.addMovie(anotherUser, "Interstellar");

        Map<String, Double> similarity = movieManager.calculateSimilarityScore(testUser);
        assertTrue(similarity.isEmpty(), "There should be no matches.");
    }

    /** FILE HANDLING Tests */

    @Test
    void testFileExists() {
        Path path = Paths.get("src/test/resources/usersample.txt"); // Ensure this is the correct path
        assertTrue(Files.exists(path), "Test file should exist at " + path.toAbsolutePath());
    }

    @Test
    void testLoadUsersFromFile() throws IOException {
        Path testFile = Paths.get("src/test/resources/usersample.txt");
        assertTrue(Files.exists(testFile), "Test file should exist before loading.");

        loginManager.loadUsersFromFile(testFile.toString()); // Ensure correct method signature

        assertFalse(loginManager.getUserDatabase().isEmpty(), "User database should not be empty after loading.");
    }
}

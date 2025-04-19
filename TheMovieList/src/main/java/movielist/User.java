package movielist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a user in the Movie List application.
 * Each user has a unique ID, hashed PIN, email, age, and a list of favorite movies.
 * The user can also have a designated favorite movie and database ID.
 */

public class User {
    private String userId;
    private String hashedPin;
    private String email;
    private int age;
    private List<String> favoriteMovies;
    private String topFavoriteMovie;

    /**
     * Constructor for creating a new user.
     *
     * @param userId    The unique username.
     * @param hashedPin The hashed PIN for authentication.
     * @param email     The user's email.
     * @param age       The user's age.
     */
    public User(String userId, String hashedPin, String email, int age) {
        this.userId = userId;
        this.hashedPin = hashedPin;
        this.email = email;
        this.age = age;
        this.favoriteMovies = new ArrayList<>();
        this.topFavoriteMovie = null;
    }

    /**
     * Gets the user's unique ID.
     *
     * @return the user ID
     */

    public String getUserId() {
        return userId;
    }

    /**
     * Gets the user's hashed PIN.
     *
     * @return the hashed PIN
     */

    public String getHashedPin() {
        return hashedPin;
    }

    /**
     * Gets the user's email.
     *
     * @return the email address
     */

    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's age.
     *
     * @return the age of the user
     */

    public int getAge() {
        return age;
    }

    /**
     * Returns a copy of the user's favorite movies list.
     *
     * @return A copy of the favoriteMovies list.
     */
    public List<String> getFavoriteMovies() {
        return Collections.unmodifiableList(favoriteMovies);
    }

    /**
     * Gets the user's top favorite movie, if set.
     *
     * @return the top favorite movie, or {@code null} if not set
     */

    public String getTopFavoriteMovie() {
        return topFavoriteMovie;
    }

    /**
     * Sets a new hashed PIN for the user.
     *
     * @param newHashedPin the new hashed PIN
     */

    public void setHashedPin(String newHashedPin) {
        this.hashedPin = newHashedPin;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the new email address
     */

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the user's age.
     *
     * @param age the new age
     */

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Adds a movie to the user's favorite movie list after normalizing it.
     *
     * @param movie The movie to add.
     * @return true if the movie was added successfully, false if the list is full or duplicate.
     */
    public boolean addFavoriteMovie(String movie) {
        if (favoriteMovies.size() < 10) {
            String normalizedMovie = normalizeMovieTitle(movie);
            if (!favoriteMovies.contains(normalizedMovie)) {
                favoriteMovies.add(normalizedMovie);
                if (topFavoriteMovie == null) {
                    topFavoriteMovie = normalizedMovie; // Set first added movie as top favorite
                }
                return true;
            }
        }
        return false; // List is full or duplicate movie
    }

    /**
     * Updates a movie at the specified index after normalizing the new title.
     *
     * @param index    The index of the movie to update (0-based).
     * @param newMovie The new movie name.
     * @return true if the update was successful, false if the index is invalid.
     */
    public boolean updateMovie(int index, String newMovie) {
        if (index >= 0 && index < favoriteMovies.size()) {
            favoriteMovies.set(index, normalizeMovieTitle(newMovie));
            return true;
        }
        return false; // Invalid index
    }

    /**
     * Deletes a movie at the specified index.
     *
     * @param index The index of the movie to delete (0-based).
     * @return true if the movie was successfully deleted, false if the index is invalid.
     */
    public boolean deleteMovie(int index) {
        if (index >= 0 && index < favoriteMovies.size()) {
            favoriteMovies.remove(index);
            if (favoriteMovies.isEmpty()) {
                topFavoriteMovie = null; // Reset top favorite if no movies left
            } else if (index == 0) {
                topFavoriteMovie = favoriteMovies.get(0); // Update top favorite if first movie is removed
            }
            return true;
        }
        return false; // Invalid index
    }

    /**
     * Normalizes a movie title by trimming whitespace and converting to lowercase.
     *
     * @param movie The movie title to normalize.
     * @return The normalized movie title.
     */
    private String normalizeMovieTitle(String movie) {
        return movie.trim().toLowerCase();
    }

    /**
     * Returns a string representation of the user for display in lists or debugging.
     *
     * @return A formatted string representation of the user.
     */
    @Override
    public String toString() {
        return String.format("%s (Age: %d, Email: %s)", userId, age, email);
    }

    /**
     * Gets the database ID associated with the user.
     *
     * @return the user's database ID
     */

    private int databaseId;

    public int getDatabaseId() {
        return databaseId;
    }

    /**
     * Sets the database ID associated with the user.
     *
     * @param id the database ID
     */

    public void setDatabaseId(int id) {
        this.databaseId = id;
    }

}

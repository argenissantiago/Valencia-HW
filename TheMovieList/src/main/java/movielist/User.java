package movielist;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the movie list application.
 * Each user has a unique ID, hashed PIN, email, age, and a list of favorite movies.
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
     * @param userId The unique username.
     * @param hashedPin The hashed PIN for authentication.
     * @param email The user's email.
     * @param age The user's age.
     */
    public User(String userId, String hashedPin, String email, int age) {
        this.userId = userId;
        this.hashedPin = hashedPin;
        this.email = email;
        this.age = age;
        this.favoriteMovies = new ArrayList<>();
        this.topFavoriteMovie = null;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getHashedPin() {
        return hashedPin;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public List<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public String getTopFavoriteMovie() {
        return topFavoriteMovie;
    }

    // Setters
    public void setHashedPin(String newHashedPin) {
        this.hashedPin = newHashedPin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Adds a movie to the user's favorite movie list.
     * @param movie The movie to add.
     * @return true if the movie was added successfully, false if the list is already full.
     */
    public boolean addFavoriteMovie(String movie) {
        if (favoriteMovies.size() < 10) {
            favoriteMovies.add(movie);
            if (topFavoriteMovie == null) {
                topFavoriteMovie = movie; // Set first added movie as top favorite
            }
            return true;
        }
        return false; // List is full
    }

    /**
     * Updates a movie at the specified index.
     * @param index The index of the movie to update (0-based).
     * @param newMovie The new movie name.
     * @return true if the update was successful, false if the index is invalid.
     */
    public boolean updateMovie(int index, String newMovie) {
        if (index >= 0 && index < favoriteMovies.size()) {
            favoriteMovies.set(index, newMovie);
            return true;
        }
        return false; // Invalid index
    }

    /**
     * Deletes a movie at the specified index.
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
}

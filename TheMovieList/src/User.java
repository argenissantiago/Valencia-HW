import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String hashedPin;
    private String email;
    private int age;
    private List<String> favoriteMovies;
    private String topFavoriteMovie;

    public User(String userId, String hashedPin, String email, int age) {
        this.userId = userId;
        this.hashedPin = hashedPin;
        this.email = email;
        this.age = age;
        this.favoriteMovies = new ArrayList<>();
        this.topFavoriteMovie = null;
    }

    public String getUserId() {
        return userId;
    }

    public String getHashedPin() {
        return hashedPin;
    }

    public void setHashedPin(String newHashedPin) {
        this.hashedPin = newHashedPin;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void addFavoriteMovie(String movie) {
        if (favoriteMovies.size() < 10) {
            favoriteMovies.add(movie);
            if (topFavoriteMovie == null) {
                topFavoriteMovie = movie;
            }
        } else {
            System.out.println("Your movie list is already full.");
        }
    }

    public List<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void updateMovie(int index, String newMovie) {
        if (index >= 0 && index < favoriteMovies.size()) {
            favoriteMovies.set(index, newMovie);
        } else {
            System.out.println("Invalid index. Choose a number between 1 and " + favoriteMovies.size());
        }
    }

    public void deleteMovie(int index) {
        if (index >= 0 && index < favoriteMovies.size()) {
            favoriteMovies.remove(index);
        } else {
            System.out.println("Invalid movie index.");
        }
    }
}

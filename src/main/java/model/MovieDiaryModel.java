package model;

import java.time.LocalDate;
import java.util.UUID;

public class MovieDiaryModel {
    private UUID id;
    private String title;
    private String year;
    private String genre;
    private String imdbRating;
    private LocalDate watchDate;
    private int userRating;
    private String review;


    public MovieDiaryModel() {
        this.id = UUID.randomUUID();
    }

    public MovieDiaryModel(UUID id, String title, String year, String genre, String imdbRating, LocalDate watchDate, int userRating, String review) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.imdbRating = imdbRating;
        this.watchDate = watchDate;
        this.userRating = userRating;
        this.review = review;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public LocalDate getWatchDate() {
        return watchDate;
    }

    public void setWatchDate(LocalDate watchDate) {
        this.watchDate = watchDate;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}

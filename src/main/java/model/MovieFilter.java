package model;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MovieFilter {

    public static List<MovieDiaryModel> filterMovies(List<MovieDiaryModel> movies, String titleFilter, LocalDate dateFilter, String ratingFilter) {
        return movies.stream()
                .filter(movie -> titleFilter == null || titleFilter.isEmpty() || movie.getTitle().toLowerCase().contains(titleFilter.toLowerCase()))
                .filter(movie -> dateFilter == null || movie.getWatchDate().equals(dateFilter))
                .filter(movie -> ratingFilter == null || ratingFilter.isEmpty() || String.valueOf(movie.getUserRating()).equals(ratingFilter))
                .collect(Collectors.toList());
    }
}

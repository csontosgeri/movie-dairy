package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovieFilterTest {

    private List<MovieDiaryModel> movies;

    @BeforeEach
    void setUp() {
        movies = List.of(
                new MovieDiaryModel(UUID.randomUUID(), "Inception", "2010", "Sci-Fi", "8.8", LocalDate.of(2023, 6, 1), 5, "Great!"),
                new MovieDiaryModel(UUID.randomUUID(), "Interstellar", "2014", "Sci-Fi", "8.6", LocalDate.of(2023, 6, 2), 4, "Amazing visuals."),
                new MovieDiaryModel(UUID.randomUUID(), "The Matrix", "1999", "Action", "8.7", LocalDate.of(2023, 6, 1), 5, "Classic.")
        );
    }

    @Test
    void testFilterByTitle() {
        List<MovieDiaryModel> result = MovieFilter.filterMovies(movies, "inception", null, null);
        assertEquals(1, result.size());
        assertEquals("Inception", result.getFirst().getTitle());
    }

    @Test
    void testFilterByDate() {
        List<MovieDiaryModel> result = MovieFilter.filterMovies(movies, null, LocalDate.of(2023, 6, 1), null);
        assertEquals(2, result.size());
    }

    @Test
    void testFilterByRating() {
        List<MovieDiaryModel> result = MovieFilter.filterMovies(movies, null, null, "5");
        assertEquals(2, result.size());
    }

    @Test
    void testFilterByAll() {
        List<MovieDiaryModel> result = MovieFilter.filterMovies(movies, "matrix", LocalDate.of(2023, 6, 1), "5");
        assertEquals(1, result.size());
        assertEquals("The Matrix", result.getFirst().getTitle());
    }
}

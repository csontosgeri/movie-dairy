package model;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MovieDiaryManagerTest {

    private static final String TEST_FILE = "test_movies.json";

    @BeforeEach
    void setup() {
        MovieDiaryManager.setFilePath(TEST_FILE);
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    void testSaveAndLoadMovies() {
        MovieDiaryModel movie = new MovieDiaryModel(
                UUID.randomUUID(),
                "Inception",
                "2010",
                "Sci-Fi",
                "8.8",
                LocalDate.of(2023, 6, 1),
                5,
                "Great movie!"
        );

        List<MovieDiaryModel> moviesToSave = List.of(movie);
        MovieDiaryManager.saveMovies(moviesToSave);

        List<MovieDiaryModel> loadedMovies = MovieDiaryManager.loadMovies();

        assertEquals(1, loadedMovies.size());
        MovieDiaryModel loaded = loadedMovies.getFirst();

        assertEquals(movie.getTitle(), loaded.getTitle());
        assertEquals(movie.getYear(), loaded.getYear());
        assertEquals(movie.getGenre(), loaded.getGenre());
        assertEquals(movie.getImdbRating(), loaded.getImdbRating());
        assertEquals(movie.getWatchDate(), loaded.getWatchDate());
        assertEquals(movie.getUserRating(), loaded.getUserRating());
        assertEquals(movie.getReview(), loaded.getReview());
    }
}

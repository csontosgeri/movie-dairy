package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MovieDiaryManager {


    private static String filePath = "movies.json";

    public static void setFilePath(String path) {
        filePath = path;
    }

    public static void saveMovies(List<MovieDiaryModel> movies) {
        JSONArray jsonArray = new JSONArray();

        for (MovieDiaryModel movie : movies) {
            JSONObject obj = new JSONObject();

            obj.put("id", movie.getId().toString());
            obj.put("title", movie.getTitle());
            obj.put("year", movie.getYear());
            obj.put("genre", movie.getGenre());
            obj.put("imdbRating", movie.getImdbRating());
            obj.put("watchDate", movie.getWatchDate().toString());
            obj.put("userRating", movie.getUserRating());
            obj.put("review", movie.getReview());
            jsonArray.put(obj);
        }

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static List<MovieDiaryModel> loadMovies() {
        List<MovieDiaryModel> movies = new ArrayList<>();

        if (!Files.exists(Paths.get(filePath))) {
            return movies;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                UUID id = obj.has("id") ? UUID.fromString(obj.getString("id")) : UUID.randomUUID();
                MovieDiaryModel movie = new MovieDiaryModel(
                        id,
                        obj.getString("title"),
                        obj.getString("year"),
                        obj.getString("genre"),
                        obj.getString("imdbRating"),
                        LocalDate.parse(obj.getString("watchDate")),
                        obj.getInt("userRating"),
                        obj.getString("review")
                );
                movies.add(movie);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return movies;
    }

}

package controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import model.MovieDiaryManager;
import model.MovieDiaryModel;
import model.MovieFilter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MovieDiaryController {

    @FXML
    private ComboBox<String> searchComboBox;
    @FXML
    private TextField titleText, yearText, genreText, imdbratingText;
    @FXML
    private Slider userRatingSlider;
    @FXML
    private DatePicker watchDatePicker;
    @FXML
    private TextField reviewText;
    @FXML
    private TableView<MovieDiaryModel> movieTable;
    @FXML
    private TableColumn<MovieDiaryModel, String> titleColumn;
    @FXML
    private TableColumn<MovieDiaryModel, LocalDate> dateColumn;
    @FXML
    private TableColumn<MovieDiaryModel, Integer> ratingColumn;
    @FXML
    private TextField filterTitleText;
    @FXML
    private DatePicker filterDatePicker;
    @FXML
    private ComboBox<String> filterRatingCB;


    private final String API_KEY = "5854492";
    private final Map<String, String> titleToImdbId = new HashMap<>();
    private final PauseTransition searchPause = new PauseTransition(Duration.millis(500));
    private MovieDiaryModel selectedMovie = null;

    // Initializes the controller, sets up UI components and event handlers
    @FXML
    public void initialize() {
        searchComboBox.setEditable(true);
        searchComboBox.getEditor().setOnKeyTyped(this::onSearchTyped);
        searchComboBox.setOnAction(event -> {
            String selectedTitle = searchComboBox.getValue();
            if (titleToImdbId.containsKey(selectedTitle)) {
                fetchMovieDetails(titleToImdbId.get(selectedTitle));
            }
        });

        // Set the movie list columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("watchDate"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("userRating"));

        // Filter by title, date and user rating
        filterTitleText.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterRatingCB.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterRatingCB.setPromptText("Filter by Rating");
        filterRatingCB.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));

        // Default descending order by watch date
        refreshTable();
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        movieTable.getSortOrder().add(dateColumn);

        // Double click on the title in movie list trigger editing
        movieTable.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2 && movieTable.getSelectionModel().getSelectedItem() != null) {
                edit();
            }
        });
    }
    // Handles search input typing, triggers delayed search after 3 characters
    @FXML
    public void onSearchTyped(KeyEvent event) {
        String query = searchComboBox.getEditor().getText();
        if (query.length() < 3) return;

        searchPause.setOnFinished(actionEvent -> searchMovies(query));
        searchPause.playFromStart();
    }

    // Sends a request to the OMDb API to search for movies by title
    private void searchMovies(String query){
        new Thread(() -> {
            try {
                String urlStr = "https://www.omdbapi.com/?s=" + query.replace(" ", "+") + "&apikey=" + API_KEY;
                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JSONObject response = new JSONObject(json.toString());
                if (response.optString("Response").equals("False")) {
                    String error = response.optString("Error", "Unknown Error.");
                    Platform.runLater(() -> System.out.println("API Error: " + error));
                    return;
                }

                if (response.has("Search")) {
                    processSearchResults(response.getJSONArray("Search"));
                }

            } catch (Exception exception) {
                exception.printStackTrace();
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error during search", "Failed to retrieve data. Please check your internet connection or try again later."));
            }
        }).start();
    }

    // Processes the JSON results from the OMDb API and populates the search combo box dropdown menu
    private void processSearchResults(JSONArray results){
        ObservableList<String> titles = FXCollections.observableArrayList();
        titleToImdbId.clear();

        for (int i = 0; i < results.length(); i++) {
            JSONObject movie = results.getJSONObject(i);
            String title = movie.getString("Title") + " (" + movie.getString("Year") + ")";
            titles.add(title);
            titleToImdbId.put(title, movie.getString("imdbID"));
        }

        Platform.runLater(() -> {
            searchComboBox.setItems(titles);
            searchComboBox.show();
        });
    }

    // Fetches detailed information about a selected movie from the OMDb API
    private void fetchMovieDetails(String imdbID) {

        new Thread(() -> {
            try {
                String urlStr = "https://www.omdbapi.com/?i=" + imdbID + "&apikey=" + API_KEY;
                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JSONObject movie = new JSONObject(json.toString());

                Platform.runLater(() -> {
                    titleText.setText(movie.optString("Title", ""));
                    yearText.setText(movie.optString("Year", ""));
                    genreText.setText(movie.optString("Genre", ""));
                    imdbratingText.setText(movie.optString("imdbRating", ""));
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Saves a new or edited movie entry to the diary
    @FXML
    public void save() {
        String title = titleText.getText();
        String year = yearText.getText();
        String genre = genreText.getText();
        String imdbRating = imdbratingText.getText();
        String review = reviewText.getText();
        int userRating = (int) userRatingSlider.getValue();
        LocalDate watchDate = watchDatePicker.getValue();

        if (title.isEmpty() || watchDate == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill in at least the title and watch date.");
            return;
        }
        UUID id = (selectedMovie != null) ? selectedMovie.getId() : UUID.randomUUID();
        MovieDiaryModel movie = new MovieDiaryModel(
                id,title, year, genre, imdbRating, watchDate, userRating, review
        );
        List<MovieDiaryModel> movies = MovieDiaryManager.loadMovies();
        movies.removeIf(m -> m.getId().equals(id));
        movies.add(movie);
        MovieDiaryManager.saveMovies(movies);
        showAlert(Alert.AlertType.INFORMATION, "Saved", "Movie saved successfully!");
        clearFields();
        refreshTable();
    }

    // Loads the selected movie's data into the form for editing
    @FXML
    public void edit() {
        selectedMovie = movieTable.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a movie to edit.");
            return;
        }
        titleText.setText(selectedMovie.getTitle());
        yearText.setText(selectedMovie.getYear());
        genreText.setText(selectedMovie.getGenre());
        imdbratingText.setText(selectedMovie.getImdbRating());
        reviewText.setText(selectedMovie.getReview());
        userRatingSlider.setValue(selectedMovie.getUserRating());
        watchDatePicker.setValue(selectedMovie.getWatchDate());
    }

    // Deletes the selected movie entry after confirmation
    @FXML
    public void delete() {
        MovieDiaryModel movieToDelete = movieTable.getSelectionModel().getSelectedItem();
        if (movieToDelete == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a movie to delete.");
            return;
        }

        Alert confirmation=new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this movie?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                List<MovieDiaryModel> movies = MovieDiaryManager.loadMovies();
                movies.removeIf(m -> m.getId().equals(movieToDelete.getId()));
                MovieDiaryManager.saveMovies(movies);
                refreshTable();
            }
        });
    }

    // Clears the form fields and resets the editing state
    @FXML
    public void cancel(){
        clearFields();
    }

    private void clearFields() {
        titleText.clear();
        yearText.clear();
        genreText.clear();
        imdbratingText.clear();
        reviewText.clear();
        userRatingSlider.setValue(3);
        watchDatePicker.setValue(null);
        searchComboBox.getEditor().clear();
    }

    // Refreshes the movie table with the latest data
    private void refreshTable() {
        List<MovieDiaryModel> movies = MovieDiaryManager.loadMovies();
        movieTable.setItems(FXCollections.observableArrayList(movies));
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        movieTable.getSortOrder().add(dateColumn);
    }

    // Displays an alert dialog with the specified type and message
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    // Applies filters to the movie list by title, date, and rating
    private void applyFilters() {
        String titleFilter = filterTitleText.getText().toLowerCase();
        LocalDate dateFilter = filterDatePicker.getValue();
        String ratingFilter = filterRatingCB.getValue();

        List<MovieDiaryModel> allMovies = MovieDiaryManager.loadMovies();
        List<MovieDiaryModel> filtered = MovieFilter.filterMovies(allMovies, titleFilter, dateFilter, ratingFilter);

        movieTable.setItems(FXCollections.observableArrayList(filtered));
    }

    // Resets all filter fields to their default state
    @FXML
    public void resetFilters() {
        filterTitleText.clear();
        filterDatePicker.setValue(null);
        filterRatingCB.setValue(null);
        refreshTable();
    }

    // Exports the movie list to a CSV file
    @FXML
    public void exportToCSV() {
        List<MovieDiaryModel> movies = MovieDiaryManager.loadMovies();
        if (movies.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Data", "There are no movies to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("movie_diary_export.csv");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Title,Year,Genre,IMDb Rating,Watch Date,User Rating,Review");
                for (MovieDiaryModel movie : movies) {
                    writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,\"%s\"%n",
                            movie.getTitle(),
                            movie.getYear(),
                            movie.getGenre(),
                            movie.getImdbRating(),
                            movie.getWatchDate(),
                            movie.getUserRating(),
                            movie.getReview().replace("\"", "\"\""));
                }
                showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Movies exported to CSV successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Export Failed", "An error occurred while exporting the file.");
            }
        }
    }

}

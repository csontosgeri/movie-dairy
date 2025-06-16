import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MovieDiaryApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/moviediary.fxml"));
        stage.setTitle("Movie Diary");
        stage.setScene(new Scene(root));
        stage.show();
    }
}


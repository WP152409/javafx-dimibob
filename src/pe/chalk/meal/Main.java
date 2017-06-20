package pe.chalk.meal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(final Stage stage) throws Exception {
        final Parent root = FXMLLoader.load(getClass().getResource("meal.fxml"));

        stage.setTitle("디미밥");
        stage.setScene(new Scene(root, 400, 700));
        stage.show();

        root.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

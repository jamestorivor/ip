package james.gui;

import java.io.IOException;

import james.James;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import james.command.CommandProcessor;

/** Provides the JavaFX entry point for James. */
public class Main extends Application {

    private final String JAMES_DATA_FILE_PATH = "data/james.txt";

    private James james = new James(JAMES_DATA_FILE_PATH);

    /** Loads and displays the main window. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        loader.<MainWindow>getController().setJames(james);
        stage.setScene(new Scene(root));
        stage.setTitle("James");
        stage.setMinWidth(417);
        stage.setMinHeight(220);
        stage.show();
    }
}

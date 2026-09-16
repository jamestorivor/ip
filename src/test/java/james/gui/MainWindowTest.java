package james.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Verifies automatic and manual scrolling in the conversation window.
 */
public class MainWindowTest {
    @BeforeAll
    public static void startJavaFx() throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            ready.countDown();
        });
        assertTrue(ready.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void initialize_overflowingConversation_scrollsWithoutScrollbarClick() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            new Scene(root, 500, 600);
            ScrollPane scrollPane = (ScrollPane) loader.getNamespace().get("scrollPane");
            VBox dialogs = (VBox) loader.getNamespace().get("dialogContainer");
            Region message = new Region();
            message.setMinHeight(2000);
            dialogs.getChildren().add(message);
            root.applyCss();
            root.layout();

            assertFalse(scrollPane.vvalueProperty().isBound());
            assertEquals(scrollPane.getVmax(), scrollPane.getVvalue());
            dialogs.fireEvent(new ScrollEvent(ScrollEvent.SCROLL, 10, 10, 10, 10,
                    false, false, false, false, false, false, 0, 120, 0, 120,
                    ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
                    ScrollEvent.VerticalTextScrollUnits.NONE, 0, 0, null));
            assertTrue(scrollPane.getVvalue() < scrollPane.getVmax());

            message.setMinHeight(3000);
            root.layout();
            assertEquals(scrollPane.getVmax(), scrollPane.getVvalue());
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }
}

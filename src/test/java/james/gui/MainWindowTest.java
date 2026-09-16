package james.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import james.command.CommandResponse;
import james.command.Sticker;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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
    @Test
    public void createJames_eachDisplayMode_buildsExpectedContent() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            CommandResponse[] responses = {
                new CommandResponse("text", CommandResponse.Type.NORMAL, false),
                CommandResponse.createStickerOnly("fallback", Sticker.YATTA),
                CommandResponse.createWithSticker("error", CommandResponse.Type.ERROR, Sticker.GOMEN)
            };
            for (CommandResponse response : responses) {
                DialogBox box = DialogBox.createJames(response, null);
                assertInstanceOf(ImageView.class, box.getChildren().get(0));
                if (response.getDisplayMode() == CommandResponse.DisplayMode.TEXT_ONLY) {
                    TextFlow text = assertInstanceOf(TextFlow.class, box.getChildren().get(1));
                    assertEquals("text", assertInstanceOf(Text.class, text.getChildren().get(0)).getText());
                } else {
                    VBox content = assertInstanceOf(VBox.class, box.getChildren().get(1));
                    ImageView sticker = assertInstanceOf(ImageView.class, content.getChildren().get(0));
                    assertFalse(sticker.getImage().isError());
                    assertTrue(sticker.isPreserveRatio());
                    assertEquals(response.getSticker().name() + " sticker", sticker.getAccessibleText());
                    if (response.getDisplayMode() == CommandResponse.DisplayMode.STICKER_ONLY) {
                        assertEquals(1, content.getChildren().size());
                        assertEquals(160, sticker.getFitWidth());
                    } else {
                        assertEquals(2, content.getChildren().size());
                        assertEquals(120, sticker.getFitWidth());
                        TextFlow text = assertInstanceOf(TextFlow.class, content.getChildren().get(1));
                        assertEquals("error", assertInstanceOf(Text.class, text.getChildren().get(0)).getText());
                        assertTrue(text.getStyleClass().contains("error"));
                    }
                }
            }
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }
}

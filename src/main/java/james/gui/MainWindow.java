package james.gui;

import james.James;
import james.command.CommandResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Controls the main JavaFX conversation window.
 */
public class MainWindow extends AnchorPane {
    private static final String GREETING_MESSAGE =
            "JAMES THE CHATTY CHATBOT\nHello! I'm James.\nI can do anything for you!";

    private static final String JAMES_IMAGE_FILE_PATH = "/images/james.png";
    private static final String USER_IMAGE_FILE_PATH = "/images/user.png";

    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;

    private James james;
    private final Image jamesImage = new Image(getClass().getResourceAsStream(JAMES_IMAGE_FILE_PATH));
    private final Image userImage = new Image(getClass().getResourceAsStream(USER_IMAGE_FILE_PATH));

    /**
     * Initializes automatic scrolling for new messages.
     */
    @FXML
    public void initialize() {
        // Keep the scroll position in its valid range and free for manual scrolling.
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(scrollPane.getVmax()));
        dialogContainer.setFillWidth(true);
        dialogContainer.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double dialogWidth = Math.max(0, newWidth.doubleValue() - 10);
            dialogContainer.getChildren().forEach(child -> {
                if (child instanceof Region region) {
                    region.setPrefWidth(dialogWidth);
                }
            });
        });
    }

    /**
     * Injects the James instance.
     */
    public void setJames(James james) {
        this.james = james;
        dialogContainer.getChildren().add(DialogBox.createJames(new CommandResponse(
                GREETING_MESSAGE,
                CommandResponse.Type.NORMAL, false), jamesImage));
    }

    /**
     * Processes the current input and appends both sides of the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim().stripTrailing();
        if (input.isEmpty()) {
            return;
        }
        CommandResponse response = james.getResponse(input);
        DialogBox userDialog = DialogBox.createUser(input, userImage);
        DialogBox jamesDialog = DialogBox.createJames(response, jamesImage);
        setDialogWidth(userDialog);
        setDialogWidth(jamesDialog);
        dialogContainer.getChildren().addAll(userDialog, jamesDialog);
        userInput.clear();
        if (response.isExit()) {
            Window window = userInput.getScene().getWindow();
            Platform.runLater(window::hide);
        }
    }

    /**
     * Fits a dialog to the conversation width while reserving its outer margin.
     *
     * @param dialog Dialog whose preferred width is updated.
     */
    private void setDialogWidth(Region dialog) {
        dialog.setPrefWidth(Math.max(0, dialogContainer.getWidth() - 10));
    }
}

package james.gui;

import java.util.Objects;

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

/** Controls the main JavaFX conversation window. */
public class MainWindow extends AnchorPane {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;

    private final String JAMES_IMAGE_FILE_PATH = "/images/james.png";
    private final String USER_IMAGE_FILE_PATH = "/images/user.png";

    private James james;
    private final Image JAMES_IMAGE = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(JAMES_IMAGE_FILE_PATH)));
    private final Image USER_IMAGE = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(USER_IMAGE_FILE_PATH)));

    /** Initializes automatic scrolling for new messages. */
    @FXML
    public void initialize() {
        int NARROW_DIALOG_WIDTH_BY = 10;
        int MIN_DIALOG_WIDTH = 0;

        assert scrollPane != null : "FXML must inject scrollPane";
        assert dialogContainer != null : "FXML must inject dialogContainer";
        assert userInput != null : "FXML must inject userInput";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.setFillWidth(true);
        dialogContainer.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double dialogWidth = Math.max(MIN_DIALOG_WIDTH, newWidth.doubleValue() - NARROW_DIALOG_WIDTH_BY);
            dialogContainer.getChildren().forEach(child -> {
                if (child instanceof Region region) {
                    region.setPrefWidth(dialogWidth);
                }
            });
        });
    }

    /** Injects the James instance */
    public void setJames(James james) {
        String JAMES_WELCOME_MESSAGE = "JAMES THE CHATTY CHATBOT\nHello! I'm James.\nI can do anything for you!";
        this.james = james;

        dialogContainer.getChildren().add(DialogBox.james(new CommandResponse(
                JAMES_WELCOME_MESSAGE,
                CommandResponse.Type.NORMAL, false), JAMES_IMAGE));
    }

    /** Processes the current input and appends both sides of the conversation. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim().stripTrailing();
        if (input.isEmpty()) {
            return;
        }
        CommandResponse response = james.getResponse(input);
        DialogBox userDialog = DialogBox.user(input, USER_IMAGE);
        DialogBox jamesDialog = DialogBox.james(response, JAMES_IMAGE);
        setDialogWidth(userDialog);
        setDialogWidth(jamesDialog);
        dialogContainer.getChildren().addAll(userDialog, jamesDialog);
        userInput.clear();
        if (response.isExit()) {
            Window window = userInput.getScene().getWindow();
            Platform.runLater(window::hide);
        }
    }

    private void setDialogWidth(Region dialog) {
        dialog.setPrefWidth(Math.max(0, dialogContainer.getWidth() - 10));
    }
}

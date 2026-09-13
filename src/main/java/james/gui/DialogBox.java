package james.gui;

import java.io.IOException;
import java.util.Collections;

import james.command.CommandResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


/** Displays one message in the conversation. */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_RESOURCE = "/view/DialogBox.fxml";
    private static final String DIALOG_LOAD_ERROR_MESSAGE = "Unable to load dialog box";
    private static final String REPLY_STYLE_CLASS = "reply-label";

    @FXML
    private TextFlow dialog;
    @FXML
    private ImageView userImage;

    private DialogBox(String text, boolean isUser, CommandResponse.Type type, Image img) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource(DIALOG_BOX_RESOURCE));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException(DIALOG_LOAD_ERROR_MESSAGE, e);
        }
        dialog.getChildren().setAll(new Text(text));
        userImage.setImage(img);
        setFillHeight(false);
        setMinWidth(0);
        setPrefWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        dialog.setMinWidth(0);
        dialog.setMaxWidth(Double.MAX_VALUE);
        dialog.setMinHeight(0);

        HBox.setHgrow(dialog, Priority.ALWAYS);
        if (!isUser) {
            dialog.getStyleClass().add(type.name().toLowerCase());
        }
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add(REPLY_STYLE_CLASS);
    }

    /** Creates a user message box. */
    public static DialogBox user(String text, Image image) {
        return new DialogBox(text, true, CommandResponse.Type.NORMAL, image);
    }

    /** Creates a James response box. */
    public static DialogBox james(CommandResponse response, Image image) {
        DialogBox db = new DialogBox(response.getMessage(), false, response.getType(), image);
        db.flip();
        return db;
    }
}

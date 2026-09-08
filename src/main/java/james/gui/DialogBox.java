package james.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import james.command.CommandResponse;

/** Displays one message in the conversation. */
public class DialogBox extends HBox {
    @FXML
    private TextFlow dialog;
    @FXML
    private ImageView userImage;

    private DialogBox(String text, boolean isUser, CommandResponse.Type type, Image img) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load dialog box", e);
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
        dialog.getStyleClass().add("reply-label");
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

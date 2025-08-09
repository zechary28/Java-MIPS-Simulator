package mips.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import mips.util.Word;

import java.util.List;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button cycleButton;
    @FXML
    private Button pipelineButton;
    @FXML
    private ListView<Word> memoryListView;
    @FXML
    private ListView<Word> registerListView;

    private Fluke fluke;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/wooper.png"));
    private final Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/manaphy.png"));

    @FXML
    public void initialize() {
        this.fluke = new Fluke();
        List<Word> mem = this.fluke.getMemory();
        memoryListView.setCellFactory(listView -> new DataListCell());
        memoryListView.getItems().setAll(mem);
        List<Word> regs = this.fluke.getRegisters();
        registerListView.setCellFactory(listView -> new DataListCell());
        registerListView.getItems().setAll(regs);    }

    /** Injects the Fluke instance */
    public void setFluke(Fluke d) {
        fluke = d;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = fluke.getResponse(input);
        updateTables();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, dukeImage)
        );
        Platform.runLater(() -> {
            scrollPane.setVvalue(1.0);
        });
        userInput.clear();
    }

    @FXML
    private void handlePipelineInput() {
        String response = fluke.getResponse("pipeline");
        updateTables();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog("pipeline", userImage),
                DialogBox.getDukeDialog(response, dukeImage)
        );
        userInput.clear();
        Platform.runLater(() -> {
            scrollPane.setVvalue(1.0);
        });
    }

    @FXML
    private void handleCycleInput() {
        String response = fluke.getResponse("cycle");
        updateTables();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog("cycle", userImage),
                DialogBox.getDukeDialog(response, dukeImage)
        );
        Platform.runLater(() -> {
            scrollPane.setVvalue(1.0);
        });
        userInput.clear();
    }

    private void updateTables() {
        List<Word> mem = this.fluke.getMemory();
        List<Word> regs = this.fluke.getRegisters();
        memoryListView.getItems().setAll(mem);
        registerListView.getItems().setAll(regs);
    }

    /**
     * Handles the startup sequence by adding a startup dialog to the dialog container.
     *
     * This method retrieves the startup message from the `luke` object and displays it
     * in the user interface as a dialog box, along with the associated Duke image.
     */
    public void handleStartUp() {
        dialogContainer.getChildren().addAll(
                DialogBox.getDukeDialog(fluke.getStartUp(), dukeImage)
        );
    }

    /**
     * Handles the shutdown sequence by adding a shutdown dialog to the dialog container.
     *
     * This method retrieves the shutdown message from the `luke` object and displays it
     * in the user interface as a dialog box, along with the associated Duke image.
     */
    public void handleShutDown() {
        dialogContainer.getChildren().addAll(
                DialogBox.getDukeDialog(fluke.getShutDown(), dukeImage)
        );
    }
}

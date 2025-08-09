package mips.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import mips.util.Word;

import java.io.IOException;

public class DataListCell extends ListCell<Word> {

    @FXML private Label binaryLabel;
    @FXML private Label decimalLabel;
    private HBox root;

    public DataListCell() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DataListCell.fxml"));
            loader.setController(this);
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Word item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            binaryLabel.setText(item.toString());
            decimalLabel.setText(item.toDec() + "");
            setGraphic(root);
        }
    }
}

package ru.testing.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoryController {

    private MainController main;

    @FXML
    private TableView<SendMessage> historyTable;

    @FXML
    private TableColumn<SendMessage, String> messageColumnItem;

    @FXML
    public void initialize() {

        // single column width setting
        messageColumnItem.prefWidthProperty().bind(historyTable.widthProperty().multiply(0.99));

        // add data
        messageColumnItem.setCellValueFactory(new PropertyValueFactory<>("message"));
        historyTable.setItems(main.sendMessageList);

        // set message text from history
        messageColumnItem.setCellFactory(col -> {
            final TableCell<SendMessage, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() > 1) {
                    String cellText = cell.getText();
                    if (!cellText.isEmpty()) {
                        main.messageText.setText(cell.getText());
                    }
                }
            });
            return cell;
        });
    }

    public void init(MainController mainController) {
        this.main = mainController;
    }
}

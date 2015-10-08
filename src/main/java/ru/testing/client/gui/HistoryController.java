package ru.testing.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoryController {

    private MainController main;

    @FXML
    private Button cleanHistoryBtn;

    @FXML
    private Button closeHistoryWindow;

    @FXML
    private TableView<SendMessage> historyTable;

    @FXML
    private TableColumn<SendMessage, String> messageColumnItem;

    /**
     * Method run then this controller initialize
     */
    @FXML
    public void initialize() {

        // Single column width setting
        messageColumnItem.prefWidthProperty().bind(historyTable.widthProperty().multiply(0.99));
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add data
        messageColumnItem.setCellValueFactory(new PropertyValueFactory<>("message"));
        historyTable.setItems(main.sendMessageList);

        // Set message text from history
        messageColumnItem.setCellFactory(col -> {
            final TableCell<SendMessage, String> cell = new TableCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() > 1) {
                    String cellText = cell.getText();
                    if (cellText != null && !cellText.isEmpty() && !main.messageText.isDisable()) {
                        main.messageText.setText(cell.getText());
                        main.history.close();
                        main.history = null;
                    }
                }
            });
            return cell;
        });

        // Close history window action
        closeHistoryWindow.setOnAction((event -> main.history.close()));

        // Clean history
        cleanHistoryBtn.setOnAction((event) -> {
            main.messageSendHistoryBtn.setDisable(true);
            main.history.close();
            main.sendMessageList.clear();
        });
    }

    /**
     * Initialize instance main controller in this controller
     * @param mainController MainController
     */
    protected void init(MainController mainController) {
        this.main = mainController;
    }
}

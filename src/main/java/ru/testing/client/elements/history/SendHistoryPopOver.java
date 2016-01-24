package ru.testing.client.elements.history;

import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.controlsfx.control.PopOver;
import ru.testing.client.controllers.MainController;

/**
 * Send message history pop over
 */
public class SendHistoryPopOver extends PopOver {

    private MainController mainController;

    public SendHistoryPopOver(MainController mainController) {
        this.mainController = mainController;
        setDetachable(false);
        setArrowLocation(ArrowLocation.TOP_RIGHT);
        setHeaderAlwaysVisible(true);
        setTitle("Send message history");
        setOnHidden(event -> mainController.getSendMsgHistoryBtn().setSelected(false));
        setContentNode(getList());
    }

    /**
     * Get history list view
     *
     * @return ListView<String>
     */
    private ListView<String> getList() {
        ListView<String> list = new ListView<>();
        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        list.setMaxHeight(150);
        list.setMaxWidth(300);
        list.setStyle("-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;" +
                "-fx-border-radius: 2px; " +
                "-fx-padding: 7px 1px");
        list.setItems(mainController.getSendMsgList());
        list.setCellFactory(listView -> new SendHistoryCellFactory(mainController, this));
        return list;
    }
}

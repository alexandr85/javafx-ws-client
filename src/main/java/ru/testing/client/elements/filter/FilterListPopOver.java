package ru.testing.client.elements.filter;

import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.controlsfx.control.PopOver;
import ru.testing.client.controllers.TabWsMessagesController;

/**
 * Filters list pop over
 */
public class FilterListPopOver extends PopOver {

    private TabWsMessagesController messagesController;

    public FilterListPopOver(TabWsMessagesController messagesController) {
        this.messagesController = messagesController;
        setDetachable(false);
        setArrowLocation(PopOver.ArrowLocation.BOTTOM_RIGHT);
        setOnHidden((event -> messagesController.getFilterListBtn().setSelected(false)));
        setContentNode(getList());
    }

    /**
     * Get filter list views
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
        list.setItems(messagesController.getFilterList());
        list.setCellFactory(listView -> new FilterCellFactory(messagesController.getFilterList()));
        return list;
    }
}

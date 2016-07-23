package ru.testing.client.elements.message;

import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import ru.testing.client.controllers.MainController;

/**
 * Tab with detail message
 */
public class DetailTab extends Tab {

    public DetailTab(OutputMessage item, MainController main) {

        // Setup text area
        TextArea area = new TextArea(item.getMessage());
        area.setEditable(false);
        area.setWrapText(true);

        // Setup tab
        this.setText(String.format("Message #%s", main.tabPane.getTabs().size()));
        this.setContent(area);

        // Setup new tab with content in tabPane
        SingleSelectionModel<Tab> selectionModel = main.tabPane.getSelectionModel();
        main.tabPane.getTabs().add(this);
        selectionModel.select(this);
    }
}

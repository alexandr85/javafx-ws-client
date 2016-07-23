package ru.testing.client.elements.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.MainController;

/**
 * Tab with detail message
 */
public class DetailTab extends Tab {

    private static final Logger LOGGER = LoggerFactory.getLogger(DetailTab.class.getName());
    private MainController main;

    public DetailTab(final OutputMessage item, MainController mainController) {
        main = mainController;
        ObservableList<Tab> tabsList = main.getTabPane().getTabs();
        if (item != null) {
            String message = item.getMessage();

            // Try pretty json string
            if (main.getJsonPretty().isSelected()) {
                message = tryJsonPretty(message);
            }

            // Setup text area
            TextArea area = new TextArea(message);
            area.setEditable(false);
            area.setWrapText(true);

            // Setup tab
            this.setText(String.format("Message #%s", tabsList.size()));
            this.setContent(area);

            // Setup new tab with content in tabPane
            SingleSelectionModel<Tab> selectionModel = main.getTabPane().getSelectionModel();
            tabsList.add(this);
            selectionModel.select(this);
        }
    }

    /**
     * Try pretty json string from cell message
     *
     * @param message String
     * @return String
     */
    private String tryJsonPretty(String message) {
        try {
            String json = message.replaceAll(main.getProperties().getJsonPrettyReplaceRegex(), "");
            ObjectMapper mapper = new ObjectMapper();
            Object object = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            LOGGER.error("Error pretty json from string: {}", e.getMessage());
            return message;
        }
    }
}

package ru.testing.client.elements.settings;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.controllers.MainController;
import ru.testing.client.controllers.TabSettingsController;

import java.io.IOException;

/**
 * Tab with application settings form
 */
public class SettingsTab extends Tab {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsTab.class.getName());
    private static Tab settingsTab;
    private MainController main;
    private TabPane tabPane;

    public SettingsTab(MainController mainController) {
        main = mainController;
        tabPane = main.getTabPane();
        if (settingsTab == null) {
            settingsTab = getSettingsTab();
        }
    }

    private Tab getSettingsTab() {

        // Create new tab
        Tab settingsTab = new Tab("Settings");
        settingsTab.setTooltip(new Tooltip("Application settings"));
        settingsTab.setGraphic( new ImageView("/images/settings.png"));

        // Load settings view form
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tab.settings.fxml"));
            loader.setController(new TabSettingsController(main));
            Parent root = loader.load();
            settingsTab.setContent(root);
        } catch (IOException e) {
            LOGGER.error("Error load sessions pop over: {}", e.getMessage());
        }
        return settingsTab;
    }

    public void showTab() {
        ObservableList<Tab> tabsList =tabPane.getTabs();
        SingleSelectionModel<Tab> selectTabModel = tabPane.getSelectionModel();
        if (!tabsList.contains(settingsTab)) {
            tabsList.add(settingsTab);
        }
        selectTabModel.select(settingsTab);
    }
}

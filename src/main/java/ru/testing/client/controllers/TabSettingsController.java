package ru.testing.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.testing.client.common.db.DataBase;
import ru.testing.client.common.db.objects.Settings;
import ru.testing.client.common.properties.DefaultProperties;
import ru.testing.client.elements.Dialogs;
import ru.testing.client.elements.tabs.WsMessageTab;

import static ru.testing.client.MainApp.getMainController;

/**
 * Controller for settings tab form
 */
public class TabSettingsController {

    private static final String FONT_SIZE_FORMAT = "-fx-font-size: %spx;";
    private DataBase dataBase = DataBase.getInstance();
    private MainController main;

    @FXML
    private CheckBox chWrap;
    @FXML
    private CheckBox chPretty;
    @FXML
    private Label fontLabel;
    @FXML
    private Slider fontSlider;
    @FXML
    private CheckBox cbAutoScroll;

    @FXML
    private void initialize() {

        // Init main controller
        main = getMainController();

        // Slider value listener
        fontSlider.valueProperty().addListener(observable ->
                fontLabel.setText(
                        String.format("%spx", ((Number) fontSlider.getValue()).intValue())
                )
        );

        // Set value from database
        setSettingsValues(dataBase.getSettings());
    }

    /**
     * Load default settings action
     */
    @FXML
    private void loadDefaultSettings() {
        setSettingsValues(DefaultProperties.getInstance().getMessageSettings());
    }

    /**
     * Save current settings state action
     */
    @FXML
    private void saveSettings() {

        // Enable loader
        main.setProgressVisible(true);

        // Save new settings in database
        boolean status = dataBase.setSettings(new Settings(
                ((Number) fontSlider.getValue()).intValue(),
                chWrap.isSelected(),
                chPretty.isSelected(),
                cbAutoScroll.isSelected()
        ));
        if (status) {

            Settings settings = dataBase.getSettings();

            // Set font size for all messages
            for (Tab tab : main.getTabPane().getTabs()) {
                if (tab instanceof WsMessageTab) {
                    Node tabNode = tab.getContent();
                    if (tabNode instanceof GridPane) {
                        for (Node node : ((GridPane) tabNode).getChildren()) {
                            if (node instanceof TextArea || node instanceof ListView) {
                                node.setStyle(String.format(FONT_SIZE_FORMAT, settings.getFontSize()));
                                node.applyCss();
                            }
                        }
                    }
                }
            }

            // Show successful dialog
            new Dialogs().getInfoDialog("Settings save successful", main, false);
        } else {
            new Dialogs().getWarningDialog("Error save settings. See log.", main, false);
        }

        // Disable loader
        main.setProgressVisible(false);
    }

    /**
     * Set settings value from database
     */
    private void setSettingsValues(Settings settings) {

        // Enable loader
        main.setProgressVisible(true);

        // Set text wrap value
        chWrap.setSelected(settings.isTextWrap());

        // Set json pretty value
        chPretty.setSelected(settings.isJsonPretty());

        // Set font size value
        fontSlider.setValue(settings.getFontSize());

        // Set auto scroll status
        cbAutoScroll.setSelected(settings.isAutoScroll());

        // Disable loader
        main.setProgressVisible(false);
    }
}

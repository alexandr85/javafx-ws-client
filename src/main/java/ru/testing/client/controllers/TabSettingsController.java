package ru.testing.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.testing.client.common.db.DataBase;
import ru.testing.client.common.db.objects.Profile;
import ru.testing.client.common.db.objects.Settings;
import ru.testing.client.common.properties.DefaultProperties;

import java.util.List;

/**
 * Controller for settings tab form
 */
public class TabSettingsController {

    private ObservableList<ProfileBox> profileBoxList = FXCollections.observableArrayList();
    private DataBase dataBase = DataBase.getInstance();
    private MainController main;

    @FXML
    private ChoiceBox<ProfileBox> cbProfilesList;
    @FXML
    private CheckBox chWrap;
    @FXML
    private CheckBox chPretty;
    @FXML
    private TextField txRegex;
    @FXML
    private Label fontValueLabel;
    @FXML
    private Slider fontValueSlider;

    public TabSettingsController(MainController mainController) {
        main = mainController;
    }

    @FXML
    private void initialize() {

        // Slider value listener
        fontValueSlider.valueProperty().addListener(observable ->
                fontValueLabel.setText(
                        String.format("Font size: %spx", ((Number) fontValueSlider.getValue()).intValue())
                )
        );

        // Set value from database
        setSettingsValues(dataBase.getSettings());
    }

    /**
     * Load default settings
     */
    @FXML
    private void loadDefaultSettings() {
        setSettingsValues(DefaultProperties.getInstance().getMessageSettings());
    }

    private void saveCurrentSettings() {

    }

    /**
     * Set settings value from database
     */
    private void setSettingsValues(Settings settings) {

        // Enable loader
        main.setProgressVisible(true);

        // Add profiles list in choice box
        cbProfilesList.setItems(profileBoxList);
        updateProfilesList();

        // Set text wrap value
        chWrap.setSelected(settings.isTextWrap());

        // Set json pretty value
        chPretty.setSelected(settings.isJsonPretty());

        // Set regex value
        txRegex.setText(settings.getJsonRegex());

        // Set font size value
        fontValueSlider.setValue(settings.getFontSize());

        // Disable loader
        main.setProgressVisible(false);
    }

    /**
     * Update profile list from database
     */
    private void updateProfilesList() {
        List<Profile> profileList = dataBase.getProfiles();
        profileBoxList.clear();
        profileList.forEach(profile -> profileBoxList.add(new ProfileBox(profile.getId(), profile.getName())));
        cbProfilesList.getSelectionModel().select(profileBoxList.get(dataBase.getCurrentProfile()));
    }

    /**
     * Profile object in choice box element
     */
    private class ProfileBox {

        private int id;
        private String name;

        ProfileBox(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

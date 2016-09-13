package ru.testing.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import ru.testing.client.common.db.DataBase;
import ru.testing.client.common.db.objects.Profile;
import ru.testing.client.common.db.objects.ProfileName;
import ru.testing.client.common.db.objects.Settings;
import ru.testing.client.common.properties.DefaultProperties;
import ru.testing.client.elements.Dialogs;

/**
 * Controller for settings tab form
 */
public class TabSettingsController {

    private static final int TF_PROFILE_NAME_MAX_LENGTH = 20;
    private static final String SLIDER_LABEL_TEXT = "Font size: %spx";
    private ObservableList<ProfileName> profilesList = FXCollections.observableArrayList();
    private DataBase dataBase = DataBase.getInstance();
    private MainController main;

    @FXML
    private TextField tfProfileName;
    @FXML
    private Button bSaveProfile;
    @FXML
    private Button bRemove;
    @FXML
    private ComboBox<ProfileName> cbProfilesNames;
    @FXML
    private CheckBox chWrap;
    @FXML
    private CheckBox chPretty;
    @FXML
    private TextField tfRegex;
    @FXML
    private Label fontLabel;
    @FXML
    private Slider fontSlider;

    public TabSettingsController(MainController mainController) {
        main = mainController;
    }

    @FXML
    private void initialize() {

        // Slider value listener
        fontSlider.valueProperty().addListener(observable ->
                fontLabel.setText(
                        String.format(SLIDER_LABEL_TEXT, ((Number) fontSlider.getValue()).intValue())
                )
        );

        // Set value from database
        setSettingsValues(dataBase.getSettings());

        // Add profiles list in choice box
        cbProfilesNames.setItems(profilesList);
        updateProfilesList();

        // Choice box listener
        cbProfilesNames.setOnAction(event -> {
            if (cbProfilesNames.getItems().size() > 0) {
                if (cbProfilesNames.getSelectionModel().getSelectedItem().getId() == 0) {
                    bRemove.setDisable(true);
                } else {
                    bRemove.setDisable(false);
                }
            }
        });

        // New profile name text field listener
        tfProfileName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (tfProfileName.getText().length() > 0) {
                bSaveProfile.setDisable(false);
            } else {
                bSaveProfile.setDisable(true);
            }
            if (tfProfileName.getText().length() > TF_PROFILE_NAME_MAX_LENGTH) {
                String s = tfProfileName.getText().substring(0, TF_PROFILE_NAME_MAX_LENGTH);
                tfProfileName.setText(s);
            }
        });
        tfProfileName.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                addProfile();
            }
        });
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
        boolean status = dataBase.setSettings(new Settings(
                ((Number) fontSlider.getValue()).intValue(),
                chWrap.isSelected(),
                chPretty.isSelected(),
                tfRegex.getText()
        ));
        if (status) {
            new Dialogs().getInfoDialog("Settings save successful");
        } else {
            new Dialogs().getWarningDialog("Error save settings. See log.");
        }
    }

    /**
     * Add new profile
     */
    @FXML
    private void addProfile() {
        if (tfProfileName.getText().length() > 0) {
            int profileId = dataBase.addProfile(new Profile(
                    tfProfileName.getText(),
                    main.getServerUrl(),
                    main.isAutoScroll(),
                    main.isStatusBarShow(),
                    main.isFilterVisible(),
                    main.isFiltered()
            ));
            // todo: add filter list & messages
            updateProfilesList();
            tfProfileName.clear();
        }
    }

    /**
     * Remove selected profile
     */
    @FXML
    private void removeProfile() {
        int selectedId = cbProfilesNames.getSelectionModel().getSelectedItem().getId();
        if (selectedId == dataBase.getCurrentProfile()) {
            dataBase.setCurrentProfileId(0);
            selectCurrentProfile();
            loadSelectedProfile();
        }
        dataBase.removeProfile(selectedId);
        profilesList.removeIf(profileName -> profileName.getId() == selectedId);
        selectCurrentProfile();
    }

    /**
     * Load selected profile in choice box
     */
    @FXML
    private void loadSelectedProfile() {
        ProfileName selectedProfile = cbProfilesNames.getSelectionModel().getSelectedItem();
        boolean setCurrent = dataBase.setCurrentProfileId(selectedProfile.getId());
        boolean loadProfile = main.loadProfile(selectedProfile.getId());
        if (setCurrent && loadProfile) {
            new Dialogs().getInfoDialog(String.format("Profile `%s` load successful", selectedProfile.toString()));
        } else {
            new Dialogs().getWarningDialog("Error load selected profile. See log.");
        }
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

        // Set regex value
        tfRegex.setText(settings.getJsonRegex());

        // Set font size value
        fontSlider.setValue(settings.getFontSize());

        // Disable loader
        main.setProgressVisible(false);
    }

    /**
     * Update profile list from database
     */
    private void updateProfilesList() {
        profilesList.setAll(dataBase.getProfilesName());
        selectCurrentProfile();
    }

    /**
     * Get current profile object in list
     */
    private void selectCurrentProfile() {
        ProfileName profile = profilesList.get(0);
        int currentId = dataBase.getCurrentProfile();
        for (ProfileName p : profilesList) {
            if (p.getId() == currentId) {
                profile = p;
                break;
            }
        }
        cbProfilesNames.setValue(profile);
    }
}

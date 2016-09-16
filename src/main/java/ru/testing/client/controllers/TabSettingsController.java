package ru.testing.client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import ru.testing.client.common.db.DataBase;
import ru.testing.client.common.db.objects.Profile;
import ru.testing.client.common.db.objects.ProfileName;
import ru.testing.client.common.db.objects.Settings;
import ru.testing.client.common.properties.DefaultProperties;
import ru.testing.client.elements.Dialogs;
import ru.testing.client.elements.message.DetailTab;

/**
 * Controller for settings tab form
 */
public class TabSettingsController {

    private static final int TF_PROFILE_NAME_MAX_LENGTH = 20;
    private static final String FONT_SIZE_FORMAT = "-fx-font-size: %spx;";
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
                        String.format("%spx", ((Number) fontSlider.getValue()).intValue())
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
            main.getOutputTextView()
                    .setStyle(String.format(FONT_SIZE_FORMAT, dataBase.getSettings().getFontSize()));
            for (Tab tab : main.getTabPane().getTabs()) {
                if (tab instanceof DetailTab) {
                    Node tabNode = tab.getContent();
                    if (tabNode instanceof GridPane) {
                        for (Node node : ((GridPane) tabNode).getChildren()) {
                            if (node instanceof TextArea) {
                                node.setStyle(String.format(FONT_SIZE_FORMAT, dataBase.getSettings().getFontSize()));
                                break;
                            }
                        }
                    }
                }
            }
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
        String profileName = tfProfileName.getText();
        if (profileName.length() > 0) {
            main.setProgressVisible(true);
            int profileId = dataBase.addProfile(new Profile(
                    profileName,
                    main.getServerUrl(),
                    main.isAutoScroll(),
                    main.isStatusBarShow(),
                    main.isFilterVisible(),
                    main.isFiltered()
            ));
            if (profileId != 0) {
                dataBase.addHeaders(profileId, main.getHeadersList());


                // todo: add filter list & messages
                dataBase.setCurrentProfileId(profileId);
                updateProfilesList();
                tfProfileName.clear();
                cbProfilesNames.requestFocus();
                main.setProgressVisible(false);
                new Dialogs().getInfoDialog(String.format("Profile `%s` add & set as current", profileName));
            }
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
        dataBase.removeHeaders(selectedId);
        profilesList.removeIf(profileName -> profileName.getId() == selectedId);
        selectCurrentProfile();
    }

    /**
     * Load selected profile in combo box
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

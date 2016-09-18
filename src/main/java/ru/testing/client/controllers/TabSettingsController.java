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
import ru.testing.client.elements.message.DetailMsgTab;

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
    @FXML
    private CheckBox cbAutoScroll;
    @FXML
    private CheckBox cbShowBar;
    @FXML
    private CheckBox cbShowFilter;

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
        cbProfilesNames.setOnAction(event -> checkSelectedProfile());

        // New profile name text field listener
        tfProfileName.textProperty().addListener((observable, oldValue, newValue) -> {
            String text = tfProfileName.getText().trim();
            bSaveProfile.setDisable(text.length() <= 0);
            if (text.length() > TF_PROFILE_NAME_MAX_LENGTH) {
                tfProfileName.setText(text.substring(0, TF_PROFILE_NAME_MAX_LENGTH));
            }
        });
        tfProfileName.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                addNewProfile();
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

        // Enable loader
        main.setProgressVisible(true);

        // Save new settings in database
        boolean status = dataBase.setSettings(new Settings(
                ((Number) fontSlider.getValue()).intValue(),
                chWrap.isSelected(),
                chPretty.isSelected(),
                tfRegex.getText(),
                cbAutoScroll.isSelected(),
                cbShowBar.isSelected(),
                cbShowFilter.isSelected()
        ));
        if (status) {

            Settings settings = dataBase.getSettings();

            // Set font size for all messages
            main.getOutputTextView()
                    .setStyle(String.format(FONT_SIZE_FORMAT, settings.getFontSize()));
            for (Tab tab : main.getTabPane().getTabs()) {
                if (tab instanceof DetailMsgTab) {
                    Node tabNode = tab.getContent();
                    if (tabNode instanceof GridPane) {
                        for (Node node : ((GridPane) tabNode).getChildren()) {
                            if (node instanceof TextArea) {
                                node.setStyle(String.format(FONT_SIZE_FORMAT, settings.getFontSize()));
                                break;
                            }
                        }
                    }
                }
            }

            // Set auto scroll status
            main.setAutoScroll(!settings.isAutoScroll());

            // Set show bar status
            main.setStatusBarVisible(settings.isBarShow());

            // Set show filter status
            main.setFilterVisible(settings.isFilterShow());

            // Show successful dialog
            new Dialogs().getInfoDialog("Settings save successful", main, false);
        } else {
            new Dialogs().getWarningDialog("Error save settings. See log.", main, false);
        }
    }

    /**
     * Add new profile
     */
    @FXML
    private void addNewProfile() {

        // Enable loader
        main.setProgressVisible(true);

        // Add new profile by name
        String profileName = tfProfileName.getText();
        if (profileName.length() > 0) {
            int profileId = dataBase.addProfile(new Profile(
                    profileName,
                    main.getServerUrl()
            ));
            if (profileId != 0) {

                // Add headers
                dataBase.addHeaders(profileId, main.getHeadersList());

                // Add filters
                dataBase.addFilters(profileId, main.getFilterList());

                // Add send messages
                dataBase.addSendMessages(profileId, main.getSendMessages());

                // Add received messages
                dataBase.addReceivedMessages(profileId, main.getOutputTextView().getItems());

                // Prepare profiles list
                dataBase.setCurrentProfileId(profileId);
                updateProfilesList();
                tfProfileName.clear();
                cbProfilesNames.requestFocus();
                new Dialogs().getInfoDialog(String.format("Profile `%s` add & set as current", profileName), main, false);
            }
        }

        // Disable loader
        main.setProgressVisible(false);
    }

    /**
     * Load selected profile in combo box
     */
    @FXML
    private void loadSelectedProfile() {

        // Enable loader
        main.setProgressVisible(true);

        // Load selected profile
        ProfileName selectedProfile = cbProfilesNames.getSelectionModel().getSelectedItem();
        boolean setCurrent = dataBase.setCurrentProfileId(selectedProfile.getId());
        boolean loadProfile = main.loadProfile(selectedProfile.getId());
        if (setCurrent && loadProfile) {
            new Dialogs().getInfoDialog(String.format("Profile `%s` load successful", selectedProfile.toString()), main, false);
        } else {
            new Dialogs().getWarningDialog("Error load selected profile. See log.", main, false);
        }
    }

    /**
     * Remove selected profile
     */
    @FXML
    private void removeSelectedProfile() {

        // Enable loader
        main.setProgressVisible(true);

        // Get selected profile id
        int profileId = cbProfilesNames.getSelectionModel().getSelectedItem().getId();

        // Select current profile name in combo box
        if (profileId == dataBase.getCurrentProfileId()) {
            dataBase.setCurrentProfileId(0);
            selectCurrentProfile();
            loadSelectedProfile();
        }

        // Remove profile data
        dataBase.removeProfile(profileId);

        // Remove headers data
        dataBase.removeHeaders(profileId);

        // Remove filters data
        dataBase.removeFilters(profileId);

        // Remove send messages data
        dataBase.removeSendMessages(profileId);

        // Remove received messages data
        dataBase.removeReceivedMessages(profileId);

        // Remove profile name from list
        profilesList.removeIf(profileName -> profileName.getId() == profileId);
        selectCurrentProfile();

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

        // Set regex value
        tfRegex.setText(settings.getJsonRegex());

        // Set font size value
        fontSlider.setValue(settings.getFontSize());

        // Set auto scroll status
        cbAutoScroll.setSelected(settings.isAutoScroll());

        // Set show bar status
        cbShowBar.setSelected(settings.isBarShow());

        // Set show filter status
        cbShowFilter.setSelected(settings.isFilterShow());

        // Disable loader
        main.setProgressVisible(false);
    }

    /**
     * Update profile list from database
     */
    private void updateProfilesList() {
        profilesList.setAll(dataBase.getProfilesName());
        selectCurrentProfile();
        checkSelectedProfile();
    }

    /**
     * Get current profile object in list
     */
    private void selectCurrentProfile() {
        ProfileName profile = profilesList.get(0);
        int currentId = dataBase.getCurrentProfileId();
        for (ProfileName p : profilesList) {
            if (p.getId() == currentId) {
                profile = p;
                break;
            }
        }
        cbProfilesNames.setValue(profile);
    }

    /**
     * Check selected profile in combo box
     */
    private void checkSelectedProfile() {
        if (cbProfilesNames.getItems().size() > 0) {
            if (cbProfilesNames.getSelectionModel().getSelectedItem().getId() == 0) {
                bRemove.setDisable(true);
            } else {
                bRemove.setDisable(false);
            }
        }
    }
}

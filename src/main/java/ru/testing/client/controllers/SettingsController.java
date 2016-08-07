package ru.testing.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

/**
 * Controller for settings tab form
 */
public class SettingsController {

    private int fontValue;

    @FXML
    private Label fontValueLabel;
    @FXML
    private Slider fontValueSlider;

    public SettingsController(MainController mainController) {

    }

    @FXML
    private void initialize() {
        fontValueSlider.valueProperty().addListener(observable -> {
            setFontValue(((Number) fontValueSlider.getValue()).intValue());
            fontValueLabel.setText(String.format("Font size: %spx", getFontValue()));
        });
    }

    public int getFontValue() {
        return fontValue;
    }

    public void setFontValue(int fontValue) {
        this.fontValue = fontValue;
    }
}

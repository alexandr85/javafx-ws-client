package ru.testing.client.elements.tabs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.common.objects.Header;
import ru.testing.client.common.objects.HttpParameter;

import java.io.IOException;
import java.util.List;

/**
 * Tab for create new client instance
 */
public class NewClientTab extends Tab {

    private static final Logger LOGGER = Logger.getLogger(SettingsTab.class.getName());
    private String prevUrl;
    private HttpTypes prevType;
    private List<Header> prevHeaders;
    private List<HttpParameter> prevHttpParams;

    public NewClientTab() {
        setClosable(false);
        var tp = new Tooltip("Create new client instance");
        tp.setShowDelay(new Duration(10));
        setTooltip(tp);
        setText("NEW");

        try {
            var image = new ImageView(getClass().getResource("/images/add.png").toExternalForm());
            image.setFitHeight(12.0);
            image.setFitWidth(12.0);
            setGraphic(image);
        } catch (Exception e) {
            LOGGER.error("Image not found", e);
        }


        try {
            var loader = new FXMLLoader(getClass().getResource("/views/tab.new.client.fxml"));
            Parent root = loader.load();
            setContent(root);
        } catch (IOException e) {
            System.out.println("Error load views form: " + e);
        }
    }

    public String getPrevUrl() {
        return prevUrl;
    }

    public void setPrevUrl(String prevUrl) {
        this.prevUrl = prevUrl;
    }

    public HttpTypes getPrevType() {
        return prevType;
    }

    public void setPrevType(HttpTypes prevType) {
        this.prevType = prevType;
    }

    public List<Header> getPrevHeaders() {
        return prevHeaders;
    }

    public void setPrevHeaders(List<Header> prevHeaders) {
        this.prevHeaders = prevHeaders;
    }

    public List<HttpParameter> getPrevHttpParams() {
        return prevHttpParams;
    }

    public void setPrevHttpParams(List<HttpParameter> prevHttpParams) {
        this.prevHttpParams = prevHttpParams;
    }
}

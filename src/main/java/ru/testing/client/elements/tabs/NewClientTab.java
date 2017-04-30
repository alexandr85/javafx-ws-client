package ru.testing.client.elements.tabs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testing.client.common.HttpTypes;
import ru.testing.client.common.objects.Header;
import ru.testing.client.common.objects.HttpParameter;

import java.io.IOException;
import java.util.List;

/**
 * Tab for create new client instance
 */
public class NewClientTab extends Tab {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsTab.class.getName());
    private String prevUrl;
    private HttpTypes prevType;
    private List<Header> prevHeaders;
    private List<HttpParameter> prevHttpParams;

    public NewClientTab() {
        setClosable(false);
        setTooltip(new Tooltip("Create new client instance"));
        setText("NEW");
        ImageView image = new ImageView("/images/add.png");
        image.setFitHeight(20.0);
        image.setFitWidth(20.0);
        setGraphic(image);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/tab.new.client.fxml"));
            Parent root = loader.load();
            setContent(root);
        } catch (IOException e) {
            LOGGER.error("Error load view form: {}", e);
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

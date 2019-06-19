module wsr.inspector {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires java.desktop;
    requires java.sql;
    requires h2;
    requires tyrus.client;
    requires tyrus.extension.deflate;
    requires javax.websocket.api;
    requires gson;
    requires commons.lang3;
    requires jersey.client;
    requires jersey.core;
    requires jsr311.api;
    requires log4j;

    exports ru.testing.client.controllers;
    exports ru.testing.client;
    exports ru.testing.client.websocket;
    exports ru.testing.client.common.objects;

    opens ru.testing.client.common.github to gson;
    opens ru.testing.client.controllers to javafx.graphics, javafx.fxml;
}
module wsr.inspector {
    requires java.desktop;
    requires java.sql;
    requires java.net.http;
    requires javax.websocket.api;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires tyrus.client;
    requires tyrus.extension.deflate;
    requires gson;
    requires log4j;

    exports ru.testing.client.controllers;
    exports ru.testing.client;
    exports ru.testing.client.websocket;
    exports ru.testing.client.common.objects;

    opens ru.testing.client.common.github to gson;
    opens ru.testing.client.controllers to javafx.graphics, javafx.fxml;
    opens ru.testing.client.common.properties to gson;
}
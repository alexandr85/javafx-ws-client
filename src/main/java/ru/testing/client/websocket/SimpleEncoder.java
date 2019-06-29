package ru.testing.client.websocket;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class SimpleEncoder implements Encoder.Text<String> {

    @Override
    public String encode(String object) {
        return null;
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}

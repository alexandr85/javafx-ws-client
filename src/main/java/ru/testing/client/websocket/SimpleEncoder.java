package ru.testing.client.websocket;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

@SuppressWarnings("WeakerAccess")
public class SimpleEncoder implements Encoder.Text<String> {

    @Override
    public String encode(String object) throws EncodeException {
        return null;
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}

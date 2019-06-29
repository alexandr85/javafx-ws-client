package ru.testing.client.websocket;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class SimpleDecoder implements Decoder.Text<String> {

    @Override
    public String decode(String s) {
        return null;
    }

    @Override
    public boolean willDecode(String s) {
        return false;
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}

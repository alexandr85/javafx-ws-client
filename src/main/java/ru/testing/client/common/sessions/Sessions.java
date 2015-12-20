package ru.testing.client.common.sessions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Xml root element for save sessions list
 */
@XmlRootElement(name = "sessions")
public class Sessions {

    private List<Session> session;

    public List<Session> getSession() {
        return session;
    }

    @XmlElement(name = "session")
    public void setSession(List<Session> session) {
        this.session = session;
    }
}

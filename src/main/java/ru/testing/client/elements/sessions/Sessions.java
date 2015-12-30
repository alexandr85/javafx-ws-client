package ru.testing.client.elements.sessions;

import ru.testing.client.elements.sessions.session.Session;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Xml root element for save sessions list
 */
@XmlRootElement(name = "sessions")
public class Sessions {

    private List<Session> sessions;

    public List<Session> getSessions() {
        return sessions;
    }

    @XmlElement(name = "session")
    public void setSessions(List<Session> session) {
        this.sessions = session;
    }
}

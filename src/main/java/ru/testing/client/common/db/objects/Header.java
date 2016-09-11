package ru.testing.client.common.db.objects;

/**
 * Headers list in database
 */
public class Header {

    private int profileId;
    private String name;
    private String value;

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Header(int profileId, String name, String value) {
        this.profileId = profileId;
        this.name = name;
        this.value = value;
    }

    public int getProfileId() {
        return profileId;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}

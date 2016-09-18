package ru.testing.client.common.db.objects;

/**
 * Profile object for save in database
 */
public class Profile {

    private int id;
    private String name;
    private String url;

    public Profile(int id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public Profile(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}

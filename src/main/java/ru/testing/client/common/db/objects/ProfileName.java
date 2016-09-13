package ru.testing.client.common.db.objects;

/**
 * Profile object in choice box element
 */
public class ProfileName {

    private int id;
    private String name;

    public ProfileName(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}

package ru.testing.client.common.github;

import com.google.gson.annotations.SerializedName;

/**
 * Git hub tag info
 */
class TagInfo {

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}

package com.servilat.intouch.friends;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendItem {
    private String userID;
    private String userName;
    private String imageURL;

    public FriendItem(String userID, String userName, String imageURL) {
        this.userID = userID;
        this.userName = userName;
        this.imageURL = imageURL;
    }

    public FriendItem(JSONObject user) {
        try {
            this.userID = user.getString("id");
            this.userName = user.getString("first_name") + " " + user.getString("last_name");
            this.imageURL = user.getString("photo_100");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getImageURL() {
        return imageURL;
    }
}

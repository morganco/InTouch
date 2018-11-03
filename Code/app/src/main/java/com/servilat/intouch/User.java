package com.servilat.intouch;

import java.io.Serializable;

public class User implements Serializable {
    private String imageURL;
    private String dialogName;
    private String userID;

    public User(String imageURL, String dialogName, String userID) {
        this.imageURL = imageURL;
        this.dialogName = dialogName;
        this.userID = userID;
    }

    public String getDialogName() {
        return dialogName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getUserID() {
        return userID;
    }
}

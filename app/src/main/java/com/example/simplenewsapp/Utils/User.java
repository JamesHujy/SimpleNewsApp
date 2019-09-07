package com.example.simplenewsapp.Utils;

import android.graphics.Bitmap;

public class User {
    private String username;
    private String password;

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String getUser()
    {
        return username;
    }
}

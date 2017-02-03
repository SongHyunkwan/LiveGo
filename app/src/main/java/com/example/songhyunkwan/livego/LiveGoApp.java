package com.example.songhyunkwan.livego;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by songhyunkwan on 2017. 1. 29..
 */
public class LiveGoApp extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);

    }
}

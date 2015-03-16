package com.thales.thalesquizzmbaas;

import android.app.Application;
import com.firebase.client.Firebase;
import com.thales.thalesquizzmbaas.mbass.Mbaas;

public class ThalesQuizzMbaasApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        //Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        Firebase.setAndroidContext(this);
        Mbaas.getInstance(this);
    }
}

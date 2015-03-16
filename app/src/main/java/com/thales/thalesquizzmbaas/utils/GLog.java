package com.thales.thalesquizzmbaas.utils;

import android.util.Log;
import com.thales.thalesquizzmbaas.ui.BuildConfig;

public class GLog {

    public static void e(Object object, String log) {
        if (BuildConfig.DEBUG) {
            Log.e(object.getClass().getSimpleName(), log);
        }
    }

    public static void e(String tag, String log) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, log);
        }
    }

    public static void w(Object object, String log) {
        if (BuildConfig.DEBUG) {
            Log.w(object.getClass().getSimpleName(), log);
        }
    }

    public static void w(String tag, String log) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, log);
        }
    }

    public static void d(Object object, String log) {
        if (BuildConfig.DEBUG) {
            Log.d(object.getClass().getSimpleName(), log);
        }
    }

    public static void d(String tag, String log) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, log);
        }
    }

    public static void i(Object object, String log) {
        if (BuildConfig.DEBUG) {
            Log.i(object.getClass().getSimpleName(), log);
        }
    }

    public static void i(String tag, String log) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, log);
        }
    }

    public static void v(Object object, String log) {
        if (BuildConfig.DEBUG) {
            Log.v(object.getClass().getSimpleName(), log);
        }
    }

    public static void v(String tag, String log) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, log);
        }
    }
}

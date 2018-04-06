package com.dhivakar.mysamples.utils;

import android.os.Build;
import android.util.Log;

import com.dhivakar.mysamples.BuildConfig;

public class LogUtils {

    private static String getClassName(Object obj) {
        String className = "";

        try{
            className = obj.getClass().getSimpleName();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if(className.isEmpty() || className.toLowerCase().equals("class")) {
            try {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                className = obj.getClass().getTypeName();
                else
                    className = obj.getClass().getCanonicalName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(className.isEmpty())
            className = "LogUtils";
        return className;
    }

    public static void i(Object obj, String message) {
        Log.i(getClassName(obj), message);
    }

    public static void d(Object obj, String message) {
        Log.d(getClassName(obj), message);
    }

    public static void v(Object obj, String message) {
        Log.v(getClassName(obj), message);
    }

    public static void w(Object obj, String message) {
        Log.w(getClassName(obj), message);
    }

    public static void e(Object obj, String message) {
        Log.e(getClassName(obj), message);
    }
}

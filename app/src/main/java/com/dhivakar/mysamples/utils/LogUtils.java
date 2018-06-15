package com.dhivakar.mysamples.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

//import com.dhivakar.mysamples.BuildConfig;

public class LogUtils {

    private static String getClassName(Object obj) {
        String className = "";

        try{
            if(obj.getClass() != null)
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

    public static void i(String TAG, String message) {
        Log.i(TAG, message);
    }

    public static void i(Object obj, String message) {
        i(getClassName(obj), message);
    }

    public static void i(String TAG, String message, boolean showToast, Context context) {
        i(TAG, message);
        //if(showToast) Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void d(String TAG, String message) {
        Log.i(TAG, message);
    }

    public static void d(Object obj, String message) {
        d(getClassName(obj), message);
    }

    public static void v(String TAG, String message) {
        Log.v(TAG, message);
    }

    public static void v(Object obj, String message) {
        v(getClassName(obj), message);
    }

    public static void w(String TAG, String message) {
        Log.w(TAG, message);
    }

    public static void w(Object obj, String message) {
        w(getClassName(obj), message);
    }

    public static void e(String TAG, String message) {
        Log.e(TAG, message);
    }

    public static void e(Object obj, String message) {
        e(getClassName(obj), message);
    }

    public static void e(String TAG, String message, boolean showToast, Context context){
        e(TAG, message);
        //if(showToast) Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

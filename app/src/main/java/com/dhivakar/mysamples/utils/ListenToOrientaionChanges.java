package com.dhivakar.mysamples.utils;

import android.content.Context;
import android.view.OrientationEventListener;

import com.dhivakar.mysamples.MainActivity;

public class ListenToOrientaionChanges extends OrientationEventListener {

    private static ListenToOrientaionChanges _intent = null;

    public static ListenToOrientaionChanges get_intent() {
        return _intent;
    }

    public static void StartListening(Context context, Callback callback) {
        if (_intent == null) _intent = new ListenToOrientaionChanges(context, callback);
        LogUtils.i(_intent, "StartListening");
    }

    public static void StopListening()
    {
        LogUtils.i(_intent, "StopListening");;
        if(_intent != null)
            _intent.disable();
        _intent = null;
    }

    public interface Callback
    {
        void onOrientationChanged(int orientation);
    }

    private Callback callback = null;
    private ListenToOrientaionChanges(Context context, Callback callback) {
        super(context);
        LogUtils.i(this, "onCreate canDetectOrientation?" + canDetectOrientation());
        enable();
        if((Callback)context != null)
            LogUtils.i(this, "context can be used as callback too");
        this.callback = callback;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        LogUtils.i(this, "onOrientationChanged orientation:" + orientation);
        if(callback != null)
            callback.onOrientationChanged(orientation);
    }
}

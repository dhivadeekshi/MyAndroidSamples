package com.dhivakar.mysamples.notifications;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.dhivakar.mysamples.utils.LogUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static String FirebaseCMToken = "";

    public static void FetchFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            try {
                                FirebaseCMToken = task.getResult().getToken();
                                LogUtils.d("FCM","Token:"+FirebaseCMToken);
                            } catch (NullPointerException e) {
                                Crashlytics.log("Exception on FCM Fetch Token");
                                Crashlytics.logException(e);
                            }
                        }
                    }
                });
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        LogUtils.d(this, "onNewToken : "+token);
        FirebaseCMToken = token;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        LogUtils.d(this, "onMessageReceived");
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }
}

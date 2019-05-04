package com.dhivakar.mysamples.notifications;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dhivakar.mysamples.BaseAppCompatActivity;
import com.dhivakar.mysamples.R;

public class NotificationsManager extends BaseAppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_manager);
        UpdateActivityHeader(getString(R.string.notification_samples));
        UpdateFCMToken();
    }

    @Override
    public void onBackPressed() {
        Log.i(getLocalClassName(), "onBackPressed");
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId())
        {
            case R.id.buttonNotificationBasic: ShowSimpleNotification(); break;
            case R.id.buttonNotificationLargeIcon: ShowLargeIconNotification(); break;
            case R.id.buttonNotificationBigPicture: ShowBigImageStyleNotification(); break;
            case R.id.buttonNotificationInboxStyle: ShowInboxStyleNotification(); break;
            case R.id.buttonNotificationBigText: ShowBigTextStyleNotification(); break;
            case R.id.buttonNotificationActions: ShowNotificationWithActions(); break;
            case R.id.buttonNotificationProgress: ShowNotificationWithProgress(); break;
            case R.id.buttonNotificationOnGoing: ShowOnGoingNotification(); break;
            case R.id.buttonNotificationDisableWhen: DisableWhenInNotification(); break;
            default:
                break;

        }
    }

    private void UpdateFCMToken()
    {
        TextView fcmToken = (TextView) findViewById(R.id.fcmTokenText);
        if(MyFirebaseMessagingService.FirebaseCMToken.isEmpty())
            MyFirebaseMessagingService.FetchFCMToken();
        fcmToken.setText(MyFirebaseMessagingService.FirebaseCMToken);
    }

    private void ShowSimpleNotification()
    {
        NotificationHelper.CreateBasicNotification(
                "Basic",
                "Sample Notification to show how notification works",
                NotificationHelper.SMALL_ICON_DEFAULT,
                NotificationHelper.CHANNEL_ID_DEFAULT,
                this);
    }

    private void ShowLargeIconNotification()
    {
        NotificationHelper.CreateLargeIconNotification(
                "Large Icon",
                "Sample Notification to show Large Icon",
                NotificationHelper.SMALL_ICON_DEFAULT,
                R.drawable.medal_512,
                NotificationHelper.CHANNEL_ID_DEFAULT,
                this);
    }

    private void ShowBigTextStyleNotification()
    {
        NotificationHelper.CreateBigTextNotification(
                "Big Text Style",
                "Sample Notification to show Big Text Style Notification works, " +
                        "Since it's big text style it can hold more text when expanded",
                NotificationHelper.SMALL_ICON_DEFAULT,
                NotificationHelper.CHANNEL_ID_DEFAULT,
                this);
    }

    private void ShowInboxStyleNotification()
    {
        String[] multiLineMessage = {
                "This is a new line of information to show how inbox style actually works",
                "You can see how the inbox style shows more and more info in a inbox style",
                "You can have more information added to a single notification as a summery"
        };

        NotificationHelper.CreateInboxNotification(
                "Inbox Style",
                "Sample Notification to show Inbox Style Notification works, " +
                        "Since it's inbox style it can hold more text when expanded",
                NotificationHelper.SMALL_ICON_DEFAULT,
                NotificationHelper.CHANNEL_ID_DEFAULT,
                this,
                multiLineMessage);
    }

    private void ShowBigImageStyleNotification()
    {
        NotificationHelper.CreateBigPictureNotification(
                "BigPicture Style",
                "Sample notification to show how to attach a big picture along with the message",
                NotificationHelper.SMALL_ICON_DEFAULT,
                R.drawable.image_android_versions,
                NotificationHelper.CHANNEL_ID_DEFAULT,
                this);
    }

    private void ShowNotificationWithActions()
    {
        NotificationCompat.Builder builder = NotificationHelper.CreateNotification(
                "Take Action",
                "Sample notification to show how Actions in notification works",
                NotificationHelper.SMALL_ICON_DEFAULT,
                NotificationHelper.CHANNEL_ID_DEFAULT,
                this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH)
        {
            // Adding action using NotificationCompat.Action was only introduced in Api level 20
            builder.addAction(new NotificationCompat.Action(R.mipmap.ic_launcher,"Proceed",null));
            builder.addAction(new NotificationCompat.Action(R.mipmap.ic_launcher,"Cancel",null));
        }
        else {
            builder.addAction(R.mipmap.ic_launcher, "Proceed", null);
            builder.addAction(R.mipmap.ic_launcher, "Cancel", null);
        }
        NotificationHelper.PublishNotification(this, builder, 0);
    }

    private int notificationIdProgress = 999;
    private int notificationProgressMax = 100;
    private int notificationProgress = 0;
    boolean useBuilder = false;
    private NotificationCompat.Builder builder = null;
    private Thread notificationProgressThread = null;

    private Thread CreateNotificationProgressThread() {

        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (notificationProgress < notificationProgressMax) {
                        ShowNotificationWithProgress(notificationProgressMax, notificationProgress, notificationProgress == 0);
                        notificationProgress++;
                        Thread.sleep(1000); // Execute every 1 sec
                    }
                    if (notificationProgress == notificationProgressMax)
                        ShowNotificationWithProgressCompleted();
                } catch (InterruptedException e) {

                }
            }
        });
    }

    private void ShowNotificationWithProgress(int max, int progress, boolean intermediate) {
        if(!useBuilder || builder == null) {
            NotificationHelper.CreateProgressNotification(
                    "Progress",
                    "Sample notification to show progress in notification works",
                    NotificationHelper.SMALL_ICON_DEFAULT,
                    max,
                    progress,
                    intermediate,
                    NotificationHelper.CHANNEL_ID_DEFAULT,
                    this,
                    notificationIdProgress);
        }
        else {
            builder.setProgress(max, progress, intermediate);
            NotificationHelper.PublishNotification(this, builder, notificationIdProgress);
        }
    }

    private void ShowNotificationWithProgressCompleted()
    {
        NotificationHelper.CreateBigTextNotification(
                "Progress",
                "Sample notification to show show progress works completed",
                NotificationHelper.SMALL_ICON_DEFAULT,
                NotificationHelper.CHANNEL_ID_DEFAULT,
                this,
                notificationIdProgress);
    }

    private void ShowNotificationWithProgress() {
        if (notificationProgressThread != null &&
                notificationProgressThread.isAlive() &&
                !notificationProgressThread.isInterrupted())
            notificationProgressThread.interrupt();

        notificationProgress = 0;
        if (useBuilder) {
            builder = NotificationHelper.CreateNotification(
                    "Progress",
                    "Showing progress",
                    NotificationHelper.SMALL_ICON_DEFAULT,
                    NotificationHelper.CHANNEL_ID_DEFAULT,
                    this);
        }

        notificationProgressThread = CreateNotificationProgressThread();
        notificationProgressThread.start();
    }

    private void ShowOnGoingNotification()
    {
        NotificationCompat.Builder builder = NotificationHelper.CreateNotification(
                "Ongoing",
                "Sample notification which stays persistent since it's ongoing",
                NotificationHelper.SMALL_ICON_DEFAULT,
                NotificationHelper.CHANNEL_ID_DEFAULT,
                this);
        builder.setOngoing(true);

        //builder.setShowWhen(false);
        //builder.setUsesChronometer(true);

        NotificationHelper.PublishNotification(this, builder, 0);
    }

    private void DisableWhenInNotification()
    {
        NotificationCompat.Builder builder = NotificationHelper.CreateNotification(
                "DisableWhen",
                "Sample notification for which the time stamp is disabled and colored red",
                NotificationHelper.SMALL_ICON_DEFAULT,
                NotificationHelper.CHANNEL_ID_DEFAULT,
                this);

        builder.setShowWhen(false);
        builder.setColor(Color.RED);
        //builder.setUsesChronometer(true);

        NotificationHelper.PublishNotification(this, builder, 0);
    }
}

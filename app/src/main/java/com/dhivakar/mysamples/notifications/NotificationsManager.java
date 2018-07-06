package com.dhivakar.mysamples.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.dhivakar.mysamples.BaseAppCompatActivity;
import com.dhivakar.mysamples.R;

public class NotificationsManager extends BaseAppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_manager);
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
            case R.id.buttonNotificationShowWhen: ShowWhenInNotification(); break;
            default:
                break;

        }
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
        //NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_launcher_background,"Action Title",null);
        builder.addAction(R.mipmap.ic_launcher,"Proceed",null);
        builder.addAction(R.mipmap.ic_launcher,"Cancel",null);
        NotificationHelper.PublishNotification(this, builder, 0);
    }

    private void ShowNotificationWithProgress()
    {
        NotificationHelper.CreateProgressNotification(
                "Progress",
                "Sample notification to show how to show progress in notifications",
                NotificationHelper.SMALL_ICON_DEFAULT,
                100,
                37,
                false,
                NotificationHelper.CHANNEL_ID_DEFAULT,
                this);
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

    private void ShowWhenInNotification()
    {
        NotificationCompat.Builder builder = NotificationHelper.CreateNotification(
                "Ongoing",
                "Sample notification to the shows when along with the notification",
                NotificationHelper.SMALL_ICON_DEFAULT,
                NotificationHelper.CHANNEL_ID_DEFAULT,
                this);

        builder.setShowWhen(false);
        builder.setColor(Color.RED);
        //builder.setUsesChronometer(true);

        NotificationHelper.PublishNotification(this, builder, 0);
    }
}

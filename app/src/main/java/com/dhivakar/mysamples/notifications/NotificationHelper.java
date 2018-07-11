package com.dhivakar.mysamples.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.dhivakar.mysamples.MainActivity;
import com.dhivakar.mysamples.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {

    public static final String CHANNEL_ID_DEFAULT = "channel_default";
    public static final int NOTIFICATION_ID_DEFAULT = 0;
    public static final int REQUEST_CODE_DEFAULT = 0;
    public static final int SMALL_ICON_DEFAULT = R.mipmap.ic_launcher;
    public static final long[] VIBRATION_PATTERN_DEFAULT = new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400};

    public static void CreateNotificationChannels(Context context)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CreateNotificationChannel(context,CHANNEL_ID_DEFAULT, "Notification", "Notification Channel", NotificationManager.IMPORTANCE_HIGH, Color.RED, true);
        }
    }

    private static void CreateNotificationChannel(Context context,String channelId, String channelName, String channelDescription, int channelImportance, int channelColor, boolean enableLight) {
        CreateNotificationChannel(context, channelId, channelName, channelDescription, channelImportance, channelColor, enableLight, false, VIBRATION_PATTERN_DEFAULT);
    }

    private static void CreateNotificationChannel(Context context,String channelId, String channelName, String channelDescription, int channelImportance, int channelColor, boolean enableLight, boolean enableVibration) {
        CreateNotificationChannel(context, channelId, channelName, channelDescription, channelImportance, channelColor, enableLight, enableVibration, VIBRATION_PATTERN_DEFAULT);
    }

    private static void CreateNotificationChannel(Context context,String channelId, String channelName, String channelDescription, int channelImportance, int channelColor, boolean enableLights, boolean enableVibration, long[] vibrationPattern)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, channelImportance);
            // Configure the notification channel.
            mChannel.setDescription(channelDescription);
            mChannel.enableLights(enableLights);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(channelColor);
            mChannel.enableVibration(enableVibration);
            mChannel.setVibrationPattern(vibrationPattern);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    public static void CreateBasicNotification(String title, String message, int smallIcon, String channelId, Context context)
    {
        CreateBasicNotification(title, message, smallIcon, channelId, context, NOTIFICATION_ID_DEFAULT);
    }

    public static void CreateBasicNotification(String title, String message, int smallIcon, String channelId, Context context, int notificationId)
    {
        PublishNotification(context, CreateNotification(title, message, smallIcon, channelId, context), notificationId);
    }

    public static void CreateLargeIconNotification(String title, String message, int smallIcon, int largeIcon, String channelId, Context context) {
        CreateLargeIconNotification(title, message, smallIcon, largeIcon, channelId, context, NOTIFICATION_ID_DEFAULT);
    }

    public static void CreateLargeIconNotification(String title, String message, int smallIcon, int largeIcon, String channelId, Context context, int notificationId)
    {
        NotificationCompat.Builder builder = CreateNotification(title, message, smallIcon, channelId, context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),largeIcon));
        PublishNotification(context, builder, notificationId);
    }

    public static void CreateBigPictureNotification(String title, String message, int smallIcon, int bigImage, String channelId, Context context) {
        CreateBigPictureNotification(title, message, smallIcon, bigImage, channelId, context, NOTIFICATION_ID_DEFAULT);
    }

    public static void CreateBigPictureNotification(String title, String message, int smallIcon, int bigImage, String channelId, Context context, int notificationId)
    {
        // Create Big Picture Style
        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle(title);
        style.bigPicture(BitmapFactory.decodeResource(context.getResources(),bigImage));

        // Create notification and publish
        NotificationCompat.Builder builder = CreateNotification(title, message, smallIcon, channelId, context)
                .setStyle(style);
        PublishNotification(context, builder, notificationId);
    }

    public static void CreateInboxNotification(String title, String message, int smallIcon, String channelId, Context context, String[] multiLineMessage) {
        CreateInboxNotification(title, message, smallIcon, channelId, context, NOTIFICATION_ID_DEFAULT, multiLineMessage);
    }

    public static void CreateInboxNotification(String title, String message, int smallIcon, String channelId, Context context, int notificationId, String[] multiLineMessage)
    {
        // Create Inbox Style
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        style.setBigContentTitle(title);
        style.addLine(message);

        // Adding multiple line of messages as inbox style
        if(multiLineMessage != null)
            for (String line :
                    multiLineMessage) {
                style.addLine(line);
            }

        // Create notification and publish
        NotificationCompat.Builder builder = CreateNotification(title, message, smallIcon, channelId, context)
                .setStyle(style);
        PublishNotification(context, builder, notificationId);
    }

    public static void CreateBigTextNotification(String title, String message, int smallIcon, String channelId, Context context) {
        CreateBigTextNotification(title, message, smallIcon, channelId, context, NOTIFICATION_ID_DEFAULT);
    }

    public static void CreateBigTextNotification(String title, String message, int smallIcon, String channelId, Context context, int notificationId)
    {
        // Create Big Text Style
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.setBigContentTitle(title);

        NotificationCompat.Builder builder = CreateNotification(title, message, smallIcon, channelId, context)
                .setStyle(style);
        PublishNotification(context, builder, notificationId);
    }

    public static void CreateProgressNotification(String title, String message, int smallIcon, int max, int progress, String channelId, Context context)
    {
        CreateProgressNotification(title, message, smallIcon, max, progress, false, channelId, context,NOTIFICATION_ID_DEFAULT);
    }

    public static void CreateProgressNotification(String title, String message, int smallIcon, int max, int progress, boolean intermediate, String channelId, Context context)
    {
        CreateProgressNotification(title, message, smallIcon, max, progress, intermediate, channelId, context,NOTIFICATION_ID_DEFAULT);
    }

    public static void CreateProgressNotification(String title, String message, int smallIcon, int max, int progress, boolean intermediate, String channelId, Context context, int notificationId)
    {
        NotificationCompat.Builder builder = CreateBigTextNotificationBuilder(title, message, smallIcon, channelId, context)
                .setProgress(max,progress,intermediate);
        PublishNotification(context, builder, notificationId);
    }

    public static NotificationCompat.Builder CreateNotification(String title, String message, int smallIcon, String channelId, Context context)
    {
        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder = new NotificationCompat.Builder(context, channelId);
        else
            builder = new NotificationCompat.Builder(context);

        builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true)
                .setContentIntent(DefaultPendingIntent(context));

        return builder;
    }

    public static NotificationCompat.Builder CreateBigTextNotificationBuilder(String title, String message, int smallIcon, String channelId, Context context)
    {
        // Create Big Text Style
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.setBigContentTitle(title);

        NotificationCompat.Builder builder = CreateNotification(title, message, smallIcon, channelId, context)
                .setStyle(style);
        return builder;
    }

    public static void PublishNotification(Context context, NotificationCompat.Builder builder, int notificationId)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if(notificationManager != null)
            notificationManager.notify(notificationId, builder.build());
    }

    public static void CancelNotification(Context context, int notificationId){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if(notificationManager != null)
            notificationManager.cancel(notificationId);
    }

    public static void CancelAllNotifications(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if(notificationManager != null)
            notificationManager.cancelAll();
    }

    public static PendingIntent DefaultPendingIntent(Context context)
    {
        return DefaultPendingIntentFor(context, MainActivity.class);
    }

    public static PendingIntent DefaultPendingIntentFor(Context context, Class activityClass)
    {
        return DefaultPendingIntentFor(context, activityClass, REQUEST_CODE_DEFAULT);
    }

    public static PendingIntent DefaultPendingIntentFor(Context context, Class activityClass, int requestCode)
    {
        Intent resultIntent = new Intent(context, activityClass);
        return PendingIntent.getActivity(context, requestCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}

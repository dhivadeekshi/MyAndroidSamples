package com.dhivakar.mysamples.notifications;

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

import com.dhivakar.mysamples.BaseAppCompatActivity;
import com.dhivakar.mysamples.R;

public class NotificationsManager extends BaseAppCompatActivity {

    private static final String defaultNotificationChannelId = "default_notif_channel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_manager);



        CreateNotificationChannel(defaultNotificationChannelId);
        Button button = (Button) findViewById(R.id.send_local_notif);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Dhivakar","Send LocalNotification");
                SendSampleNotification();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.i(getLocalClassName(), "onBackPressed");
        super.onBackPressed();
    }



    private void CreateNotificationChannel(String channelId)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// The id of the channel.
            String id = "my_channel_01";
// The user-visible name of the channel.
            CharSequence name = getString(R.string.channel_name);
// The user-visible description of the channel.
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
// Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
// Sets the notification light color for notifications posted to this
// channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private static int notificationId = 0;
    private void SendSampleNotification()
    {
        String body = "Sample Notification Sample asmple asample sample sample";
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, defaultNotificationChannelId)
                .setContentTitle("Hello")
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher)
                .setColor(notificationId==0?Color.RED:notificationId==1?Color.GREEN:Color.BLUE)
                .setColorized(true)
                .setDeleteIntent(pi)
                .setContentIntent(pi)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));

        if(notificationId == 0) {
            builder.setOngoing(true);
            builder.setShowWhen(false);
            //builder.setUsesChronometer(true);
        }
        if(notificationId == 1) {
            builder.setProgress(100, 37, false);
            NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_launcher,"Action Title",null);
            builder.addAction(R.drawable.ic_launcher,"Proceed",null);
            builder.addAction(R.drawable.ic_launcher,"Cancel",null);
        }
        if(notificationId == 2)
        {
            NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
            style.setBigContentTitle("Big Picture Title");
            style.bigPicture(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));

            builder.setStyle(style);
			/*NotificationCompat.Action.Extender extender = new NotificationCompat.Action.Extender() {
				@Override
				public NotificationCompat.Action.Builder extend(NotificationCompat.Action.Builder builder) {
					return null;
				}
			};
			builder.extend((NotificationCompat.Extender) extender);*/
        }


        if(notificationId == 0){
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle("Hello");
            inboxStyle.addLine(body);
            inboxStyle.addLine("Line 2");

            builder.setStyle(inboxStyle);}

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(notificationManager != null)
            notificationManager.notify(notificationId++, builder.build());
    }
}

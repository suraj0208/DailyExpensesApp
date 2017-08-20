package com.suraj.dailyexpenses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by suraj on 20/8/17.
 */
public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Intent mainActivityIntent = new Intent(context, MainActivity.class);

        mainActivityIntent.putExtra("name",intent.getStringExtra("name"));
        mainActivityIntent.putExtra("rs",intent.getIntExtra("rs",-1));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("Daily Expenses")
                        .setContentText("Spent on " + intent.getStringExtra("name") + "? " + "Write it down!")
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(965778, mBuilder.build());
    }
}

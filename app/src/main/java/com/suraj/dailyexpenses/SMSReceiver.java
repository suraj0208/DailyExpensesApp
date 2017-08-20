package com.suraj.dailyexpenses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

/**
 * Created by suraj on 12/8/17.
 */
public class SMSReceiver extends BroadcastReceiver {
    public static final String[] debitCardTransact = {"thank you for using", "debit card", "ending"};

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = null;
            String messageFrom;

            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        messageFrom = messages[i].getOriginatingAddress();
                        String messageBody = messages[i].getMessageBody().toLowerCase();

                        boolean in = true;

                        for (String keyword : debitCardTransact) {
                            if (!messageBody.contains(keyword)) {
                                in = false;
                            }
                        }

                        if (in) {
                            String rs = messageBody.substring(messageBody.indexOf("rs.")).split(" ")[0].replace("rs.", " ").trim();
                            rs = rs.split("\\.")[0];
                            final Intent mainActivityIntent = new Intent(context, MainActivity.class);
                            mainActivityIntent.putExtra("rs", Integer.parseInt(rs));

                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.app_icon)
                                            .setContentTitle("Daily Expenses")
                                            .setContentText("Spent somewhere? Write it down!")
                                            .setContentIntent(pendingIntent);

                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(965778, mBuilder.build());
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

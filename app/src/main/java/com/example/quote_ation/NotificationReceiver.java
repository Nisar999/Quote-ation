package com.example.quote_ation;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import java.util.Calendar;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "daily_quote_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        new Thread(() -> {
            try {
                QuoteRepository repository = new QuoteRepository();
                QuoteRepository.Quote quote = repository.getRandomQuoteSync();
                if (quote != null) {
                    showNotification(context, quote.content, quote.author);
                    scheduleNextAlarm(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showNotification(Context context, String quote, String author) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.daily_quote_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(context.getString(R.string.daily_quote_channel_desc));
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Daily Inspiration")
                .setContentText("\"" + quote + "\" - " + author)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("\"" + quote + "\"\n\n- " + author))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void scheduleNextAlarm(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("quote_prefs", Context.MODE_PRIVATE);
        int hour = prefs.getInt("reminder_hour", -1);
        int minute = prefs.getInt("reminder_minute", -1);
        if (hour < 0 || minute < 0) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, NotificationReceiver.class);
        alarmIntent.setAction("com.example.quote_ation.DAILY_QUOTE_ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long nextTrigger = calendar.getTimeInMillis();
        if (nextTrigger <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            nextTrigger = calendar.getTimeInMillis();
        }

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTrigger, pendingIntent);
        }
    }
}

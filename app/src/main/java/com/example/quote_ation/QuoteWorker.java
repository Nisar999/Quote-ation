package com.example.quote_ation;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class QuoteWorker extends Worker {

    private static final String CHANNEL_ID = "daily_quote_channel";

    public QuoteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        QuoteRepository repository = new QuoteRepository();
        QuoteRepository.Quote quote = repository.getRandomQuoteSync();
        if (quote != null && quote.content != null) {
            showNotification(quote.content, quote.author);
            return Result.success();
        }
        return Result.retry();
    }

    private void showNotification(String quote, String author) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getApplicationContext().getString(R.string.daily_quote_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(getApplicationContext().getString(R.string.daily_quote_channel_desc));
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Daily Inspiration")
                .setContentText("\"" + quote + "\" - " + author)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("\"" + quote + "\"\n\n- " + author))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}

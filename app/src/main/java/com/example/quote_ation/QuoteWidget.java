package com.example.quote_ation;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuoteWidget extends AppWidgetProvider {

    private static final String API_URL = "https://api.quotable.io/random";
    private static final String ACTION_UPDATE_QUOTE = "com.example.quote_ation.UPDATE_QUOTE";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Intent to launch app on click
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widgetQuoteText, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        fetchNewQuoteForWidget(context, appWidgetManager, appWidgetId);
    }

    private static void fetchNewQuoteForWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(API_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);
                        String quote = json.getString("content");
                        String author = json.getString("author");

                        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                        views.setTextViewText(R.id.widgetQuoteText, "\"" + quote + "\"");
                        views.setTextViewText(R.id.widgetAuthorText, "- " + author);

                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

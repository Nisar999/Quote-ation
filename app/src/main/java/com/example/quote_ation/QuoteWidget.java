package com.example.quote_ation;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

public class QuoteWidget extends AppWidgetProvider {

    private static final String ACTION_REFRESH_QUOTE = "com.example.quote_ation.REFRESH_WIDGET_QUOTE";
    private static final String PREFS_NAME = "com.example.quote_ation.QuoteWidgetPrefs";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final String PREF_STYLE_KEY = "style_";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_REFRESH_QUOTE.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            removeWidgetPref(context, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent openAppIntent = new Intent(context, MainActivity.class);
        PendingIntent openPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widgetRoot, openPendingIntent);

        Intent refreshIntent = new Intent(context, QuoteWidget.class);
        refreshIntent.setAction(ACTION_REFRESH_QUOTE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widgetRefreshButton, refreshPendingIntent);

        views.setTextViewText(R.id.widgetQuoteText, context.getString(R.string.widget_loading));
        views.setTextViewText(R.id.widgetAuthorText, "");
        applyWidgetStyle(context, views, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, views);

        fetchNewQuoteForWidget(context, appWidgetManager, appWidgetId);
    }

    private static void fetchNewQuoteForWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String customText = loadQuotePref(context, appWidgetId);
        if (customText != null && !customText.trim().isEmpty()) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextViewText(R.id.widgetQuoteText, customText.trim());
            views.setTextViewText(R.id.widgetAuthorText, "");
            applyWidgetStyle(context, views, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            return;
        }

        QuoteRepository repository = new QuoteRepository();
        repository.getRandomQuote(new QuoteRepository.QuoteCallback() {
            @Override
            public void onQuote(@NonNull QuoteRepository.Quote quote) {
                RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                updateViews.setTextViewText(R.id.widgetQuoteText, "\"" + quote.content + "\"");
                updateViews.setTextViewText(R.id.widgetAuthorText, "- " + quote.author);
                applyWidgetStyle(context, updateViews, appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, updateViews);
            }

            @Override
            public void onError(@NonNull String message) {
                RemoteViews errorViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                errorViews.setTextViewText(R.id.widgetQuoteText, context.getString(R.string.widget_error));
                errorViews.setTextViewText(R.id.widgetAuthorText, "");
                applyWidgetStyle(context, errorViews, appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, errorViews);
            }
        });
    }

    private static void applyWidgetStyle(Context context, RemoteViews views, int appWidgetId) {
        String styleKey = loadStylePref(context, appWidgetId);
        int quoteColor = Color.WHITE;
        int authorColor = Color.parseColor("#B3E5FF");
        int backgroundRes;

        switch (styleKey) {
            case "style_aurora":
                backgroundRes = R.drawable.glass_style_aurora;
                quoteColor = Color.parseColor("#FFF8E1");
                authorColor = Color.parseColor("#B0EBFF");
                break;
            case "style_sapphire":
                backgroundRes = R.drawable.glass_style_sapphire;
                quoteColor = Color.parseColor("#E3F2FD");
                authorColor = Color.parseColor("#C5CAE9");
                break;
            default:
                backgroundRes = R.drawable.glass_style_moonlit;
                quoteColor = Color.WHITE;
                authorColor = Color.parseColor("#B3E5FF");
                break;
        }

        views.setInt(R.id.widgetRoot, "setBackgroundResource", backgroundRes);
        views.setTextColor(R.id.widgetQuoteText, quoteColor);
        views.setTextColor(R.id.widgetAuthorText, authorColor);
    }

    public static void saveQuotePref(Context context, int appWidgetId, String text) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(PREF_PREFIX_KEY + appWidgetId, text)
                .apply();
    }

    public static String loadQuotePref(Context context, int appWidgetId) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(PREF_PREFIX_KEY + appWidgetId, "");
    }

    public static void saveStylePref(Context context, int appWidgetId, String styleKey) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(PREF_STYLE_KEY + appWidgetId, styleKey)
                .apply();
    }

    public static String loadStylePref(Context context, int appWidgetId) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(PREF_STYLE_KEY + appWidgetId, "style_moonlit");
    }

    private static void removeWidgetPref(Context context, int appWidgetId) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(PREF_PREFIX_KEY + appWidgetId)
                .remove(PREF_STYLE_KEY + appWidgetId)
                .apply();
    }
}

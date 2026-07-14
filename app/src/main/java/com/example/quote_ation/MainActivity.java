package com.example.quote_ation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView quoteTextView, authorTextView;
    private Button generateButton;
    private ImageButton copyButton, shareButton;
    private ProgressBar progressBar;
    private QuoteRepository repository;

    // Reminder UI
    private TextView reminderText;
    private Button setReminderButton;
    private static final String PREFS = "quote_prefs";
    private static final String PREF_HOUR = "reminder_hour";
    private static final String PREF_MIN = "reminder_minute";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        quoteTextView = findViewById(R.id.quoteText);
        authorTextView = findViewById(R.id.authorText);
        generateButton = findViewById(R.id.fetchQuoteButton);
        progressBar = findViewById(R.id.progressBar);
        copyButton = findViewById(R.id.copyButton);
        shareButton = findViewById(R.id.shareButton);

        reminderText = findViewById(R.id.reminderText);
        setReminderButton = findViewById(R.id.setReminderButton);

        repository = new QuoteRepository();

        // Initial fetch
        fetchRandomQuote();

        generateButton.setOnClickListener(v -> fetchRandomQuote());

        copyButton.setOnClickListener(v -> copyToClipboard());
        shareButton.setOnClickListener(v -> shareQuote());

        setReminderButton.setOnClickListener(v -> showTimePicker());

        loadReminderUi();

        // Keep WorkManager periodic job as a fallback/backup
        setupDailyNotifications();

        // Request notification permission on Android 13+
        requestNotificationPermissionIfNeeded();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadReminderUi() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        int hour = prefs.getInt(PREF_HOUR, -1);
        int min = prefs.getInt(PREF_MIN, -1);
        if (hour >= 0 && min >= 0) {
            reminderText.setText(getString(R.string.reminder_set_for, formatTime(hour, min)));
        } else {
            reminderText.setText(R.string.reminder_not_set);
        }
    }

    private void showTimePicker() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        int hour = prefs.getInt(PREF_HOUR, 8);
        int min = prefs.getInt(PREF_MIN, 0);

        TimePickerDialog dialog = new TimePickerDialog(this, (TimePicker view, int hourOfDay, int minute) -> {
            saveReminder(hourOfDay, minute);
            scheduleAlarm(hourOfDay, minute);
            reminderText.setText(getString(R.string.reminder_set_for, formatTime(hourOfDay, minute)));
            Toast.makeText(this, R.string.daily_reminder_set, Toast.LENGTH_SHORT).show();
        }, hour, min, true);
        dialog.show();
    }

    private String formatTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    private void saveReminder(int hour, int minute) {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit().putInt(PREF_HOUR, hour).putInt(PREF_MIN, minute).apply();
    }

    private void scheduleAlarm(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.setAction("com.example.quote_ation.DAILY_QUOTE_ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long triggerAtMillis = calendar.getTimeInMillis();
        long interval = AlarmManager.INTERVAL_DAY;

        // If time is before now, schedule for next day
        if (triggerAtMillis <= System.currentTimeMillis()) {
            triggerAtMillis += interval;
        }

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }

    private void copyToClipboard() {
        String textToCopy = quoteTextView.getText().toString() + " " + authorTextView.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Quote", textToCopy);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.quote_copied, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareQuote() {
        String shareText = quoteTextView.getText().toString() + " " + authorTextView.getText().toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void setupDailyNotifications() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(
                QuoteWorker.class, 24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "DailyQuoteWork",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                notificationWork
        );
    }

    private void fetchRandomQuote() {
        setLoading(true);

        repository.getRandomQuote(new QuoteRepository.QuoteCallback() {
            @Override
            public void onQuote(@NonNull QuoteRepository.Quote quote) {
                runOnUiThread(() -> updateUI(quote.content, quote.author));
            }

            @Override
            public void onError(@NonNull String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateUI(String quote, String author) {
        setLoading(false);

        // UX: Smooth Fade-in Animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);

        quoteTextView.setText("\"" + quote + "\"");
        authorTextView.setText("- " + author);

        quoteTextView.startAnimation(fadeIn);
        authorTextView.startAnimation(fadeIn);
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            generateButton.setEnabled(false);
            generateButton.setAlpha(0.5f);
        } else {
            progressBar.setVisibility(View.GONE);
            generateButton.setEnabled(true);
            generateButton.setAlpha(1.0f);
        }
    }
}

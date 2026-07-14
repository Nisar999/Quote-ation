package com.example.quote_ation;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView quoteTextView, authorTextView;
    private Button generateButton;
    private ImageButton copyButton, shareButton;
    private ProgressBar progressBar;
    private OkHttpClient client;
    private static final String API_URL = "https://api.quotable.io/random";

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

        client = new OkHttpClient();

        // Initial fetch
        fetchRandomQuote();

        generateButton.setOnClickListener(v -> fetchRandomQuote());

        copyButton.setOnClickListener(v -> copyToClipboard());
        shareButton.setOnClickListener(v -> shareQuote());

        setupDailyNotifications();
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
        // UX: Show loading state
        setLoading(true);

        Request request = new Request.Builder().url(API_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);
                        final String quote = json.getString("content");
                        final String author = json.getString("author");

                        runOnUiThread(() -> updateUI(quote, author));
                    } catch (JSONException e) {
                        runOnUiThread(() -> setLoading(false));
                    }
                }
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
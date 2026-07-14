package com.example.quote_ation;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView quoteTextView, authorTextView;
    private Button generateButton;
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

        client = new OkHttpClient();

        // Initial fetch
        fetchRandomQuote();

        generateButton.setOnClickListener(v -> fetchRandomQuote());
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
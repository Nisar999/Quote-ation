package com.example.quote_ation;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuoteRepository {

    public static class Quote {
        public final String content;
        public final String author;

        public Quote(String content, String author) {
            this.content = content;
            this.author = author;
        }
    }

    public interface QuoteCallback {
        void onQuote(@NonNull Quote quote);
        void onError(@NonNull String message);
    }

    private static final String API_URL = "https://api.quotable.io/random";
    private static final List<Quote> CURATED_QUOTES = new ArrayList<>();

    static {
        // Vagabond
        CURATED_QUOTES.add(new Quote("Do not be distracted by the leaf in front of you. See the whole tree.", "Miyamoto Musashi (Vagabond)"));
        CURATED_QUOTES.add(new Quote("Preoccupation with a single leaf prevents you from seeing the tree. Preoccupation with a single tree prevents you from seeing the forest.", "Takuan Soho (Vagabond)"));
        CURATED_QUOTES.add(new Quote("The spiral of death and rebirth. The sword is a tool of death, yet it is also a tool of life.", "Miyamoto Musashi (Vagabond)"));

        // Vinland Saga
        CURATED_QUOTES.add(new Quote("You don't have any enemies. No one has any enemies. There's no one that it's okay to hurt.", "Thors (Vinland Saga)"));
        CURATED_QUOTES.add(new Quote("A true warrior doesn't need a sword.", "Thors (Vinland Saga)"));
        CURATED_QUOTES.add(new Quote("To be a warrior means to protect those you love.", "Thorfinn (Vinland Saga)"));

        // Mind-opening / Philosophical
        CURATED_QUOTES.add(new Quote("The only way to deal with an unfree world is to become so absolutely free that your very existence is an act of rebellion.", "Albert Camus"));
        CURATED_QUOTES.add(new Quote("He who has a why to live for can bear almost any how.", "Friedrich Nietzsche"));
        CURATED_QUOTES.add(new Quote("We are what we repeatedly do. Excellence, then, is not an act, but a habit.", "Aristotle"));
        CURATED_QUOTES.add(new Quote("The unexamined life is not worth living.", "Socrates"));
        CURATED_QUOTES.add(new Quote("Everything you've ever wanted is on the other side of fear.", "George Addair"));

        // Realistic / Simple
        CURATED_QUOTES.add(new Quote("Sleep. Wake. Repeat. But find the meaning in between.", "Anonymous"));
        CURATED_QUOTES.add(new Quote("Life is really simple, but we insist on making it complicated.", "Confucius"));
    }

    private final Random random = new Random();
    private final OkHttpClient client = new OkHttpClient();

    public Quote getRandomCuratedQuote() {
        return CURATED_QUOTES.get(random.nextInt(CURATED_QUOTES.size()));
    }

    public Quote getRandomQuoteSync() {
        if (random.nextFloat() < 0.6f) {
            return getRandomCuratedQuote();
        }

        Request request = new Request.Builder().url(API_URL).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                JSONObject json = new JSONObject(body);
                return new Quote(json.getString("content"), json.getString("author"));
            }
        } catch (Exception e) {
            Log.w("QuoteRepository", "Remote quote fetch failed, falling back to curated quote", e);
        }

        return getRandomCuratedQuote();
    }

    public void getRandomQuote(@NonNull QuoteCallback callback) {
        if (random.nextFloat() < 0.6f) {
            callback.onQuote(getRandomCuratedQuote());
            return;
        }

        Request request = new Request.Builder().url(API_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onQuote(getRandomCuratedQuote());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String body = response.body().string();
                        JSONObject json = new JSONObject(body);
                        callback.onQuote(new Quote(json.getString("content"), json.getString("author")));
                    } catch (Exception e) {
                        callback.onQuote(getRandomCuratedQuote());
                    }
                } else {
                    callback.onQuote(getRandomCuratedQuote());
                }
            }
        });
    }
}

package com.example.quote_ation;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class QuoteWidgetConfigureActivity extends AppCompatActivity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.widget_configure);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        EditText customQuoteEdit = findViewById(R.id.customQuoteEdit);
        RadioGroup styleGroup = findViewById(R.id.styleGroup);
        Button addWidgetButton = findViewById(R.id.addWidgetButton);

        customQuoteEdit.setText(QuoteWidget.loadQuotePref(this, mAppWidgetId));
        setStyleSelection(styleGroup, QuoteWidget.loadStylePref(this, mAppWidgetId));

        addWidgetButton.setOnClickListener(v -> {
            final Context context = QuoteWidgetConfigureActivity.this;

            String customQuote = customQuoteEdit.getText().toString().trim();
            String selectedStyle = getSelectedStyle(styleGroup);
            QuoteWidget.saveQuotePref(context, mAppWidgetId, customQuote);
            QuoteWidget.saveStylePref(context, mAppWidgetId, selectedStyle);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            QuoteWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        });
    }

    private void setStyleSelection(RadioGroup styleGroup, String styleKey) {
        int selectedId = R.id.styleMoonlit;
        if ("style_aurora".equals(styleKey)) {
            selectedId = R.id.styleAurora;
        } else if ("style_sapphire".equals(styleKey)) {
            selectedId = R.id.styleSapphire;
        }
        styleGroup.check(selectedId);
    }

    private String getSelectedStyle(RadioGroup styleGroup) {
        int selectedId = styleGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.styleAurora) {
            return "style_aurora";
        }
        if (selectedId == R.id.styleSapphire) {
            return "style_sapphire";
        }
        return "style_moonlit";
    }
}

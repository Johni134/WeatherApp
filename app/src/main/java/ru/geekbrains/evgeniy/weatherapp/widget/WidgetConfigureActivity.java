package ru.geekbrains.evgeniy.weatherapp.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.data.WorkWithSharedPreferences;

public class WidgetConfigureActivity extends Activity implements View.OnClickListener {

    public static final String PREFS_CITIES_NUM = "CITIES_NUM";

    private Spinner spinner;
    private Button buttonAdd;

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init shared pref
        WorkWithSharedPreferences.initSharedPreferences(getApplicationContext());

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.widget_configure);

        // Set layout size of activity
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        initViews();

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

    }

    private void initViews() {
        spinner = findViewById(R.id.spinnerCitiesNum);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAdd:
                createWidget();
                break;
            default:
                break;
        }
    }

    private void createWidget() {

        int cities_num = 1;

        switch (spinner.getSelectedItemPosition()) {
            case 0:
                cities_num = 1;
                break;
            case 1:
                cities_num = 5;
                break;
            case 2:
                cities_num = 10;
                break;
            case 3:
                cities_num = 100000;
                break;
            default:
                cities_num = 1;

        }
        // save property
        WorkWithSharedPreferences.saveProperty(PREFS_CITIES_NUM, cities_num);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}

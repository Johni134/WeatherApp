package ru.geekbrains.evgeniy.weatherapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import ru.geekbrains.evgeniy.weatherapp.R;
import ru.geekbrains.evgeniy.weatherapp.services.CitiesWidgetService;

public class WidgetCities extends AppWidgetProvider {
    public static final String UPDATE_WIDGET_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String ITEM_ON_CLICK_ACTION = "android.appwidget.action.ITEM_ON_CLICK";
    public static final String CITY_DESCRIPTION = "city_description";

    public WidgetCities() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equalsIgnoreCase(UPDATE_WIDGET_ACTION)) {
            int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context, WidgetCities.class));
            mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view);
        }
        if (intent.getAction().equalsIgnoreCase(ITEM_ON_CLICK_ACTION)) {
            String itemText = intent.getStringExtra(CITY_DESCRIPTION);
            if (!itemText.equalsIgnoreCase("")) {
                Toast.makeText(context, itemText, Toast.LENGTH_SHORT).show();
            }
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }
    }

    void updateWidget(Context context, AppWidgetManager appWidgetManager,
                      int appWidgetId) {
        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.widget_list);
        setList(rv, context, appWidgetId);
        setListClick(rv, context);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    void setList(RemoteViews rv, Context context, int appWidgetId) {
        Intent adapter = new Intent(context, CitiesWidgetService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        rv.setRemoteAdapter(R.id.list_view, adapter);
        rv.setEmptyView(R.id.list_view, R.id.empty_view);
    }

    void setListClick(RemoteViews rv, Context context) {
        Intent listClickIntent = new Intent(context, WidgetCities.class);
        listClickIntent.setAction(ITEM_ON_CLICK_ACTION);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0,
                listClickIntent, 0);
        rv.setPendingIntentTemplate(R.id.list_view, listClickPIntent);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }
}

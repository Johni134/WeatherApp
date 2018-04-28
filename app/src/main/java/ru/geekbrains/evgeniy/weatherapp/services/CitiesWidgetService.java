package ru.geekbrains.evgeniy.weatherapp.services;

import android.content.Intent;
import android.widget.RemoteViewsService;

import ru.geekbrains.evgeniy.weatherapp.widget.WidgetCitiesFactory;

public class CitiesWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new WidgetCitiesFactory(this.getApplicationContext(), intent);

    }
}

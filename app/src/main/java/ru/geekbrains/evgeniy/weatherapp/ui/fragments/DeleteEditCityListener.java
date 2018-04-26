package ru.geekbrains.evgeniy.weatherapp.ui.fragments;

import ru.geekbrains.evgeniy.weatherapp.model.CityModel;

public interface DeleteEditCityListener {

    void onDeleteCity(CityModel cityModel);
    void onEditCity(Long id, String name);
    void setFavorite(CityModel cityModel);
}

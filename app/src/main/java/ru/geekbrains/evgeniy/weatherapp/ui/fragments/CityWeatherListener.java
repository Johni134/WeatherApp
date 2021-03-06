package ru.geekbrains.evgeniy.weatherapp.ui.fragments;


import ru.geekbrains.evgeniy.weatherapp.model.CityModel;

public interface CityWeatherListener {
    void showCityWeather(CityModel cityModel);
    void showForecast(CityModel cityModel);
    void showAirPollution(CityModel cityModel);
    void setFavoriteCityFromRealm();
}

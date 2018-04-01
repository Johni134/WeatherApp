package ru.geekbrains.evgeniy.weatherapp.model;

public class CityModel {
    private String name;
    private int temp;

    public CityModel(String name, int temp) {
        this.name = name;
        this.temp = temp;
    }

    public CityModel(String name) {
        this.name = name;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

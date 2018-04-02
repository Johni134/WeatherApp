package ru.geekbrains.evgeniy.weatherapp.model;

public class CityModel {
    private String name;
    private Integer temp;

    public CityModel(String name, Integer temp) {
        this.name = name;
        this.temp = temp;
    }

    public CityModel(String name) {
        this.name = name;
        temp = (int)(Math.random()*100 - 50);
    }

    public Integer getTemp() {
        return temp;
    }

    public String getTempC() {
        return temp.toString() + "\u00B0";
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

package com.example.weatherapplication;

public class WeatherModel {
    String city, country;
    String date_info;
    int img;
    int temperature;

    public WeatherModel(String city, String country, String date_info,  int temperature, int img) {
        this.city = city;
        this.country = country;
        this.date_info = date_info;
        this.temperature = temperature;
        this.img = img;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getDate_info() {
        return date_info;
    }

    public int getImg() {
        return img;
    }

    public int getTemperature() {
        return temperature;
    }
}

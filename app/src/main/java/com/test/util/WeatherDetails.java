package com.test.util;

/**
 * Created by mohit.kansal on 7/25/2016.
 */
public class WeatherDetails {

    String forecastTime = "", forecastDetails = "";

    public String getForecastTime() {
        return forecastTime;
    }

    public void setForecastTime(String forecastTime) {
        this.forecastTime = forecastTime;
    }

    public String getForecastDetails() {
        return forecastDetails;
    }

    public void setForecastDetails(String forecastDetails) {
        this.forecastDetails = forecastDetails;
    }

    @Override
    public String toString() {
        return "WeatherDetails{" +
                "forecastTime='" + forecastTime + '\'' +
                ", forecastDetails='" + forecastDetails + '\'' +
                '}';
    }

    public WeatherDetails(String forecastTime, String forecastDetails) {
        this.forecastTime = forecastTime;
        this.forecastDetails = forecastDetails;
    }
}

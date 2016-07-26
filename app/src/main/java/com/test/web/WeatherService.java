package com.test.web;

import com.test.util.AppConstants;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mohit.kansal on 7/25/2016.
 */
public interface WeatherService {

    //call for fetching weather details of that city
    @GET(AppConstants.METHOD_NAME)
    Call<Object> getWeatherDetails(@Query(AppConstants.STR_WEATHER_FORECAST_QUERY) String owner,
                                   @Query(AppConstants.STR_WEATHER_FORECAST_API_KEY) String repo);


    public static final Retrofit retroService = new Retrofit.Builder()
            .baseUrl(AppConstants.WEATHER_FORECAST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}



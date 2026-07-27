package com.salekh.weather.data.remote;

import com.salekh.weather.data.model.remote.GeocodingResponse;
import com.salekh.weather.data.model.remote.WeatherResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {

  @GET
  Single<GeocodingResponse> searchCity(
      @Url String url,
      @Query("name") String cityName,
      @Query("count") int count,
      @Query("language") String lang,
      @Query("format") String format
  );

  @GET
  Single<WeatherResponse> getForecast(
      @Url String url,
      @Query("latitude") double lat,
      @Query("longitude") double lon,
      @Query("current") String currentVars,
      @Query("daily") String dailyVars,
      @Query("timezone") String timezone,
      @Query("forecast_days") int days
  );
}

package com.salekh.weather.data.remote;

import com.salekh.weather.data.model.currentweather.CurrentWeatherResponse;
import com.salekh.weather.data.model.daysweather.MultipleDaysWeatherResponse;
import com.salekh.weather.data.model.fivedayweather.FiveDayResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

  /**
   * Get current weather of city
   *
   * @param q     String name of city
   * @param units String units of response
   * @param lang  String language of response
   * @param appId String api key
   * @return instance of {@link CurrentWeatherResponse}
   */
  @GET("weather")
  Single<CurrentWeatherResponse> getCurrentWeather(
      @Query("q") String q,
      @Query("units") String units,
      @Query("lang") String lang,
      @Query("appid") String appId
  );

  /**
   * Get five days weather forecast.
   *
   * @param q     String name of city
   * @param units String units of response
   * @param lang  String language of response
   * @param appId String api key
   * @return instance of {@link FiveDayResponse}
   */
  @GET("forecast")
  Single<FiveDayResponse> getFiveDaysWeather(
      @Query("q") String q,
      @Query("units") String units,
      @Query("lang") String lang,
      @Query("appid") String appId
  );

  /**
   * Get multiple days weather
   *
   * @param q     String name of city
   * @param units String units of response
   * @param lang  String language of response
   * @param appId String api key
   * @return instance of {@link MultipleDaysWeatherResponse}
   */
  @GET("forecast/daily")
  Single<MultipleDaysWeatherResponse> getMultipleDaysWeather(
      @Query("q") String q,
      @Query("units") String units,
      @Query("lang") String lang,
      @Query("cnt") int dayCount,
      @Query("appid") String appId
  );
}

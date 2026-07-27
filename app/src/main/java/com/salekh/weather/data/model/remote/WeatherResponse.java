package com.salekh.weather.data.model.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    private double latitude;
    private double longitude;
    private CurrentData current;
    private DailyForecast daily;

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public CurrentData getCurrent() { return current; }
    public DailyForecast getDaily() { return daily; }

    public static class CurrentData {
        @SerializedName("temperature_2m")
        private double temperature;
        @SerializedName("relative_humidity_2m")
        private int humidity;
        @SerializedName("apparent_temperature")
        private double apparentTemperature;
        @SerializedName("weather_code")
        private int weatherCode;
        @SerializedName("pressure_msl")
        private double pressure;
        @SerializedName("wind_speed_10m")
        private double windSpeed;

        public double getTemperature() { return temperature; }
        public int getHumidity() { return humidity; }
        public double getApparentTemperature() { return apparentTemperature; }
        public int getWeatherCode() { return weatherCode; }
        public double getPressure() { return pressure; }
        public double getWindSpeed() { return windSpeed; }
    }

    public static class DailyForecast {
        private List<String> time;
        @SerializedName("weather_code")
        private List<Integer> weatherCode;
        @SerializedName("temperature_2m_max")
        private List<Double> tempMax;
        @SerializedName("temperature_2m_min")
        private List<Double> tempMin;
        private List<String> sunrise;
        private List<String> sunset;
        @SerializedName("uv_index_max")
        private List<Double> uvIndex;

        public List<String> getTime() { return time; }
        public List<Integer> getWeatherCode() { return weatherCode; }
        public List<Double> getTempMax() { return tempMax; }
        public List<Double> getTempMin() { return tempMin; }
        public List<String> getSunrise() { return sunrise; }
        public List<String> getSunset() { return sunset; }
        public List<Double> getUvIndex() { return uvIndex; }
    }
}

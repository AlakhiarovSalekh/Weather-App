package com.salekh.weather.utils;

import com.salekh.weather.data.model.common.Coord;
import com.salekh.weather.data.model.common.WeatherItem;
import com.salekh.weather.data.model.common.Wind;
import com.salekh.weather.data.model.currentweather.CurrentWeatherResponse;
import com.salekh.weather.data.model.currentweather.Main;
import com.salekh.weather.data.model.currentweather.Sys;
import com.salekh.weather.data.model.daysweather.City;
import com.salekh.weather.data.model.daysweather.ListItem;
import com.salekh.weather.data.model.daysweather.MultipleDaysWeatherResponse;
import com.salekh.weather.data.model.daysweather.Temp;
import com.salekh.weather.data.model.fivedayweather.FiveDayResponse;
import com.salekh.weather.data.model.fivedayweather.ItemHourly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MockUtil {

    private static final Random random = new Random();

    public static CurrentWeatherResponse getMockCurrentWeather(String cityName) {
        CurrentWeatherResponse response = new CurrentWeatherResponse();
        response.setName(cityName);
        response.setId(123456);
        response.setDt((int) (System.currentTimeMillis() / 1000));

        Main main = new Main();
        main.setTemp(20.0 + random.nextInt(15));
        main.setHumidity(40 + random.nextInt(50));
        main.setPressure(1000 + random.nextInt(30));
        // Note: OpenWeatherMap standard visibility is in meters
        // We'll use this field to mock professional metrics if needed
        response.setMain(main);

        WeatherItem weatherItem = new WeatherItem();
        int[] ids = {800, 801, 500, 600, 200}; // Clear, Clouds, Rain, Snow, Storm
        int weatherId = ids[random.nextInt(ids.length)];
        weatherItem.setId(weatherId);
        weatherItem.setMain(getWeatherMain(weatherId));
        weatherItem.setDescription(getWeatherDesc(weatherId));
        weatherItem.setIcon("01d");
        response.setWeather(Collections.singletonList(weatherItem));

        Wind wind = new Wind();
        wind.setSpeed(2.0 + random.nextDouble() * 10);
        wind.setDeg(random.nextInt(360));
        response.setWind(wind);

        Sys sys = new Sys();
        sys.setCountry("MC");
        response.setSys(sys);

        Coord coord = new Coord();
        coord.setLat(40.0 + random.nextDouble());
        coord.setLon(40.0 + random.nextDouble());
        response.setCoord(coord);

        return response;
    }

    private static String getWeatherMain(int id) {
        if (id == 800) return "Clear";
        if (id == 801) return "Clouds";
        if (id == 500) return "Rain";
        if (id == 600) return "Snow";
        return "Thunderstorm";
    }

    private static String getWeatherDesc(int id) {
        if (id == 800) return "clear sky";
        if (id == 801) return "few clouds";
        if (id == 500) return "light rain";
        if (id == 600) return "snow";
        return "thunderstorm with rain";
    }

    public static MultipleDaysWeatherResponse getMockMultipleDaysWeather(String cityName, int count) {
        MultipleDaysWeatherResponse response = new MultipleDaysWeatherResponse();
        City city = new City();
        city.setName(cityName);
        city.setCountry("MC");
        response.setCity(city);
        response.setCnt(count);

        List<ListItem> list = new ArrayList<>();
        long now = System.currentTimeMillis() / 1000;
        for (int i = 0; i < count; i++) {
            ListItem item = new ListItem();
            item.setDt((int) (now + i * 86400));
            item.setHumidity(50 + i);
            item.setSpeed(5.0 + i);

            Temp temp = new Temp();
            temp.setDay(18.0 + random.nextInt(10));
            temp.setMin(12.0 + random.nextInt(5));
            temp.setMax(25.0 + random.nextInt(5));
            item.setTemp(temp);

            WeatherItem weatherItem = new WeatherItem();
            weatherItem.setId(800 + (i % 2));
            weatherItem.setMain("Clouds");
            weatherItem.setDescription("scattered clouds");
            item.setWeather(Collections.singletonList(weatherItem));

            list.add(item);
        }
        response.setList(list);
        return response;
    }

    public static FiveDayResponse getMockFiveDaysHourlyWeather(String cityName) {
        FiveDayResponse response = new FiveDayResponse();
        com.salekh.weather.data.model.fivedayweather.City city = new com.salekh.weather.data.model.fivedayweather.City();
        city.setName(cityName);
        response.setCity(city);

        List<ItemHourly> list = new ArrayList<>();
        long now = (System.currentTimeMillis() / 1000) / 3600 * 3600;
        for (int i = 0; i < 40; i++) {
            ItemHourly item = new ItemHourly();
            item.setDt((int) (now + i * 10800));

            com.salekh.weather.data.model.fivedayweather.Main main = new com.salekh.weather.data.model.fivedayweather.Main();
            main.setTemp(15.0 + random.nextInt(15));
            main.setHumidity(60);
            item.setMain(main);

            WeatherItem weatherItem = new WeatherItem();
            weatherItem.setId(800);
            weatherItem.setMain("Clear");
            item.setWeather(Collections.singletonList(weatherItem));

            list.add(item);
        }
        response.setList(list);
        return response;
    }
}

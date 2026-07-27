package com.salekh.weather.presentation.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.salekh.weather.data.model.remote.GeocodingResponse;
import com.salekh.weather.data.model.remote.WeatherResponse;
import com.salekh.weather.data.remote.ApiClient;
import com.salekh.weather.data.remote.ApiService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {

    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final MutableLiveData<WeatherResponse> _weatherData = new MutableLiveData<>();
    public LiveData<WeatherResponse> weatherData = _weatherData;

    private final MutableLiveData<String> _cityName = new MutableLiveData<>();
    public LiveData<String> cityName = _cityName;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public void searchCity(String cityName) {
        _isLoading.setValue(true);
        disposable.add(
            apiService.searchCity("https://geocoding-api.open-meteo.com/v1/search", cityName, 1, "en", "json")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(geocodingResponse -> {
                    if (geocodingResponse.getResults() != null && !geocodingResponse.getResults().isEmpty()) {
                        GeocodingResponse.CityResult city = geocodingResponse.getResults().get(0);
                        _cityName.setValue(city.getName() + ", " + city.getCountry());
                        fetchWeather(city.getLatitude(), city.getLongitude());
                    } else {
                        _error.setValue("City not found");
                        _isLoading.setValue(false);
                    }
                }, throwable -> {
                    _error.setValue("Search failed: " + throwable.getMessage());
                    _isLoading.setValue(false);
                })
        );
    }

    private void fetchWeather(double lat, double lon) {
        String currentVars = "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,pressure_msl,wind_speed_10m";
        String dailyVars = "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max";
        
        disposable.add(
            apiService.getForecast("https://api.open-meteo.com/v1/forecast", lat, lon, currentVars, dailyVars, "auto", 7)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherResponse -> {
                    _weatherData.setValue(weatherResponse);
                    _error.setValue(null);
                    _isLoading.setValue(false);
                }, throwable -> {
                    _error.setValue("Failed to get weather: " + throwable.getMessage());
                    _isLoading.setValue(false);
                })
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}

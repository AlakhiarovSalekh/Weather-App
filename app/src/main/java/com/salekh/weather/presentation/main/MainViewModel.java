package com.salekh.weather.presentation.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.salekh.weather.data.model.currentweather.CurrentWeatherResponse;
import com.salekh.weather.utils.MockUtil;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<CurrentWeatherResponse> _weatherData = new MutableLiveData<>();
    public LiveData<CurrentWeatherResponse> weatherData = _weatherData;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public void searchCity(String cityName) {
        _isLoading.setValue(true);
        // Professional simulation delay
        new android.os.Handler().postDelayed(() -> {
            try {
                CurrentWeatherResponse mockData = MockUtil.getMockCurrentWeather(cityName);
                _weatherData.setValue(mockData);
                _error.setValue(null);
            } catch (Exception e) {
                _error.setValue("City not found or network error");
            } finally {
                _isLoading.setValue(false);
            }
        }, 1000);
    }
}

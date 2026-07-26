package com.salekh.weather.presentation.main;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.salekh.weather.R;
import com.salekh.weather.data.model.CityInfo;
import com.salekh.weather.data.model.currentweather.CurrentWeatherResponse;
import com.salekh.weather.data.model.db.CurrentWeather;
import com.salekh.weather.data.model.db.FiveDayWeather;
import com.salekh.weather.data.model.db.ItemHourlyDB;
import com.salekh.weather.data.model.fivedayweather.FiveDayResponse;
import com.salekh.weather.databinding.ActivityMainBinding;
import com.salekh.weather.presentation.about.AboutFragment;
import com.salekh.weather.presentation.base.BaseActivity;
import com.salekh.weather.presentation.forecast.MultipleDaysFragment;
import com.salekh.weather.utils.AppUtil;
import com.salekh.weather.utils.Constants;
import com.salekh.weather.utils.DbUtil;
import com.salekh.weather.utils.MyApplication;
import com.github.pwittchen.prefser.library.rx2.Prefser;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.List;
import java.util.Locale;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.query.Query;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscriptionList;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private ItemAdapter<FiveDayWeather> mItemAdapter;
    private FastAdapter<FiveDayWeather> mFastAdapter;
    private CompositeDisposable disposable = new CompositeDisposable();
    private DataSubscriptionList subscriptions = new DataSubscriptionList();
    private Prefser prefser;
    private Box<CurrentWeather> currentWeatherBox;
    private Box<FiveDayWeather> fiveDayWeatherBox;
    private int PERMISSION_REQUEST_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        prefser = new Prefser(this);
        
        initDatabase();
        initViews();
        setupObservers();
        initRecyclerView();
        
        showStoredData();
        checkLastUpdate();
        
        if (Build.VERSION.SDK_INT > 32) {
            getNotificationPermission();
        }
    }

    private void initDatabase() {
        BoxStore boxStore = MyApplication.getBoxStore();
        currentWeatherBox = boxStore.boxFor(CurrentWeather.class);
        fiveDayWeatherBox = boxStore.boxFor(FiveDayWeather.class);
    }

    private void initViews() {
        // M3 Search Setup
        binding.searchView.setupWithSearchBar(binding.searchBar);

        // SearchBar Menu -> About & Search
        binding.searchBar.setNavigationOnClickListener(v -> AppUtil.showFragment(new AboutFragment(), getSupportFragmentManager(), true));
        binding.searchBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                binding.searchView.show();
                return true;
            }
            return false;
        });

        // Search Logic inside SearchView
        binding.searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            String query = binding.searchView.getText().toString();
            if (!query.isEmpty()) {
                binding.searchBar.setText(query);
                binding.searchView.hide();
                viewModel.searchCity(query);
            }
            return true;
        });

        binding.nextDaysButton.setOnClickListener(v -> {
            AppUtil.showFragment(new MultipleDaysFragment(), getSupportFragmentManager(), true);
        });

        binding.swipeContainer.setOnRefreshListener(() -> {
            CityInfo cityInfo = prefser.get(Constants.CITY_INFO, CityInfo.class, null);
            if (cityInfo != null) {
                viewModel.searchCity(cityInfo.getName());
            } else {
                binding.swipeContainer.setRefreshing(false);
            }
        });

        binding.nextDaysButton.setOnClickListener(v -> {
            AppUtil.showFragment(new MultipleDaysFragment(), getSupportFragmentManager(), true);
        });
    }

    private void setupObservers() {
        viewModel.weatherData.observe(this, response -> {
            if (response != null) {
                storeCurrentWeather(response);
                storeCityInfo(response);
                updateUI(response);
            }
        });

        viewModel.isLoading.observe(this, isLoading -> {
            binding.swipeContainer.setRefreshing(isLoading);
        });

        viewModel.error.observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initRecyclerView() {
        mItemAdapter = new ItemAdapter<>();
        mFastAdapter = FastAdapter.with(mItemAdapter);
        binding.contentMainLayout.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.contentMainLayout.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.contentMainLayout.recyclerView.setAdapter(mFastAdapter);
        
        mFastAdapter.withOnClickListener((v, adapter, item, position) -> {
            Intent intent = new Intent(MainActivity.this, HourlyActivity.class);
            intent.putExtra(Constants.FIVE_DAY_WEATHER_ITEM, item);
            startActivity(intent);
            return true;
        });
    }

    private void showStoredData() {
        Query<CurrentWeather> query = DbUtil.getCurrentWeatherQuery(currentWeatherBox);
        query.subscribe(subscriptions).on(AndroidScheduler.mainThread())
                .onError(error -> {
                    Toast.makeText(this, "DB Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                })
                .observer(data -> {
                    if (data.size() > 0) {
                        CurrentWeather currentWeather = data.get(0);
                        binding.contentMainLayout.tempTextView.setCurrentText(String.format(Locale.getDefault(), "%.0f°", currentWeather.getTemp()));
                        binding.contentMainLayout.descriptionTextView.setCurrentText(AppUtil.getWeatherStatus(MainActivity.this, currentWeather.getWeatherId()));
                        binding.contentMainLayout.humidityTextView.setCurrentText(String.format(Locale.getDefault(), "%d%%", currentWeather.getHumidity()));
                        binding.contentMainLayout.windTextView.setCurrentText(String.format(Locale.getDefault(), getString(R.string.wind_unit_label), currentWeather.getWindSpeed()));
                        binding.contentMainLayout.pressureTextView.setText(String.format(Locale.getDefault(), "%d hPa", 1012)); // Mocked
                        binding.contentMainLayout.visibilityTextView.setText("10 km");
                        
                        updateBackground(currentWeather.getWeatherId());
                        binding.contentMainLayout.animationView.setAnimation(AppUtil.getWeatherAnimation(currentWeather.getWeatherId()));
                        binding.contentMainLayout.animationView.playAnimation();
                    }
                });

        Query<FiveDayWeather> forecastQuery = DbUtil.getFiveDayWeatherQuery(fiveDayWeatherBox);
        forecastQuery.subscribe(subscriptions).on(AndroidScheduler.mainThread())
                .onError(error -> {
                    Toast.makeText(this, "Forecast DB Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                })
                .observer(data -> {
                    if (data.size() > 0) {
                        data.remove(0); // remove today
                        mItemAdapter.clear();
                        mItemAdapter.add(data);
                    }
                });
    }

    private void updateUI(CurrentWeatherResponse response) {
        binding.contentMainLayout.cityNameDisplay.setText(String.format("%s, %s", response.getName(), response.getSys().getCountry()));
        binding.contentMainLayout.tempTextView.setText(String.format(Locale.getDefault(), "%.0f°", response.getMain().getTemp()));
        binding.contentMainLayout.descriptionTextView.setText(AppUtil.getWeatherStatus(this, response.getWeather().get(0).getId()));
        binding.contentMainLayout.humidityTextView.setText(String.format(Locale.getDefault(), "%d%%", response.getMain().getHumidity()));
        binding.contentMainLayout.windTextView.setText(String.format(Locale.getDefault(), getString(R.string.wind_unit_label), response.getWind().getSpeed()));
        binding.contentMainLayout.pressureTextView.setText(String.format(Locale.getDefault(), "%.0f hPa", response.getMain().getPressure()));
        
        updateBackground(response.getWeather().get(0).getId());
        binding.contentMainLayout.animationView.setAnimation(AppUtil.getWeatherAnimation(response.getWeather().get(0).getId()));
        binding.contentMainLayout.animationView.playAnimation();
        
        animateEntrance();
    }

    private void updateBackground(int weatherId) {
        int bgDrawable;
        if (weatherId / 100 == 2) bgDrawable = R.drawable.weather_bg_storm;
        else if (weatherId / 100 == 3 || weatherId / 100 == 5) bgDrawable = R.drawable.weather_bg_rain;
        else if (weatherId / 100 == 8 && weatherId != 800) bgDrawable = R.drawable.weather_bg_cloudy;
        else bgDrawable = R.drawable.weather_bg_clear;
        binding.backgroundView.setBackgroundResource(bgDrawable);
    }

    private void animateEntrance() {
        binding.contentMainLayout.heroLayout.setAlpha(0f);
        binding.contentMainLayout.heroLayout.setTranslationY(-50f);
        binding.contentMainLayout.detailsGrid.setAlpha(0f);
        binding.contentMainLayout.detailsGrid.setTranslationY(100f);
        
        binding.contentMainLayout.heroLayout.animate().alpha(1f).translationY(0f).setDuration(800).start();
        binding.contentMainLayout.detailsGrid.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(200).start();
    }

    private void storeCurrentWeather(CurrentWeatherResponse response) {
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTemp(response.getMain().getTemp());
        currentWeather.setHumidity(response.getMain().getHumidity());
        currentWeather.setDescription(response.getWeather().get(0).getDescription());
        currentWeather.setWeatherId(response.getWeather().get(0).getId());
        currentWeather.setWindSpeed(response.getWind().getSpeed());
        currentWeatherBox.removeAll();
        currentWeatherBox.put(currentWeather);
        prefser.put(Constants.LAST_STORED_CURRENT, System.currentTimeMillis());
    }

    private void storeCityInfo(CurrentWeatherResponse response) {
        CityInfo info = new CityInfo();
        info.setCountry(response.getSys().getCountry());
        info.setName(response.getName());
        prefser.put(Constants.CITY_INFO, info);
    }

    private void checkLastUpdate() {
        CityInfo info = prefser.get(Constants.CITY_INFO, CityInfo.class, null);
        if (info != null) {
            binding.searchBar.setText(String.format("%s, %s", info.getName(), info.getCountry()));
            viewModel.searchCity(info.getName());
        }
    }

    private void getNotificationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        subscriptions.cancel();
    }
}

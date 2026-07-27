package com.salekh.weather.data.model.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeocodingResponse {
    @SerializedName("results")
    private List<CityResult> results;

    public List<CityResult> getResults() { return results; }

    public static class CityResult {
        private String name;
        private double latitude;
        private double longitude;
        private String country;
        @SerializedName("country_code")
        private String countryCode;

        public String getName() { return name; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getCountry() { return country; }
        public String getCountryCode() { return countryCode; }
    }
}

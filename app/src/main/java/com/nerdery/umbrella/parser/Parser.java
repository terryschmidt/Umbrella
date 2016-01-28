package com.nerdery.umbrella.parser;

import com.nerdery.umbrella.model.CurrentObservation;
import com.nerdery.umbrella.model.ForecastCondition;
import com.nerdery.umbrella.model.DisplayLocation;
import com.nerdery.umbrella.model.HourlyWeatherData;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Converts Weather Underground's hourly forecast data to Java objects
 *
 * @author bherbst
 */
public class Parser {

    public static void parseCurrentObs(JSONObject root) throws JSONException {
        JSONObject current_observation = root.getJSONObject("current_observation");
        JSONObject display_location = current_observation.getJSONObject("display_location");
        DisplayLocation.full = display_location.getString("full");
        DisplayLocation.city = display_location.getString("city");
        DisplayLocation.state = display_location.getString("state");
        DisplayLocation.state_name = display_location.getString("state_name");
        DisplayLocation.country = display_location.getString("country");
        DisplayLocation.zip = display_location.getString("zip");
        CurrentObservation.tempFahrenheit = current_observation.getDouble("temp_f");
        CurrentObservation.tempCelsius = current_observation.getDouble("temp_c");
        CurrentObservation.weather = current_observation.getString("weather");
        CurrentObservation.icon = current_observation.getString("icon");
    }

    public static void parseHourlyData(JSONObject root) throws JSONException {
        JSONArray hourly_forecast = root.getJSONArray("hourly_forecast");
        int hourlyForecastLength = hourly_forecast.length();

        for (int i = 0; i < hourlyForecastLength; i++) {
            JSONObject json = hourly_forecast.getJSONObject(i);
            ForecastCondition cond = new ForecastCondition();
            cond.icon = json.getString("icon");
            cond.condition = json.getString("condition");
            JSONObject tempData = json.getJSONObject("temp");
            cond.tempFahrenheit = tempData.getString("english");
            cond.tempCelsius = tempData.getString("metric");
            JSONObject fcttime = json.getJSONObject("FCTTIME");
            cond.civil = fcttime.getString("civil");
            cond.hour = fcttime.getString("hour");
            cond.weekday = fcttime.getString("weekday_name");
            HourlyWeatherData.data.add(cond);
        }
    }
}

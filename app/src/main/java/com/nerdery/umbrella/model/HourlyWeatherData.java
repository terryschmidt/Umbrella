package com.nerdery.umbrella.model;

import java.util.ArrayList;

/**
 * Created by terryschmidt on 1/9/16.
 */
public class HourlyWeatherData {
    public static ArrayList<ForecastCondition> data = new ArrayList<ForecastCondition>();

    public static ArrayList<ForecastCondition> getData () {
        return data;
    }
}

package com.nerdery.umbrella.model;

/**
 * Represents a forecast weather condition returned from Weather Underground
 *
 * Does not include all available only data- only potentially useful fields are included
 *
 * @author bherbst
 */
public class ForecastCondition {
    public String hour = "";
    public String civil = "";
    public String icon = "";
    public String condition = "";
    public String tempFahrenheit = "";
    public String tempCelsius = "";
    public String weekday;
}

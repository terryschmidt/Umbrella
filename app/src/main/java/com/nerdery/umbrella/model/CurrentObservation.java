package com.nerdery.umbrella.model;

/**
 * Represents the "current_observation" data returned from Weather Underground
 *
 * Does not include all available only data- only potentially useful fields are included
 *
 * @author bherbst
 */
public class CurrentObservation {
    public static double tempFahrenheit;
    public static double tempCelsius;
    public static String weather;
    public static String icon;
}

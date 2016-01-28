package com.nerdery.umbrella.api;

/**
 * API for getting custom Nerdery icon URLs for weather conditions
 *
 * This really isn't so much an API as a utility, but we will treat it as an API
 *
 * @author bherbst
 */
public class IconApi {

    // Only allow fellow package members to create IconApi instances
    public IconApi() {}

    /**
     * Get the URL to an icon suitable for use as a replacement for the icons given by Weather Underground
     * @param icon The name of the icon provided by Weather Underground (e.g. "clear").
     *      {@see {@link com.nerdery.umbrella.model.CurrentObservation#icon}}
     * @param highlighted True to get the highlighted version, false to get the outline version
     * @return A URL to an icon
     */
    public String getUrlForIcon(String icon, boolean highlighted) {
        String highlightParam = highlighted ? "-selected" : "";
        return String.format("http://nerdery-umbrella.s3.amazonaws.com/%s%s.png", icon, highlightParam);
    }
}

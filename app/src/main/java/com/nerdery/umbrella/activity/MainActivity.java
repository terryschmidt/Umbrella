package com.nerdery.umbrella.activity;

import android.support.v7.widget.CardView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import com.nerdery.umbrella.R;
import com.nerdery.umbrella.api.IconApi;
import com.nerdery.umbrella.model.CurrentObservation;
import com.nerdery.umbrella.model.DisplayLocation;
import com.nerdery.umbrella.model.HourlyWeatherData;
import com.nerdery.umbrella.parser.Parser;
import com.nerdery.umbrella.model.ForecastCondition;
import com.nerdery.umbrella.network.DownloadImagesTask;
import com.nerdery.umbrella.network.DownloadTomorrowImagesTask;
import com.nerdery.umbrella.network.DownloadWeekdayImagesTask;
import android.os.*;
import java.io.*;
import java.net.*;
import android.widget.*;
import android.widget.Toast;
import android.content.*;
import android.view.*;
import org.json.*;
import java.util.*;
import android.graphics.*;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;

// Terry Schmidt, January 2016
// Thanks for taking the time to look at my code.  :)
// here's a link to my xkcd viewing app for Android if you want to check it out: https://play.google.com/store/apps/details?id=com.transitiontose.xkcdviewand

public class MainActivity extends AppCompatActivity {

    ScrollView scrollView;
    String myAPIKey = "8537729989d571f3";
    JSONObject rootJSON = null;
    public String userZip = "33908";
    String URLtoRequestDataFrom = "http://api.wunderground.com/api/" + myAPIKey + "/conditions/hourly/q/" + userZip + ".json";
    ButtonClickListener btnClick;
    ImageView settingsIcon;
    TextView cityAndStateLabel;
    TextView largeTempLabel;
    TextView weatherDescriptionLabel;
    RelativeLayout topRelativeLayout;
    RelativeLayout secondRelativeLayout;
    RelativeLayout thirdRelativeLayout;
    public static CardView firstCardView;
    public static CardView secondCardView;
    public static CardView thirdCardView;
    public static ViewGroup.LayoutParams todayParams;
    public static ViewGroup.LayoutParams tomorrowParams;
    public static ViewGroup.LayoutParams weekdayParams;
    TextView todayTextView;
    TextView weekdayTextView;
    Boolean shouldDisplayFahr = true;
    Boolean shouldDisplayCels = false;
    IconApi iconapi;
    GridView gv;
    GridView gv2;
    GridView gv3;
    public static CustomAdapter adapter;
    public static CustomAdapter tomorrowAdapter;
    public static CustomAdapter weekdayAdapter;
    public static int indexForTomorrowToStartAt;
    public static String[] hourlyTime = new String[24];
    public static String[] hourlyTemp = new String[24];
    public static String[] tomorrowHourlyTime = new String[36];
    public static String[] tomorrowHourlyTemp = new String[36];
    public static String[] weekdayHourlyTime = new String[36];
    public static String[] weekdayHourlyTemp = new String[36];
    static int indexOfCoolestFC = -1;
    static int indexOfHottestFC = -1;
    static int tomorrowIndexOfCoolest = -1;
    static int tomorrowIndexOfHottest = -1;
    static int weekdayIndexOfHottest = -1;
    static int weekdayIndexOfCoolest = -1;
    int weekdayCurrentHottest = -1000;
    int weekdayCurrentCoolest = 1000;
    int currentCoolestTempValue = 1000;
    int currentHottestTempValue = -1000;
    int tomorrowCurrentCoolest = 1000;
    int tomorrowCurrentHottest = -1000;
    int tomorrowCounter = 0;
    int weekdayCounter = 0;
    String[] todayIconURLsToDownload = new String[24];
    String[] tomorrowIconURLsToDownload = new String[36];
    String[] weekdayIconURLsToDownload = new String[36];
    public static int tomorrowEndPoint = -1;
    public int numDaysTraversed = 0;
    static public int numLeftToShow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Arrays.fill(hourlyTime, "");
        Arrays.fill(hourlyTemp, "");
        Arrays.fill(tomorrowHourlyTime, "");
        Arrays.fill(tomorrowHourlyTemp, "");
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        btnClick = new ButtonClickListener();
        firstCardView = (CardView) findViewById(R.id.firstCardView);
        secondCardView = (CardView) findViewById((R.id.secondCardView));
        thirdCardView = (CardView) findViewById(R.id.thirdCardView);
        int btn = R.id.settingsButton;
        View v = (View) findViewById(btn);
        v.setOnClickListener(btnClick);
        settingsIcon = (ImageView) findViewById(R.id.settingsButton);
        cityAndStateLabel = (TextView) findViewById(R.id.cityAndStateLabel);
        largeTempLabel = (TextView) findViewById(R.id.largeTempLabel);
        weatherDescriptionLabel = (TextView) findViewById(R.id.weatherDescriptionLabel);
        topRelativeLayout = (RelativeLayout) findViewById((R.id.topRelativeLayout));
        secondRelativeLayout = (RelativeLayout) findViewById(R.id.secondRelativeLayout);
        thirdRelativeLayout = (RelativeLayout) findViewById(R.id.thirdRelativeLayout);
        todayTextView = (TextView) findViewById(R.id.todayTextView);
        weekdayTextView = (TextView) findViewById(R.id.weekdayTextView);
        gv = (GridView) findViewById(R.id.gridview);
        gv2 = (GridView) findViewById(R.id.gridview2);
        gv3 = (GridView) findViewById(R.id.gridview3);
        adapter = new CustomAdapter(this, hourlyTime, hourlyTemp);
        tomorrowAdapter = new CustomAdapter(this, tomorrowHourlyTime, tomorrowHourlyTemp);
        weekdayAdapter = new CustomAdapter(this, weekdayHourlyTime, weekdayHourlyTemp);
        gv.setAdapter(adapter);
        gv2.setAdapter(tomorrowAdapter);
        gv3.setAdapter(weekdayAdapter);
        iconapi = new IconApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HourlyWeatherData.data.removeAll(HourlyWeatherData.data);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
            HourlyWeatherData.data.removeAll(HourlyWeatherData.data);
            String zip = SP.getString("uZip", "NA");
            if (validateZip(zip) == true) {
                userZip = zip;
                URLtoRequestDataFrom = "http://api.wunderground.com/api/" + myAPIKey + "/conditions/hourly/q/" + userZip + ".json";
                String tempType = SP.getString("tempType", "1");
                if (tempType.equals("1")) {
                    shouldDisplayFahr = true;
                    shouldDisplayCels = false;
                } else {
                    shouldDisplayFahr = false;
                    shouldDisplayCels = true;
                }

                getWeatherDataAsync();
            } else {
                requestZipAgain();
            }
        } else {
            topRelativeLayout.setBackgroundColor(Color.BLACK);
            weatherDescriptionLabel.setText("No connection");
            cityAndStateLabel.setText("");
            largeTempLabel.setText("");
            Arrays.fill(todayIconURLsToDownload, "");
            Arrays.fill(tomorrowIconURLsToDownload, "");
            Arrays.fill(hourlyTime, "");
            Arrays.fill(hourlyTemp, "");
            Arrays.fill(tomorrowHourlyTime, "");
            Arrays.fill(tomorrowHourlyTemp, "");
            Arrays.fill(adapter.imageIconStrings, "");
            Arrays.fill(tomorrowAdapter.imageIconStrings, "");
            Arrays.fill(weekdayIconURLsToDownload, "");
            Arrays.fill(weekdayHourlyTime, "");
            Arrays.fill(weekdayHourlyTemp, "");
            Arrays.fill(weekdayAdapter.imageIconStrings, "");
            adapter.bmList = new Bitmap[36];
            tomorrowAdapter.bmList = new Bitmap[36];
            weekdayAdapter.bmList = new Bitmap[36];
            adapter.notifyDataSetChanged();
            tomorrowAdapter.notifyDataSetChanged();
            weekdayAdapter.notifyDataSetChanged();
        }
    }

    private Boolean validateZip(String zip) {
        if (zip.contains("%") || zip.contains("\n")) { // if these are in the zip, it crashes the app, so they're disallowed.
            return false;
        } else {
            return true;
        }
    }

    private void getWeatherDataAsync() {
        new DownloadWebpageTask().execute(URLtoRequestDataFrom);
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                System.out.println("Unable to retrieve web page.");
                return "Unable to retrieve web page.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            todayParams =  firstCardView.getLayoutParams();
            tomorrowParams =  secondCardView.getLayoutParams();
            weekdayParams = thirdCardView.getLayoutParams();
            indexForTomorrowToStartAt = -1; // reset from previous pass through

            // after getting json from WU, we need to do a lot of work afterward to parse it, download icons, and display it all
            try {
                rootJSON = new JSONObject(result);
                Parser.parseCurrentObs(rootJSON);
                Parser.parseHourlyData(rootJSON);
            } catch (org.json.JSONException j) {
                System.out.println("JSONObject creation failed.");
                requestZipAgain();
                return;
            }

            erasePreviousData: {
                Arrays.fill(weekdayAdapter.imageIconStrings, "");
                Arrays.fill(weekdayIconURLsToDownload, "");
                Arrays.fill(weekdayHourlyTemp, "");
                Arrays.fill(weekdayHourlyTime, "");
                Arrays.fill(adapter.imageIconStrings, "");
                Arrays.fill(hourlyTemp, "");
                Arrays.fill(todayIconURLsToDownload, "");
                Arrays.fill(hourlyTime, "");
                Arrays.fill(tomorrowHourlyTemp, "");
                Arrays.fill(tomorrowIconURLsToDownload, "");
                Arrays.fill(tomorrowHourlyTime, "");
                Arrays.fill(tomorrowAdapter.imageIconStrings, "");
            }

            addCurrentObservationData: {
                int temperatureVal;

                cityAndStateLabel.setText(DisplayLocation.full);

                temperatureVal = (int) Math.round(CurrentObservation.tempFahrenheit);
                if (temperatureVal < 60) {
                    topRelativeLayout.setBackgroundColor(Color.argb(255, 3, 169, 244));
                } else {
                    topRelativeLayout.setBackgroundColor(Color.argb(255, 255, 152, 0));
                }

                if (shouldDisplayFahr) {
                    largeTempLabel.setText(String.valueOf((int) Math.round(CurrentObservation.tempFahrenheit)) + "°");
                }

                if (shouldDisplayCels) {
                    largeTempLabel.setText(String.valueOf((int) Math.round(CurrentObservation.tempCelsius)) + "°");
                }

                weatherDescriptionLabel.setText(CurrentObservation.weather);
            }

            addTodayHourlyTextData: {
                for (int i = 0; i < 24; i++) {
                    ForecastCondition tmp = HourlyWeatherData.data.get(i);

                    if (tmp.hour.equals("0")) {
                        indexForTomorrowToStartAt = i;
                        break addTodayHourlyTextData;
                    }

                    int currentTemp = -1;
                    try {
                        currentTemp = Integer.parseInt(tmp.tempFahrenheit);
                    } catch (NumberFormatException e) {

                    }

                    if (currentTemp > currentHottestTempValue) {
                        currentHottestTempValue = currentTemp;
                        indexOfHottestFC = i;
                        adapter.indexOfHottest = indexOfHottestFC;
                    }

                    if (currentTemp < currentCoolestTempValue) {
                        currentCoolestTempValue = currentTemp;
                        indexOfCoolestFC = i;
                        adapter.indexOfCoolest = indexOfCoolestFC;
                    }

                    hourlyTime[i] = tmp.civil;
                    adapter.imageIconStrings[i] = tmp.icon; // set icons

                    if (shouldDisplayFahr) {
                        hourlyTemp[i] = tmp.tempFahrenheit + "°";
                    }

                    if (shouldDisplayCels) {
                        hourlyTemp[i] = tmp.tempCelsius + "°";
                    }
                }
            }

            addTommorowHourlyTextData: {
                numLeftToShow = 36 - indexForTomorrowToStartAt;
                if (numLeftToShow <= 24) {
                    for (int i = indexForTomorrowToStartAt; i < 36; i++) {
                        ForecastCondition tmp = HourlyWeatherData.data.get(i);
                        int currentTemp = -1;

                        try {
                            currentTemp = Integer.parseInt(tmp.tempFahrenheit);
                        } catch (NumberFormatException e) {

                        }

                        if (currentTemp > tomorrowCurrentHottest) {
                            tomorrowCurrentHottest = currentTemp;
                            tomorrowIndexOfHottest = i - indexForTomorrowToStartAt;
                            tomorrowAdapter.indexOfHottest = tomorrowIndexOfHottest;
                        }

                        if (currentTemp < tomorrowCurrentCoolest) {
                            tomorrowCurrentCoolest = currentTemp;
                            tomorrowIndexOfCoolest = i - indexForTomorrowToStartAt;
                            tomorrowAdapter.indexOfCoolest = tomorrowIndexOfCoolest;
                        }

                        tomorrowHourlyTime[tomorrowCounter] = tmp.civil;
                        tomorrowAdapter.imageIconStrings[tomorrowCounter] = tmp.icon;

                        if (shouldDisplayFahr) {
                            tomorrowHourlyTemp[tomorrowCounter] = tmp.tempFahrenheit + "°";
                        }

                        if (shouldDisplayCels) {
                            tomorrowHourlyTemp[tomorrowCounter] = tmp.tempCelsius + "°";
                        }
                        tomorrowCounter++;
                    }
                } else {
                    int lengthToGo = indexForTomorrowToStartAt + 24;
                    for (int i = indexForTomorrowToStartAt; i <= lengthToGo; i++) {
                        ForecastCondition tmp = HourlyWeatherData.data.get(i);
                        int currentTemp = -1;

                        try {
                            currentTemp = Integer.parseInt(tmp.tempFahrenheit);
                        } catch (NumberFormatException e) {

                        }

                        if (tmp.hour.equals("0") && numDaysTraversed != 2) {
                            numDaysTraversed++;
                        }

                        if (tmp.hour.equals("0") && numDaysTraversed == 2) {
                            tomorrowEndPoint = i;

                            addWeekdayHourlyTextData: {
                                for (int j = tomorrowEndPoint; j < 36; j++) {
                                    ForecastCondition temp = HourlyWeatherData.data.get(j);
                                    int current = -1;

                                    try {
                                        current = Integer.parseInt(temp.tempFahrenheit);
                                    } catch (NumberFormatException e) {

                                    }

                                    if (current > weekdayCurrentHottest) {
                                        weekdayCurrentHottest = current;
                                        weekdayIndexOfHottest = j - tomorrowEndPoint;
                                        weekdayAdapter.indexOfHottest = weekdayIndexOfHottest;
                                    }

                                    if (current < weekdayCurrentCoolest) {
                                        weekdayCurrentCoolest = current;
                                        weekdayIndexOfCoolest = j - tomorrowEndPoint;
                                        weekdayAdapter.indexOfCoolest = weekdayIndexOfCoolest;
                                    }

                                    weekdayHourlyTime[weekdayCounter] = temp.civil;
                                    weekdayAdapter.imageIconStrings[weekdayCounter] = temp.icon;

                                    if (shouldDisplayFahr) {
                                        weekdayHourlyTemp[weekdayCounter] = temp.tempFahrenheit + "°";
                                    }

                                    if (shouldDisplayCels) {
                                        weekdayHourlyTemp[weekdayCounter] = temp.tempCelsius + "°";
                                    }

                                    weekdayCounter++;
                                    weekdayTextView.setText(temp.weekday);
                                }
                            }
                            break addTommorowHourlyTextData;
                        }

                        if (currentTemp > tomorrowCurrentHottest) {
                            tomorrowCurrentHottest = currentTemp;
                            tomorrowIndexOfHottest = i - indexForTomorrowToStartAt;
                            tomorrowAdapter.indexOfHottest = tomorrowIndexOfHottest;
                        }

                        if (currentTemp < tomorrowCurrentCoolest) {
                            tomorrowCurrentCoolest = currentTemp;
                            tomorrowIndexOfCoolest = i - indexForTomorrowToStartAt;
                            tomorrowAdapter.indexOfCoolest = tomorrowIndexOfCoolest;
                        }

                        tomorrowHourlyTime[tomorrowCounter] = tmp.civil;
                        tomorrowAdapter.imageIconStrings[tomorrowCounter] = tmp.icon;

                        if (shouldDisplayFahr) {
                            tomorrowHourlyTemp[tomorrowCounter] = tmp.tempFahrenheit + "°";
                        }

                        if (shouldDisplayCels) {
                            tomorrowHourlyTemp[tomorrowCounter] = tmp.tempCelsius + "°";
                        }
                        tomorrowCounter++;
                    }
                }
            }

            getAndSetTodayIconsAsync: {
                int length = adapter.imageIconStrings.length;

                for (int i = 0; i < length; i++) {
                    if (!adapter.imageIconStrings[i].equals("") && i == indexOfCoolestFC && i != indexOfHottestFC) {
                        todayIconURLsToDownload[i] = iconapi.getUrlForIcon(adapter.imageIconStrings[i], true);
                    } else if (!adapter.imageIconStrings[i].equals("") && i == indexOfHottestFC && i != indexOfCoolestFC) {
                        todayIconURLsToDownload[i] = iconapi.getUrlForIcon(adapter.imageIconStrings[i], true);
                    } else if (!adapter.imageIconStrings[i].equals("")) {
                        todayIconURLsToDownload[i] = iconapi.getUrlForIcon(adapter.imageIconStrings[i], false);
                    }
                }

                new DownloadImagesTask().execute(todayIconURLsToDownload);
            }

            getAndSetTomorrowIconsAsync: {
                int length = tomorrowAdapter.imageIconStrings.length;

               for (int i = 0; i < length; i++) {
                    if (!tomorrowAdapter.imageIconStrings[i].equals("") && i == tomorrowIndexOfCoolest && i != tomorrowIndexOfHottest) {
                        tomorrowIconURLsToDownload[i] = iconapi.getUrlForIcon(tomorrowAdapter.imageIconStrings[i], true);
                    } else if (!tomorrowAdapter.imageIconStrings[i].equals("") && i == tomorrowIndexOfHottest && i != tomorrowIndexOfCoolest) {
                        tomorrowIconURLsToDownload[i] = iconapi.getUrlForIcon(tomorrowAdapter.imageIconStrings[i], true);
                    } else if (!tomorrowAdapter.imageIconStrings[i].equals("")) {
                        tomorrowIconURLsToDownload[i] = iconapi.getUrlForIcon(tomorrowAdapter.imageIconStrings[i], false);
                    }
                }

                new DownloadTomorrowImagesTask().execute(tomorrowIconURLsToDownload);
            }

            getAndSetWeekdayIconsAsync: {
                int length = weekdayAdapter.imageIconStrings.length;

                for (int i = 0; i < length; i++) {
                    if (!weekdayAdapter.imageIconStrings[i].equals("") && i == weekdayIndexOfCoolest && i != weekdayIndexOfHottest) {
                        weekdayIconURLsToDownload[i] = iconapi.getUrlForIcon(weekdayAdapter.imageIconStrings[i], true);
                    } else if (!weekdayAdapter.imageIconStrings[i].equals("") && i == weekdayIndexOfHottest && i != weekdayIndexOfCoolest) {
                        weekdayIconURLsToDownload[i] = iconapi.getUrlForIcon(weekdayAdapter.imageIconStrings[i], true);
                    } else if (!weekdayAdapter.imageIconStrings[i].equals("")) {
                        weekdayIconURLsToDownload[i] = iconapi.getUrlForIcon(weekdayAdapter.imageIconStrings[i], false);
                    }
                }

                new DownloadWeekdayImagesTask().execute(weekdayIconURLsToDownload);
            }

            housekeeping: {
                weekdayCounter = 0;
                tomorrowCounter = 0;
                currentHottestTempValue = -1000;
                currentCoolestTempValue = 1000;
                tomorrowCurrentHottest = -1000;
                tomorrowCurrentCoolest = 1000;
                weekdayCurrentHottest = -1000;
                weekdayCurrentCoolest = 1000;
                indexOfCoolestFC = -1;
                indexOfHottestFC = -1;
                weekdayIndexOfCoolest = -1;
                weekdayIndexOfHottest = -1;
                tomorrowIndexOfCoolest = -1;
                tomorrowIndexOfHottest = -1;
                numDaysTraversed = 0;
            }
        }
    }

    private void requestZipAgain() {
        Toast.makeText(this, "Please enter a valid zip code.", Toast.LENGTH_LONG).show();
        settingsPressed();
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            //int response = conn.getResponseCode();
            is = conn.getInputStream();
            String contentAsString = convertStreamToString(is);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private void settingsPressed() {
        Intent i = new Intent(this, PrefActivity.class);
        startActivity(i);
    }

    private class ButtonClickListener implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.settingsButton: settingsPressed(); break;
            }
        }
    }
}
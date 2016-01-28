package com.nerdery.umbrella.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.nerdery.umbrella.activity.MainActivity;

import java.io.InputStream;

/**
 * Created by terryschmidt on 1/18/16.
 */
public class DownloadWeekdayImagesTask extends AsyncTask<String, Void, Bitmap[]> {

    @Override
    protected Bitmap[] doInBackground(String[] urls) {
        Bitmap[] tmp = new Bitmap[36];
        Bitmap image = null;
        int length = urls.length;

        for (int i = 0; i < length; i++) {
            String urldisplay = urls[i];

            if (!urldisplay.equals("")) {
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    image = BitmapFactory.decodeStream(in);
                    tmp[i] = image;
                } catch (Exception e) {
                    System.out.println("Couldn't retrieve a today icon at address: " + urldisplay);
                    e.printStackTrace();
                }
            }
        }
        return tmp;
    }

    protected void onPostExecute(Bitmap[] result) {
        MainActivity.weekdayAdapter.bmList = result;
        MainActivity.weekdayAdapter.notifyDataSetChanged();

        setThirdCardViewSize: {
            int numberOfHourlyWeatherDataPointsBeingDisplayed = MainActivity.numLeftToShow - 24;

            if (numberOfHourlyWeatherDataPointsBeingDisplayed >= 21 && numberOfHourlyWeatherDataPointsBeingDisplayed <= 24) { // 6 rows
                MainActivity.weekdayParams.height = 2520;
            } else if (numberOfHourlyWeatherDataPointsBeingDisplayed >= 1 && numberOfHourlyWeatherDataPointsBeingDisplayed <= 4) { // 1 row
                MainActivity.weekdayParams.height = 650;
            } else if (numberOfHourlyWeatherDataPointsBeingDisplayed >= 5 && numberOfHourlyWeatherDataPointsBeingDisplayed <= 8) { // 2 rows
                MainActivity.weekdayParams.height = 1015;
            } else if (numberOfHourlyWeatherDataPointsBeingDisplayed >= 9 && numberOfHourlyWeatherDataPointsBeingDisplayed <= 12) { // 3 rows
                MainActivity.weekdayParams.height = 1400;
            } else if (numberOfHourlyWeatherDataPointsBeingDisplayed >= 13 && numberOfHourlyWeatherDataPointsBeingDisplayed <= 16) { // 4 rows
                MainActivity.weekdayParams.height = 1770;
            } else if (numberOfHourlyWeatherDataPointsBeingDisplayed >= 17 && numberOfHourlyWeatherDataPointsBeingDisplayed <= 20) { // 5 rows
                MainActivity.weekdayParams.height = 2200;
            } else {
                MainActivity.weekdayParams.height = 0;
            }

            MainActivity.thirdCardView.setLayoutParams(MainActivity.weekdayParams);
        }
    }
}

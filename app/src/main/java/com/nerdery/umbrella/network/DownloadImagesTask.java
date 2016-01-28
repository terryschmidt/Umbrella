package com.nerdery.umbrella.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.InputStream;
import com.nerdery.umbrella.activity.MainActivity;

/**
 * Created by terryschmidt on 1/17/16.
 */
public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap[]> {

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
        MainActivity.adapter.bmList = result;
        MainActivity.adapter.notifyDataSetChanged();

        setFirstCardViewSize: {
            int numberOfHourlyWeatherDataPointsBeingDisplayed = MainActivity.indexForTomorrowToStartAt;

            if (numberOfHourlyWeatherDataPointsBeingDisplayed >= 21 && numberOfHourlyWeatherDataPointsBeingDisplayed <= 24) { // 6 rows
                MainActivity.todayParams.height = 2520;
            } else if (numberOfHourlyWeatherDataPointsBeingDisplayed >= 1 && numberOfHourlyWeatherDataPointsBeingDisplayed <= 4) { // 1 row
                MainActivity.todayParams.height = 650;
            } else if (numberOfHourlyWeatherDataPointsBeingDisplayed >= 5 && numberOfHourlyWeatherDataPointsBeingDisplayed <= 8) { // 2 rows
                MainActivity.todayParams.height = 1015;
            } else if (numberOfHourlyWeatherDataPointsBeingDisplayed >= 9 && numberOfHourlyWeatherDataPointsBeingDisplayed <= 12) { // 3 rows
                MainActivity.todayParams.height = 1400;
            } else if (numberOfHourlyWeatherDataPointsBeingDisplayed >= 13 && numberOfHourlyWeatherDataPointsBeingDisplayed <= 16) { // 4 rows
                MainActivity.todayParams.height = 1770;
            } else if (numberOfHourlyWeatherDataPointsBeingDisplayed >= 17 && numberOfHourlyWeatherDataPointsBeingDisplayed <= 20) { // 5 rows
                MainActivity.todayParams.height = 2200;
            } else if (numberOfHourlyWeatherDataPointsBeingDisplayed == 0) { // nothing to display
                MainActivity.todayParams.height = 650;
            }

            MainActivity.firstCardView.setLayoutParams(MainActivity.todayParams);
        }
    }
}

package com.nerdery.umbrella.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.nerdery.umbrella.activity.MainActivity;
import java.io.InputStream;

/**
 * Created by terryschmidt on 1/17/16.
 */
public class DownloadTomorrowImagesTask extends AsyncTask<String, Void, Bitmap[]> {

    @Override
    protected Bitmap[] doInBackground(String[] urls) {
        Bitmap[] tmp = new Bitmap[36];
        Bitmap image;
        int length = urls.length;

        for (int i = 0; i < length; i++) {
            String urldisplay = urls[i];

            if (!urldisplay.equals("")) {
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    image = BitmapFactory.decodeStream(in);
                    tmp[i] = image;
                } catch (Exception e) {
                    System.out.println("Couldn't retrieve a tomorrow icon at address: " + urldisplay);
                    e.printStackTrace();
                }
            }
        }

        return tmp;
    }

    protected void onPostExecute(Bitmap[] result) {
        MainActivity.tomorrowAdapter.bmList = result;
        MainActivity.tomorrowAdapter.notifyDataSetChanged();

        setSecondCardViewSize: {
            int numLeftToDisplay = 36 - MainActivity.indexForTomorrowToStartAt;

            if (numLeftToDisplay  <= 12) {
                MainActivity.tomorrowParams.height = 1400;
            } else if (numLeftToDisplay >= 13 && numLeftToDisplay <= 16) {
                MainActivity.tomorrowParams.height = 1770;
            } else if (numLeftToDisplay >= 17 && numLeftToDisplay <= 20) {
                MainActivity.tomorrowParams.height = 2200;
            } else if (numLeftToDisplay >= 21 && numLeftToDisplay <= 36) {
                MainActivity.tomorrowParams.height = 2520;
            }

            MainActivity.secondCardView.setLayoutParams(MainActivity.tomorrowParams);
        }
    }
}
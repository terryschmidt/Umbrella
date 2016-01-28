package com.nerdery.umbrella.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nerdery.umbrella.R;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.content.res.ColorStateList;
import java.util.Arrays;

public class CustomAdapter extends BaseAdapter {

    int indexOfCoolest;
    int indexOfHottest;
    String [] times;
    String [] temps;
    String [] imageIconStrings = new String[36];
    public Bitmap [] bmList = new Bitmap[36];
    Context context;
    private static LayoutInflater inflater = null;
    public CustomAdapter(MainActivity mainActivity, String[] timeList, String[] tempList) {
        times = timeList;
        temps = tempList;
        context = mainActivity;
        Arrays.fill(imageIconStrings, "");
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return times.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tv;
        TextView tv2;
        ImageView iv;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.hourlylist, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textView1);
        holder.tv2 = (TextView) rowView.findViewById(R.id.textView2);
        holder.iv = (ImageView) rowView.findViewById(R.id.imageView1);

        holder.tv.setTextColor(Color.BLACK);
        holder.tv2.setTextColor(Color.BLACK);

        if (indexOfHottest != indexOfCoolest) {
            if (position == indexOfCoolest) {
                holder.tv.setTextColor(Color.argb(255, 3, 169, 244));
                holder.tv2.setTextColor(Color.argb(255, 3, 169, 244));
                holder.iv.setColorFilter(Color.argb(255, 3, 169, 244));
            } else if (position == indexOfHottest) {
                holder.tv.setTextColor(Color.argb(255, 255, 152, 0));
                holder.tv2.setTextColor(Color.argb(255, 255, 152, 0));
                holder.iv.setColorFilter(Color.argb(255, 255, 152, 0));
            } else {
                holder.iv.setColorFilter(Color.BLACK);
            }
        } else {
            holder.iv.setColorFilter(Color.BLACK);
        }

        holder.tv.setText(times[position]);
        holder.tv2.setText(temps[position]);
        holder.iv.setImageBitmap(bmList[position]);

        return rowView;
    }
}
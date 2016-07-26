package com.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.test.util.AppConstants;
import com.test.util.Helper;
import com.test.util.WeatherDetails;
import com.test.weatherforecast.R;

import java.util.ArrayList;

/**
 * Created by mohit.kansal on 7/25/2016.
 */
public class WeatherForecastListAdapter extends ArrayAdapter<WeatherDetails> {

    private Context mContext;
    private ArrayList<WeatherDetails> mListDtls;
    private int resourceLayoutId;
    ViewHolder holder;

    public WeatherForecastListAdapter(Context mContext, int resourceLayoutId,
                                      ArrayList<WeatherDetails> mListDtls) {
        super(mContext, resourceLayoutId, mListDtls);

        this.mContext = mContext;
        this.resourceLayoutId = resourceLayoutId;
        this.mListDtls = mListDtls;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        try {

            if (view == null) {

                // inflate the layout
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(resourceLayoutId, null);

                holder = new ViewHolder();

                holder.tvForecastTime = (TextView) view
                        .findViewById(R.id.row_details_tv_time);
                holder.tvForecastDetails = (TextView) view
                        .findViewById(R.id.row_details_tv_detail);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            WeatherDetails mObj = mListDtls.get(position);

            holder.tvForecastDetails.setText(mObj.getForecastDetails());

            String convertedDate = Helper.convertDateFormat(mContext, mObj.getForecastTime(), AppConstants.RESPONSE_DATE_FORMAT,
                    AppConstants.DISPLAY_DATE_FORMAT);
            holder.tvForecastTime.setText(convertedDate);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public class ViewHolder {
        public TextView tvForecastTime;
        public TextView tvForecastDetails;
    }

}
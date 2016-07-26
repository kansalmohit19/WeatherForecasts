package com.test.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mohit.kansal on 7/25/2016.
 */
public class Helper {

    public static final int LOCATION_PERMISSION = 1;

    /**
     * hide keyboard
     *
     * @param context
     * @param focussedView
     */
    public static void hideKeyboard(Context context, View focussedView) {
        try {

            if (focussedView != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focussedView.getWindowToken(), 0);
            } else {
                InputMethodManager inputManager = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(((Activity) context)
                                .getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert date time format
     *
     * @param context
     * @param date
     * @param convertFromFormat
     * @param convertToFormat
     * @return date
     */
    public static String convertDateFormat(Context context, String date,
                                           String convertFromFormat, String convertToFormat) {

        String mDate = "";
        try {
            Date dateTime = new SimpleDateFormat(convertFromFormat).parse(date);
            mDate = new SimpleDateFormat(convertToFormat).format(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mDate;
    }

    /**
     * check whether location permission is granted or not for
     * marshmallow
     *
     * @param mCtx
     * @return
     */
    public static boolean isLocationPermissionGranted(Context mCtx) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (mCtx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Location", "Permission is granted");
                return true;
            } else {

                Log.v("Location", "Permission is revoked");
                ActivityCompat.requestPermissions(((Activity) mCtx), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Location", "Permission is granted");
            return true;
        }
    }

}

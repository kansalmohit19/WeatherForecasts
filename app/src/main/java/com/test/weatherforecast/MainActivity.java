package com.test.weatherforecast;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.test.adapter.WeatherForecastListAdapter;
import com.test.util.AppConstants;
import com.test.util.Helper;
import com.test.util.WeatherDetails;
import com.test.web.WeatherService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //general params
    private Context mContext;
    private boolean isLocationFetched = false;
    private String cityName = "";

    //layout params
    private EditText etCityName;
    private ListView lvWeatherInfo;
    private Button btnSearch;
    private TextView tvNoData;
    private ImageView ivLocation;

    //fused location fields
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public final int LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        //initiliase the layout params
        etCityName = (EditText) findViewById(R.id.main_act_et_city_name);
        lvWeatherInfo = (ListView) findViewById(R.id.main_act_lv_info);
        btnSearch = (Button) findViewById(R.id.main_act_btn_search);
        tvNoData = (TextView) findViewById(R.id.main_act_tv_no_data);
        ivLocation = (ImageView) findViewById(R.id.main_act_iv_location);

        //intialise google api
        intGoogleApiClient();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //minimum four characters are required
                if (etCityName.getText().toString().trim().length() > 3) {

                    //hide keyboard
                    Helper.hideKeyboard(mContext, v);

                    // get weather details for respective serach
                    getDetails(etCityName.getText().toString().trim());
                }
                //show error message to enter something
                else if (etCityName.getText().toString().trim().length() == 0) {
                    Toast.makeText(mContext, getResources().getString(R.string.err_city_name), Toast.LENGTH_LONG).show();
                }
                //show error message to enter atleast four characters
                else {
                    Toast.makeText(mContext, getResources().getString(R.string.err_city_name_min_length), Toast.LENGTH_LONG).show();
                }
            }
        });

        ivLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Helper.isLocationPermissionGranted(mContext)) {

                    //set text as city name
                    etCityName.setText(cityName);

                }
            }
        });

    }

    /**
     * get weather forecast for the search city
     *
     * @param searchCity
     */
    private void getDetails(String searchCity) {

        try {
            WeatherService gitHubService = WeatherService.retroService.create(WeatherService.class);

            final Call<Object> call =
                    gitHubService.getWeatherDetails(searchCity, AppConstants.WEATHER_FORECAST_API);

            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {

                    try {

                        //serialise the output response
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());

                        // downcast it into JSON object
                        JSONObject mResponseData = new JSONObject(json);

                        // get list of weather details
                        JSONArray arrWeatherDtls = mResponseData.getJSONArray("list");

                        if (arrWeatherDtls.length() > 0) {

                            //hide error message and show data
                            tvNoData.setVisibility(View.GONE);
                            lvWeatherInfo.setVisibility(View.VISIBLE);


                            setDataToList(getModifiedData(arrWeatherDtls));

                        } else {

                            //show error message
                            lvWeatherInfo.setVisibility(View.GONE);
                            tvNoData.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

                    //show error message
                    lvWeatherInfo.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * set data to list and its adapter
     *
     * @param listDtls
     */
    private void setDataToList(ArrayList<WeatherDetails> listDtls) {

        try {
            WeatherForecastListAdapter mAdapter = new WeatherForecastListAdapter(mContext, R.layout.row_weather_forecast,
                    listDtls);
            lvWeatherInfo.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * convert json array to arraylist and get required data
     *
     * @param jsonArray
     * @return
     */
    private ArrayList<WeatherDetails> getModifiedData(JSONArray jsonArray) {

        ArrayList<WeatherDetails> listDtls = null;
        try {
            listDtls = new ArrayList<WeatherDetails>();

            int listSize = 0;

            if (jsonArray.length() >= AppConstants.FORECAST_LIST_SIZE) {
                listSize = AppConstants.FORECAST_LIST_SIZE;
            } else {
                listSize = jsonArray.length();
            }

            for (int i = 0; i < listSize; i++) {

                JSONObject mObj = jsonArray.getJSONObject(i);

                //get weather forecast details
                JSONArray arrForecastDtls = mObj.getJSONArray("weather");
                String strForecastDtls = arrForecastDtls.getJSONObject(0).getString("description");

                //get weather forecast time
                String strForecastTime = mObj.getString("dt_txt");

                WeatherDetails mDtls = new WeatherDetails(strForecastTime, strForecastDtls);

                listDtls.add(mDtls);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return listDtls;
    }

    // location code starts //////////////////////////
    @Override
    public void onConnected(Bundle bundle) {

        try {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(500); // Update location every second

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {

                updateUi(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        updateUi(location.getLatitude(), location.getLongitude());

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        intGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    synchronized void intGoogleApiClient() {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // location code starts //////////////////////////


    /**
     * get city from latitude and longitude
     *
     * @param latitude
     * @param longitude
     */
    private void updateUi(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            cityName = addresses.get(0).getLocality();

            Log.e("City", "" + cityName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * check for permission
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                //start getting coordinates for location
//                startLocationUpdates();
            } else {

                if (requestCode == LOCATION_PERMISSION) {

                    boolean neverAskedFlag = !ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

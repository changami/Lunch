package com.changami.app.lunchdecider.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.changami.app.lunchdecider.R;
import com.changami.app.lunchdecider.data.PlaceEntity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MapActivity extends Activity implements OnMapReadyCallback, LocationListener {

    LocationManager manager;
    double latitude;
    double longitude;

    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.inject(this);

        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_for_map, MapFragment.newInstance());
        fragmentTransaction.commit();

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 低精度・低消費電力でお願いします
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = manager.getBestProvider(criteria, true);
        manager.requestLocationUpdates(provider, 0, 0, MapActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_for_map);
        mapFragment.getMapAsync(this);
    }

    @OnClick(R.id.search_button)
    void onClicking() {
        searchNearPlace("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeUpdates(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // 表示するGoogleMapの種類
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        // 現在地を表示するボタンの有無
        map.setMyLocationEnabled(true);
        // TODO:現在地中心に表示する
    }

    public URL createRequestUrl(double latitude, double longitude, String rankBy, Boolean sensor, String types, Boolean openNow) {
        StringBuilder urlStrBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json");
        urlStrBuilder.append("?location=" + latitude + "," + longitude);
        urlStrBuilder.append("&rankby=" + rankBy);
        urlStrBuilder.append("&sensor=" + sensor.toString());
        urlStrBuilder.append("&types=" + types);
        urlStrBuilder.append("&opennow=" + openNow.toString());
        urlStrBuilder.append("&key=" + getResources().getString(R.string.api_key));

        Log.e("Request URL: ", urlStrBuilder.toString());

        URL resultURL;
        try {
            resultURL = new URL(urlStrBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            resultURL = null;
        }

        return resultURL;
    }

    public void searchNearPlace(String types) {
        final String RANK_BY = "distance"; //距離が近い順に取得する
        final Boolean SENSOR = true;
        final String TYPES = "restaurant|cafe|bakery"; //検索するプレイスのタイプ
        final Boolean OPEN_NOW = false; //実行時に営業中のものだけを取得するかどうか

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {

            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                // show dialog
                progressDialog = new ProgressDialog(MapActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.now_loading));
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... types) {

                String data = "";
                URL url = createRequestUrl(latitude, longitude, RANK_BY, SENSOR, TYPES, OPEN_NOW);

                // JSONを取得
                HttpURLConnection con;
                try {
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.connect();
                    InputStream is = new BufferedInputStream(con.getInputStream());

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                    int bytesRead = -1;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = is.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    data = outputStream.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return data;
            }

            @Override
            protected void onPostExecute(String result) {
                ArrayList<PlaceEntity> places = new ArrayList<PlaceEntity>();

                try {
                    // JSONをパースする
                    JSONObject rootObject = new JSONObject(result);
                    JSONArray resultArray = rootObject.getJSONArray("results");

                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject jsonObject = resultArray.getJSONObject(i);

                        PlaceEntity place = new PlaceEntity();
                        place.setLatitude(jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                        place.setLongitude(jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                        place.set_id(jsonObject.getString("id"));
                        place.setName(jsonObject.getString("name"));
                        place.setIconUrl(jsonObject.getString("icon"));
                        place.setOpenNow(jsonObject.getJSONObject("opening_hours").getBoolean("open_now"));
                        place.setPlaceId(jsonObject.getString("place_id"));
                        place.setVicinity(jsonObject.getString("vicinity"));

                        places.add(place);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 取得したプレイスの数だけマーカーを置く
                for (PlaceEntity place : places) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(place.getLatitude(), place.getLongitude()))
                            .title(place.getName() + "\n"
                                    + place.getVicinity()));
                }

                // close dialog
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        };
        task.execute(types);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

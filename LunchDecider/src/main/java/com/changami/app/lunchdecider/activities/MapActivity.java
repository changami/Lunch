package com.changami.app.lunchdecider.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.changami.app.lunchdecider.R;
import com.changami.app.lunchdecider.data.PlaceEntity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

public class MapActivity extends Activity implements OnMapReadyCallback {

    // 現在の緯度経度はGooglePlacesAPIへのPATHで使用する
    double latitude;
    double longitude;

    GoogleMap map;

    ArrayList<Marker> markers = new ArrayList<Marker>();

    @InjectView(R.id.linear_search)
    LinearLayout linearSearch;
    @InjectView(R.id.keyword_search_input_edit_text)
    EditText keywordInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.inject(this);

        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_for_map, MapFragment.newInstance());
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_for_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // 表示するGoogleMapの種類
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        // 現在地を表示するボタンの有無
        map.setMyLocationEnabled(true);

        // 現在地中心に表示する
        CameraPosition.Builder builder = new CameraPosition.Builder().zoom(15);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (location == null) {
            // getLastKnownLocationを実行して、nullが返ってきた場合はGPSの最後に取得した位置を取得する。
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        // locationが取得できている場合はカメラの初期位置を変動させる。
        if (location != null) {
            builder.target(new LatLng(location.getLatitude(), location.getLongitude()));
            map.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
            // 設定した緯度経度も保持しておく
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        // LocationChangeListenerをSet
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        });
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
                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(place.getLatitude(), place.getLongitude()))
                            .title(place.getName() + "\n"
                                    + place.getVicinity()));
                    markers.add(marker);
                }

                // close dialog
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        };
        task.execute(types);
    }

    @OnClick(R.id.search_button)
    void onSearchClick() {
        searchNearPlace("");
    }

    @OnClick(R.id.clear_button)
    void onPinClearClick() {
        int markerCount = markers.size();
        // 排他処理
        if (markers == null || markerCount == 0) return;

        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

    @OnClick(R.id.show_linear_search_button)
    void onKeywordSearchFormShowClick() {
        linearSearch.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.keyword_search_cancel_button)
    void onKeywordSearchingCancelClick() {
        linearSearch.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(keywordInputEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}

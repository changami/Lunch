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
import android.widget.LinearLayout;
import android.widget.Toast;
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

    static final String REQUEST_METHOD = "GET";
    static final String REQUEST_URL_BASE = "https://maps.googleapis.com/maps/api/place/search/json";
    static final String REQUEST_URL_LOCATION = "?location=";
    static final String REQUEST_URL_RADIUS = "&radius=";
    static final String REQUEST_URL_TYPES = "&types=";
    static final String REQUEST_URL_KEYWORD = "&keyword=";
    static final String REQUEST_URL_KEY = "&key=";
    static final String REQUEST_URL_RANKBY_DISTANCE = "&rankby=distance";
    static final String REQUEST_URL_SENSOR_FALSE = "&sensor=false";
    static final String REQUEST_URL_OPENNOW_TRUE = "&opennow=true";

    // 現在の緯度経度はGooglePlacesAPIへのPATHで使用する
    double latitude;
    double longitude;

    GoogleMap map;

    ArrayList<Marker> markers = new ArrayList<Marker>();

    @InjectView(R.id.linear_category)
    LinearLayout categoryLinearLayout;

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

    /**
     * カテゴリー[restaurant, cafe, bakery, food, bar]に一致する
     * 最寄りのプレイスをリクエストするためのURLを生成する。
     *
     * @param latitude  double
     * @param longitude double
     * @return URL
     */
    public URL createNearbyRequestUrl(double latitude, double longitude) {
        StringBuilder sb = new StringBuilder(REQUEST_URL_BASE);
        sb.append(REQUEST_URL_LOCATION + latitude + "," + longitude);
        sb.append(REQUEST_URL_RANKBY_DISTANCE);
        sb.append(REQUEST_URL_SENSOR_FALSE);
        sb.append(REQUEST_URL_TYPES + "restaurant|cafe|bakery|food|bar");
        sb.append(REQUEST_URL_OPENNOW_TRUE);
        sb.append(REQUEST_URL_KEY + getResources().getString(R.string.api_key));

        Log.d("Request URL: ", sb.toString());

        URL resultURL;
        try {
            resultURL = new URL(sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            resultURL = null;
        }

        return resultURL;
    }

    /**
     * 引数categoryと一致するtypesを持った最寄りのプレイスをリクエストするためのURLを生成する。
     *
     * @param latitude  double
     * @param longitude double
     * @param category  String
     * @return URL
     */
    public URL createChooseCategoryUrl(double latitude, double longitude, String category) {
        StringBuilder sb = new StringBuilder(REQUEST_URL_BASE);
        sb.append(REQUEST_URL_LOCATION + latitude + "," + longitude);
        sb.append(REQUEST_URL_RANKBY_DISTANCE);
        sb.append(REQUEST_URL_SENSOR_FALSE);
        sb.append(REQUEST_URL_TYPES + category);
        sb.append(REQUEST_URL_OPENNOW_TRUE);
        sb.append(REQUEST_URL_KEY + getResources().getString(R.string.api_key));

        Log.d("Request URL: ", sb.toString());

        URL resultURL;
        try {
            resultURL = new URL(sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            resultURL = null;
        }

        return resultURL;
    }

    /**
     * キーワードに一致するプレイスをリクエストするためのURLを生成する。
     *
     * @param latitude  double
     * @param longitude double
     * @param keyword   String
     * @return URL
     */
    public URL createRequestUrl(double latitude, double longitude, String keyword) {
        String radius = "5000";

        StringBuilder sb = new StringBuilder(REQUEST_URL_BASE);
        sb.append(REQUEST_URL_LOCATION + latitude + "," + longitude);
        sb.append(REQUEST_URL_RADIUS + radius);
        sb.append(REQUEST_URL_SENSOR_FALSE);
        sb.append(REQUEST_URL_KEYWORD + keyword);
        sb.append(REQUEST_URL_KEY + getResources().getString(R.string.api_key));

        URL resultURL;
        try {
            resultURL = new URL(sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            resultURL = null;
        }

        return resultURL;
    }

    public String inputStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int bytesRead;
        byte[] buffer = new byte[1024];
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toString();
    }

    public void connectHTTPToGetRequestDataFromURLAndAddMarkersToMap(URL url) {

        AsyncTask<URL, Void, String> task = new AsyncTask<URL, Void, String>() {

            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                // show dialog
                progressDialog = new ProgressDialog(MapActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.now_loading));
                progressDialog.show();
            }

            @Override
            protected String doInBackground(URL... urls) {

                String data = "";

                // JSONを取得
                HttpURLConnection con;
                try {
                    con = (HttpURLConnection) urls[0].openConnection();
                    con.setRequestMethod(REQUEST_METHOD);
                    con.connect();
                    InputStream is = new BufferedInputStream(con.getInputStream());

                    data = inputStreamToString(is);
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
        task.execute(url);
    }

    @OnClick(R.id.nearby_button)
    void onNearbySearchClick() {
        clearAllPin();
        if (latitude == 0 && longitude == 0) {
            Toast.makeText(MapActivity.this, getString(R.string.can_not_catch_location), Toast.LENGTH_LONG).show();
            return;
        }
        URL url = createNearbyRequestUrl(latitude, longitude);
        connectHTTPToGetRequestDataFromURLAndAddMarkersToMap(url);
    }

    @OnClick(R.id.category_button)
    void onCategorySearchClick() {
        if (categoryLinearLayout.getVisibility() == View.VISIBLE) {
            categoryLinearLayout.setVisibility(View.GONE);
        } else {
            categoryLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.category_restaurant)
    void onCategoryRestaurantClick() {
        // value: restaurant
        String category = getResources().getString(R.string.button_category_restaurant);
        selectedCategorySearch(category);
    }

    @OnClick(R.id.category_cafe)
    void onCategoryCafeClick() {
        // value: cafe
        String category = getResources().getString(R.string.button_category_cafe);
        selectedCategorySearch(category);
    }

    @OnClick(R.id.category_bakery)
    void onCategoryBakeryClick() {
        // value: bakery
        String category = getResources().getString(R.string.button_category_bakery);
        selectedCategorySearch(category);
    }

    @OnClick(R.id.category_food)
    void onCategoryFoodClick() {
        // value: food
        String category = getResources().getString(R.string.button_category_food);
        selectedCategorySearch(category);
    }

    @OnClick(R.id.category_bar)
    void onCategoryBarClick() {
        // value: bar
        String category = getResources().getString(R.string.button_category_bar);
        selectedCategorySearch(category);
    }

    /**
     * 各カテゴリー検索ボタンが押された際の処理
     *
     * @param category String
     */
    private void selectedCategorySearch(String category) {
        // 既にピンが刺さっている場合は消す
        clearAllPin();
        // カテゴリー選択LinearLayoutを非表示
        categoryLinearLayout.setVisibility(View.GONE);

        URL url = createChooseCategoryUrl(latitude, longitude, category);
        connectHTTPToGetRequestDataFromURLAndAddMarkersToMap(url);
    }

    public void clearAllPin() {
        int markerCount = markers.size();
        // 排他処理
        if (markers == null || markerCount == 0) return;

        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }
}

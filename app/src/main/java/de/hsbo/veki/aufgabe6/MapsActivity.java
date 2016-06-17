package de.hsbo.veki.aufgabe6;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SearchView searchView= (SearchView) findViewById(R.id.search_view);
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            requestQueue(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       // requestQueue();
    }


    public void requestQueue(String city) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://geoapi-kswe2016.rhcloud.com/api/germany/" + city;


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        getWeatherData(response);


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        LatLng bochum = new LatLng(51.48, 7.12);
                        mMap.addMarker(new MarkerOptions().position(bochum).title("Did Not work"));
                    }
                });

// Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }


    public void getWeatherData(JSONObject jsonObject) {

        try {
            JSONObject locationJSON = jsonObject.getJSONObject("location");
            JSONObject weather = jsonObject.getJSONObject("weather");


            LatLng location = new LatLng(locationJSON.getDouble("latitude"), locationJSON.getDouble("longitude"));
            int temp = weather.getJSONObject("temperature").getInt("value") - 273;

            mMap.addMarker(new MarkerOptions().position(location).title(locationJSON.getString("city") + ", " + locationJSON.getString("country")).snippet(weather.getString("description") + ", " + temp + " C°").icon(BitmapDescriptorFactory.fromResource(setWeatherIcon(weather.getString("description"))))).showInfoWindow();

            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public int setWeatherIcon(String description){
        Integer icon = null;
        switch (description) {
            case "clear sky":
                icon = R.drawable.i01d;
                break;

            case "few clouds":
                icon = R.drawable.i02d;
                break;

            case "scattered clouds":
                icon = R.drawable.i03d;
                break;

            case "broken clouds":
                icon = R.drawable.i04d;
                break;

            case "shower rain":
                icon = R.drawable.i09d;
                break;

            case "rain":
                icon = R.drawable.i10d;
                break;

            case "thunderstorm":
                icon = R.drawable.i11d;
                break;

            case "snow":
                icon = R.drawable.i13d;
                break;

            case "mist":
                icon = R.drawable.i50d;
                break;

            default:
                break;
        }
        if(icon==null) {
            if (description.contains("rain")) {
                icon = R.drawable.i10d;
            }
            else if(description.contains("clouds")){
                icon = R.drawable.i04d;
            }
        }

        return  icon;

    }

}


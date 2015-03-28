package com.msd.routeinfo;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        //39.756782, -84.079473

        Criteria criteria = new Criteria();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        double lat =  location.getLatitude();
        double lng = location.getLongitude();
        LatLng coordinate = new LatLng(lat, lng);

        String post = "";
        Geocoder code=new Geocoder(MapsActivity.this);
        try
        {
            List<Address> addresses = code.getFromLocation(lat,lng, 1);
            if(addresses.size() >0)
            {
                Address add=new Address(Locale.getDefault());
                add=addresses.get(0);
                post= add.getAddressLine(0)+", "+add.getAddressLine(1);
            }
        }
        catch(Exception ex)
        {
            Log.d("Error", ex.getMessage());
        }

        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(""+post));
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);


        LatLng l1=new LatLng(39.756782, -84.079473);
        LatLng l2=new LatLng(39.1000, 84.5167);

        getDocument gd=new getDocument();
       gd.execute();
    }




    private class getDocument extends AsyncTask<Void,Void,Void>{//(LatLng start, LatLng end, String mode) {

        @Override
        protected Void doInBackground(Void... voids) {
        LatLng l1=new LatLng(39.756782, -84.079473);
        LatLng l2=new LatLng(39.1000, -84.5167);

        String url = "http://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + l1.latitude + "," + l1.longitude
                + "&destination=" + l2.latitude + "," + l2.longitude
                + "&sensor=false&units=miles&mode=driving";
           //     +"&key=AIzaSyCKNov_-AGmJgzewm4bYog-byOaCFBZxsE";
//        Log.d("url", url);
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            String result = EntityUtils.toString(response.getEntity());
            //  InputStream in = response.getEntity().getContent();
            Log.e("r","Response "+result);
          //  JSONArray ja = new JSONArray(result);
            JSONObject jo=new JSONObject(result);
            int n = jo.length();
            Log.e("r","Result Response: "+result);
       //     for (int i = 0; i < n; i++) {
                // GET INDIVIDUAL JSON OBJECT FROM JSON ARRAY
              //  JSONObject jo = ja.getJSONObject(i);

                // RETRIEVE EACH JSON OBJECT'S FIELDS
                JSONArray obj;
                obj = jo.getJSONArray("routes");
                jo=obj.getJSONObject(0);
                obj=jo.getJSONArray("legs");
                jo=obj.getJSONObject(0);
                String tmp=jo.getString("end_address");
                Log.e("end",tmp);

/*                String tmp=
                for (int j = 0; j <obj.length() ; j++) {
                    Log.e("hii",obj.);
                }*/

                // CONVERT DATA FIELDS TO CLUB OBJECT
        //    }


            // 39.1000, 84.5167

        } catch (Exception e) {
            System.out.println("Exception mine: "+e);
        }


            return null;
        }
    }
}

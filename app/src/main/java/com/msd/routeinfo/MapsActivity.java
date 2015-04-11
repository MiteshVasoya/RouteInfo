package com.msd.routeinfo;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Polyline polyline=null;
    static double src_lat,src_lng;
    static LatLngBounds.Builder bounds;
    EditText source_et,destination_et;
    Button source_cancel,destination_cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        source_et = (EditText)findViewById(R.id.source);
        source_cancel = (Button)findViewById(R.id.source_cancel);
        source_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source_et.setText("");
            }
        });
        destination_et = (EditText)findViewById(R.id.destination);
        destination_cancel = (Button)findViewById(R.id.destination_cancel);
        destination_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destination_et.setText("");
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
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
        src_lat =  location.getLatitude();
        src_lng = location.getLongitude();
        LatLng coordinate = new LatLng(src_lat, src_lng);

        String post = "";
        Geocoder code=new Geocoder(MapsActivity.this);
        try
        {
            List<Address> addresses = code.getFromLocation(src_lat, src_lng, 1);
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

        mMap.addMarker(new MarkerOptions().position(new LatLng(src_lat, src_lng)).title(""+post));

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(src_lat, src_lng))      // Sets the center of the map to Mountain View
                .zoom(10)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();

              mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        //mMap.animateCamera(cameraUpdate);

     /*   PolylineOptions rectOptions = new PolylineOptions()
                .add(l1)
                .add(l2)  // North of the previous point, but at the same longitude
                    ; // Closes the polyline.

// Get back the mutable Polyline

        polyline = mMap.addPolyline(rectOptions);
        */
        GetDocument gd=new GetDocument();
        gd.execute();

    }




    private class GetDocument extends AsyncTask<Void,Void,Void>{//(LatLng start, LatLng end, String mode) {

        ArrayList<LatLng> source=new ArrayList<LatLng>();
        ArrayList<LatLng> destination=new ArrayList<LatLng>();
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
               Log.d("","Path size"+source.size());
             for(int i=0;i<source.size();i++) {
                 polyline = mMap.addPolyline(new PolylineOptions()
                         .add(source.get(i),
                                 destination.get(i))
                         .width(20).color(Color.BLUE).geodesic(true));
             }


            //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150));
            //LatLngBounds latLngBounds = new LatLngBounds(new LatLng(src_lat, src_lng), new LatLng(39.1000, -84.5167));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));

        }
        @Override
        protected Void doInBackground(Void... voids) {
        LatLng l1=new LatLng(src_lat, src_lng);
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
            JSONObject routes = obj.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);

                source.add(src);
                destination.add(dest);

            }

                // CONVERT DATA FIELDS TO CLUB OBJECT
        //    }


            // 39.1000, 84.5167

        } catch (Exception e) {
            System.out.println("Exception mine: "+e);
        }


            return null;
        }





        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }
}

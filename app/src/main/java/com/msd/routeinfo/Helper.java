package com.msd.routeinfo;

import android.location.Geocoder;
import android.util.Log;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.location.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snehitgajjar on 3/27/15.
 */
public class Helper {



   // String str="https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&avoid=highways&mode=bicycling&key=API_KEY\n";

    public static void getDocument(LatLng start, LatLng end, String mode) {

        String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=driving"
                +"&key=AIzaSyCKNov_-AGmJgzewm4bYog-byOaCFBZxsE";
//        Log.d("url", url);
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            String result = EntityUtils.toString(response.getEntity());
          //  InputStream in = response.getEntity().getContent();

            JSONArray ja = new JSONArray(result);
            int n = ja.length();
            for (int i = 0; i < n; i++) {
                // GET INDIVIDUAL JSON OBJECT FROM JSON ARRAY
                JSONObject jo = ja.getJSONObject(i);

                // RETRIEVE EACH JSON OBJECT'S FIELDS
               String tmp[]= (String[]) jo.get("legs");
                for (int j = 0; j <tmp.length ; j++) {
                   System.out.println("hii"+tmp[i]);
                }

                // CONVERT DATA FIELDS TO CLUB OBJECT
            }


            // 39.1000, 84.5167

        } catch (Exception e) {
           System.out.println("Exception mine: "+e);
        }

    }

    public static void main(String arg[])
    {
        System.out.println("Start");
        LatLng l1=new LatLng(39.756782, -84.079473);
        LatLng l2=new LatLng(39.1000, 84.5167);

        getDocument(l1,l2,"Driving");

    }


    public String getDurationText(Document doc) {
        try {

            NodeList nl1 = doc.getElementsByTagName("duration");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "text"));
            Log.i("DurationText", node2.getTextContent());
            return node2.getTextContent();
        } catch (Exception e) {
            return "0";
        }
    }

    public int getDurationValue(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("duration");
            Node node1 = nl1.item(0);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.i("DurationValue", node2.getTextContent());
            return Integer.parseInt(node2.getTextContent());
        } catch (Exception e) {
            return -1;
        }
    }

    public String getDistanceText(Document doc) {
    /*
     * while (en.hasMoreElements()) { type type = (type) en.nextElement();
     *
     * }
     */

        try {
            NodeList nl1;
            nl1 = doc.getElementsByTagName("distance");

            Node node1 = nl1.item(nl1.getLength() - 1);
            NodeList nl2 = null;
            nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.d("DistanceText", node2.getTextContent());
            return node2.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    /*
     * NodeList nl1; if(doc.getElementsByTagName("distance")!=null){ nl1=
     * doc.getElementsByTagName("distance");
     *
     * Node node1 = nl1.item(nl1.getLength() - 1); NodeList nl2 = null; if
     * (node1.getChildNodes() != null) { nl2 = node1.getChildNodes(); Node
     * node2 = nl2.item(getNodeIndex(nl2, "value")); Log.d("DistanceText",
     * node2.getTextContent()); return node2.getTextContent(); } else return
     * "-1";} else return "-1";
     */
    }

    public int getDistanceValue(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("distance");
            Node node1 = null;
            node1 = nl1.item(nl1.getLength() - 1);
            NodeList nl2 = node1.getChildNodes();
            Node node2 = nl2.item(getNodeIndex(nl2, "value"));
            Log.i("DistanceValue", node2.getTextContent());
            return Integer.parseInt(node2.getTextContent());
        } catch (Exception e) {
            return -1;
        }
    /*
     * NodeList nl1 = doc.getElementsByTagName("distance"); Node node1 =
     * null; if (nl1.getLength() > 0) node1 = nl1.item(nl1.getLength() - 1);
     * if (node1 != null) { NodeList nl2 = node1.getChildNodes(); Node node2
     * = nl2.item(getNodeIndex(nl2, "value")); Log.i("DistanceValue",
     * node2.getTextContent()); return
     * Integer.parseInt(node2.getTextContent()); } else return 0;
     */
    }

    public String getStartAddress(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("start_address");
            Node node1 = nl1.item(0);
            Log.i("StartAddress", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    }

    public String getEndAddress(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("end_address");
            Node node1 = nl1.item(0);
            Log.i("StartAddress", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }
    }
    public String getCopyRights(Document doc) {
        try {
            NodeList nl1 = doc.getElementsByTagName("copyrights");
            Node node1 = nl1.item(0);
            Log.i("CopyRights", node1.getTextContent());
            return node1.getTextContent();
        } catch (Exception e) {
            return "-1";
        }

    }

    public ArrayList<LatLng> getDirection(Document doc) {
        NodeList nl1, nl2, nl3;
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
        nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Node node1 = nl1.item(i);
                nl2 = node1.getChildNodes();

                Node locationNode = nl2
                        .item(getNodeIndex(nl2, "start_location"));
                nl3 = locationNode.getChildNodes();
                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                double lat = Double.parseDouble(latNode.getTextContent());
                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                double lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));

                locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "points"));
                ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
                for (int j = 0; j < arr.size(); j++) {
                    listGeopoints.add(new LatLng(arr.get(j).latitude, arr
                            .get(j).longitude));
                }

                locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "lat"));
                lat = Double.parseDouble(latNode.getTextContent());
                lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));
            }
        }

        return listGeopoints;
    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
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

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }


    /**
     *  convert address into latitude and longitude.
     */




}

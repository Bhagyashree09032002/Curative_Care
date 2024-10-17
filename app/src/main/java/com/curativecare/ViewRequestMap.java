package com.curativecare;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ViewRequestMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;

    static List<PlaceObject> allplaces = new ArrayList<>();
    public static List tempo1;
    public static int size = 0;

    static Marker markers[] = new Marker[2];

    public static final int MY_SOCKET_TIMEOUT_MS = 5000;
    public static List<LatLng> tempo;

    static String waypoints = "";
    LatLngBounds routebounds;
    static int counter = 0;
    String sensor = "sensor=false";

    public static String getLocURL = IPaddress.ip + "getLocation.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // User Request Location

        LatLng loc = new LatLng(UserData.reqlat, UserData.reqlon);
        mMap.addMarker(new MarkerOptions().position(loc).title("User Request Location"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(loc)
                .zoom(18)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // Hospital Location


        MarkerOptions markerOptions = new MarkerOptions();

        LatLng loc1 = new LatLng(UserData.dlat, UserData.dlon);
        markerOptions.position(loc1);

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMap.addMarker(markerOptions.position(loc1).title("Hospital Location"));

        CameraPosition cameraPosition1 = new CameraPosition.Builder()
                .target(loc1)
                .zoom(18)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));

        // Driver Current Location

        LatLng loc2 = new LatLng(Double.valueOf(UserData.slat), Double.valueOf(UserData.slon));
        markerOptions.position(loc2);

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        mMap.addMarker(markerOptions.position(loc2).title("Driver Current Location"));

        CameraPosition cameraPosition2 = new CameraPosition.Builder()
                .target(loc2)
                .zoom(18)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));


        String directionApiPath = "https://maps.googleapis.com/maps/api/directions/json?origin="+UserData.slat+","+UserData.slon+"&destination="+UserData.reqlat+","+UserData.reqlon+"&key=AIzaSyC94Rkax10-74mEZTVj1EVmB1m7osUfbUs";
        getDirectionFromDirectionApiServer(directionApiPath);

        String directionApiPath1 = "https://maps.googleapis.com/maps/api/directions/json?origin="+UserData.reqlat+","+UserData.reqlon+"&destination="+UserData.dlat+","+UserData.dlon+"&key=AIzaSyC94Rkax10-74mEZTVj1EVmB1m7osUfbUs";
        getDirectionFromDirectionApiServer(directionApiPath1);

        //waypoints
        /*String directionApiPath = "https://maps.googleapis.com/maps/api/directions/json?origin="+String.valueOf(defaultLocation.latitude)+","+String.valueOf(defaultLocation.longitude)+"&destination="+String.valueOf(destinationLocation.latitude)+","+String.valueOf(destinationLocation.longitude);
        getDirectionFromDirectionApiServer(directionApiPath);*/

    }

    private void getDirectionFromDirectionApiServer(String url){
        GsonRequest<DirectionObject> serverRequest = new GsonRequest<DirectionObject>(
                Request.Method.GET,
                url,
                DirectionObject.class,
                createRequestSuccessListener(),
                createRequestErrorListener());
        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(serverRequest);
    }
    private Response.Listener<DirectionObject> createRequestSuccessListener() {
        return new Response.Listener<DirectionObject>() {
            @Override
            public void onResponse(DirectionObject response) {
                try {
                    Log.d("JSON Response", response.toString());
                    if(response.getStatus().equals("OK")){
                        List<LatLng> mDirections = getDirectionPolylines(response.getRoutes());
                        drawRouteOnMap(mMap, mDirections);
                    }else{
                        //Toast.makeText(ViewRequestMap.this, "Server error occured!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }
    private List<LatLng> getDirectionPolylines(List<RouteObject> routes){
        List<LatLng> directionList = new ArrayList<LatLng>();
        for(RouteObject route : routes){
            List<LegsObject> legs = route.getLegs();
            for(LegsObject leg : legs){
                List<StepsObject> steps = leg.getSteps();
                for(StepsObject step : steps){
                    PolylineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    for (LatLng direction : singlePolyline){
                        directionList.add(direction);
                    }
                }
            }
        }
        size = directionList.size();
        tempo = new ArrayList<>();
        tempo1 = new ArrayList();
        tempo = directionList;
        //distu = getSignals(directionList);
        System.out.print("Directions list ===== "+String.valueOf(directionList));
        return directionList;
    }
    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
    }
    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions){
        PolylineOptions options = new PolylineOptions().width(8).color(Color.BLUE).geodesic(true);
        options.addAll(positions);
        Polyline polyline = map.addPolyline(options);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(positions.get(1).latitude, positions.get(1).longitude))
                .zoom(18)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
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


    /* public void getLocation() {

        RequestParams params = new RequestParams();

        final ProgressDialog pDialog = ProgressDialog.show(ViewRequestMap.this, "Processing", "fetching locations...", true, false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getLocURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pDialog.dismiss();
                String res = new String(responseBody);
                try {
                    JSONObject o = new JSONObject(res);
                    if (o.getString("success").equals("200")) {
                        allplaces.clear();
                        JSONArray a = o.getJSONArray("allplaces");
                        for (int i = 0; i < a.length(); i++) {
                            PlaceObject po = new PlaceObject();
                            JSONObject obj = a.getJSONObject(i);
                            //po.name = obj.getString("name");
                            po.lat = Double.parseDouble(obj.getString("lat"));
                            po.lon = Double.parseDouble(obj.getString("lon"));
                            //po.pollution = Float.parseFloat(obj.getString("pollution"));
                            allplaces.add(po);
                        }
                        displayOnMap();
                    } else {
                        Toast.makeText(ViewRequestMap.this, res, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ViewRequestMap.this, res, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.dismiss();
            }
        });
    }*/


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }
}

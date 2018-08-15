package com.progandroid.map;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.progandroid.foodordering.R;
import com.progandroid.foodordering.network.GsonRequest;
import com.progandroid.foodordering.network.VolleySingleton;
import com.progandroid.foodordering.util.Helper;
import com.progandroid.map.beans.Constant;
import com.progandroid.map.beans.DirectionObject;
import com.progandroid.map.beans.LegsObject;
import com.progandroid.map.beans.PolylineObject;
import com.progandroid.map.beans.RouteObject;
import com.progandroid.map.beans.StepsObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderTrackingActivity extends FragmentActivity implements OnMapReadyCallback{

    private static final String TAG = OrderTrackingActivity.class.getSimpleName();

    private GoogleMap mMap;

    private TextView distanceValue;
    private TextView durationValue;
    private TextView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        distanceValue = (TextView)findViewById(R.id.order_distance);
        durationValue = (TextView)findViewById(R.id.order_duration);
        timer = (TextView)findViewById(R.id.timer);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng restaurant = new LatLng(55.851903, 13.660413);
        mMap.addMarker(new MarkerOptions().position(restaurant).title("Restaurant"));
        addCameraToMap(restaurant);

        LatLng  user = getUserLocationFromAddress("Nygatan 11 24231 horby, Sweden");
        //mMap.addMarker(new MarkerOptions().position(user).title("Delivery Address"));
        createMarker(user);

        String directionPath = Constant.getUrl(String.valueOf(restaurant.latitude), String.valueOf(restaurant.longitude),
                String.valueOf(user.latitude), String.valueOf(user.longitude));
        getDirectionFromDirectionApiServer(directionPath);
    }

    private void createMarker(LatLng latLng){
        MarkerOptions mOptions = new MarkerOptions().position(latLng);
        mOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        addCameraToMap(latLng);
        mMap.addMarker(mOptions);
    }

    private void addCameraToMap(LatLng latLng){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(16)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void getDirectionFromDirectionApiServer(String url){
        GsonRequest<DirectionObject> serverRequest = new GsonRequest<DirectionObject>(
                Request.Method.GET,
                url,
                DirectionObject.class,
                createRequestSuccessListener(),
                createRequestErrorListener());

        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
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
                        Toast.makeText(OrderTrackingActivity.this, "Server error: could not get direction", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    private List<LatLng> getDirectionPolylines(List<RouteObject> routes){
        List<LatLng> directionList = new ArrayList<LatLng>();
        for(RouteObject route : routes){
            List<LegsObject> legs = route.getLegs();

            for(LegsObject leg : legs){
                String routeDistance = leg.getDistance().getText();
                String routeDuration = leg.getDuration().getText();
                setRouteDistanceAndDuration(routeDistance, routeDuration);
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
        return directionList;
    }

    private LatLng getUserLocationFromAddress(String userAddress){
        Geocoder coder = new Geocoder(this);
        LatLng location = null;
        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(userAddress, 50);
            for(Address add : adresses){
                if (add != null) {//Controls to ensure it is right address such as country etc.
                    double longitude = add.getLongitude();
                    double latitude = add.getLatitude();
                    location = new LatLng(latitude, longitude);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

    private void setRouteDistanceAndDuration(String distance, String duration){
        distanceValue.setText("DISTANCE: " + distance);
        durationValue.setText("DURATION: " + duration);
        timer.setText(duration);
    }

    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions){
        PolylineOptions options = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
        options.addAll(positions);
        Polyline polyline = map.addPolyline(options);
    }

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
}

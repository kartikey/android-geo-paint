package edu.uw.kartikey.geopaint;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    Polyline line;
    PolylineOptions polylineOptions;


    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "Location_Activity";
    private static final int LOC_REQUEST_CODE = 1;
    boolean penDown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient =
                    new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
        }









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

        // Add a marker in Sydney and move the camera
        LatLng seattle = new LatLng(47.6550, -122.3080);
        mMap.addMarker(new MarkerOptions().position(seattle).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seattle));

        googleMap.getUiSettings().setZoomControlsEnabled(true);



    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    /** Helper method for getting location **/
    public Location getLocation(View v){

        Location loc=null;
        try {
            loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        }catch (SecurityException se) {
            Log.v(TAG,"No location permission");
        }

        return loc;
    }

    @Override
    public void onConnected(Bundle bundle) {
        //when API has connected!
        getLocation(null);

        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permission == PackageManager.PERMISSION_GRANTED){
            //yay! Have permission, do the thing
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
        }
        else{
            //if(ActivityCompat.shouldShowRequestPermissionRationale(...))

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOC_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case LOC_REQUEST_CODE: { //if asked for location
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    onConnected(null); //should work :/
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.v(TAG, "latitude = " + location.getLatitude());
        Log.v(TAG, "longitute = " + location.getLongitude());

        LatLng newLatLong = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(newLatLong).title("I am here!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLong));

        if(penDown) {
            List<LatLng> points = line.getPoints();
            points.add(newLatLong);
            line.setPoints(points);
        }

        //googleMap.getUiSettings().setZoomControlsEnabled(true);


    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.togglePen:
                togglePen();
                return true;
            case R.id.colorPicker:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void togglePen() {
        penDown = !penDown;

        if(penDown) {
            Location  loc = null;
            try {
                  loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }catch (SecurityException se) {
                Log.v(TAG,"Location Error in toggle");
            }

            if(loc != null) {
                line = mMap.addPolyline(new PolylineOptions().add(new LatLng(loc.getLatitude(),loc.getLongitude()))
                        .color(Color.BLACK));
            }else {
                line = mMap.addPolyline(new PolylineOptions()
                        .color(Color.BLACK));
            }

        }

        String penText = penDown ? "down" : "up";

        Toast.makeText(this,"Pen is now "+penText,Toast.LENGTH_SHORT).show();
    }

}// end of class

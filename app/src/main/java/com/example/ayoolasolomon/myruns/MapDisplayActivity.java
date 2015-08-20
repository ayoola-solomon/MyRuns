package com.example.ayoolasolomon.myruns;

import android.content.IntentSender;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapDisplayActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

  private GoogleMap mMap;
  private GoogleApiClient mGoogleApiClient;
  private LocationRequest mLocationRequest;

  public static final String TAG = "Map";

  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map_display);

    setUpMapIfNeeded();

    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();

    mLocationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(10 * 1000)
        .setFastestInterval(1 * 1000);

    String activityType = getIntent().getStringExtra("activity");
    TextView activity = (TextView) findViewById(R.id.type_stats);
    activity.setText(activityType);
  }

  private void setUpMapIfNeeded() {
    // Do a null check to confirm that we have not already instantiated the map.
    if (mMap == null) {
      // Try to obtain the map from the SupportMapFragment.
      mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
          .getMap();
      // Check if we were successful in obtaining the map.
      if (mMap != null) {
//        mMap.setOnMapClickListener(this);
//        mMap.setOnMapLongClickListener(this);
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    mGoogleApiClient.connect();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mGoogleApiClient.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
      mGoogleApiClient.disconnect();
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    Log.i(TAG, "Location services connected.");

    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    if (location == null) {
      LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    } else {
      handleNewLocation(location);
    }
  }

  private void handleNewLocation(Location location) {
    Log.d(TAG, location.toString());

    double currentLatitude = location.getLatitude();
    double currentLongitude = location.getLongitude();
    LatLng latLng = new LatLng(currentLatitude, currentLongitude);

    MarkerOptions options = new MarkerOptions()
        .position(latLng)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
    mMap.addMarker(options);
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
  }

  @Override
  public void onConnectionSuspended(int i) {
    Log.i(TAG, "Location services suspended. Please reconnect.");
  }

  @Override
  public void onLocationChanged(Location location) {
    handleNewLocation(location);
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    if (connectionResult.hasResolution()) {
      try {
        connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
      } catch (IntentSender.SendIntentException e) {
        e.printStackTrace();
      }
    }
  }

  public void saveBtn(View view) {
    finish();
  }

  public void cancelMap(View view) {
    finish();
  }
}

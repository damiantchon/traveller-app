package tim.project.travellerapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import tim.project.travellerapp.R;

public class AddNewPlaceMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    private LocationManager mLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        checkForPermisions();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            LatLng warsaw = new LatLng(52.2297, 21.0122);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warsaw, 15f));

        } else {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean gpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

//            if (gpsOn) {
//                Criteria criteria = new Criteria();
//                String provider = locationManager.getBestProvider(criteria, true);
                Location location = getLastKnownLocation();

                if (location != null){
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    LatLng currentLocation = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                }
                else {
                    LatLng centerOfPoland = new LatLng(52.11416667, 19.42361111);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerOfPoland, 5.6f));
                }

//            } else {
//                LatLng centerOfPoland = new LatLng(52.11416667, 19.42361111);
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerOfPoland, 5.6f));
//            }

        }

        mMap.setOnMapLongClickListener(this);
    }

    public void checkForPermisions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permisions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "If you want to show your current location, you need to enable Location permission!", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permisions, 1);
            }
            return;
        } else {

            // Permisions granted
            mMap.setMyLocationEnabled(true);

            Location location = getLastKnownLocation();

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
            } else {

            }
            // Add a marker in Sydney and move the camera
        }
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    recreate();
                }
                else {
                    Toast.makeText(this, "If you want to show your current location, you need to enable Location permission!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), AddNewPlaceActivity.class);
                intent.putExtra(getString(R.string.ADD_NEW_PLACE_LATITUDE), latLng.latitude);
                intent.putExtra(getString(R.string.ADD_NEW_PLACE_LONGITUDE), latLng.longitude);
                startActivity(intent);
                marker.remove();
            }
        }, 700);

    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
    }

}

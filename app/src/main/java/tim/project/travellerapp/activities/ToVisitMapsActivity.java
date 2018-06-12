package tim.project.travellerapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tim.project.travellerapp.Constants;
import tim.project.travellerapp.R;
import tim.project.travellerapp.clients.ApiClient;
import tim.project.travellerapp.helpers.ApiHelper;
import tim.project.travellerapp.helpers.GpsHelper;
import tim.project.travellerapp.models.Place;

public class ToVisitMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double[] mGps;

    private List<Place> placeList;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.fab1)
    FloatingActionButton fab1;

    @BindView(R.id.fab2)
    FloatingActionButton fab2;

    @BindView(R.id.fab3)
    FloatingActionButton fab3;

    boolean isFabOpen = false;

    boolean isOpeningNavigation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_visit_maps);
        ButterKnife.bind(this);

        fab1.setAlpha(0f);
        fab2.setAlpha(0f);
        fab3.setAlpha(0f);

        fab1.animate().translationY(230).setDuration(30).alpha(0);
        fab2.animate().translationY(160).setDuration(30).alpha(0);
        fab3.animate().translationY(90).setDuration(30).alpha(0);

        fab1.setEnabled(false);
        fab2.setEnabled(false);
        fab3.setEnabled(false);



        fab.setOnClickListener(v -> {
            if(!isFabOpen) {
                Toast.makeText(this, "Test!", Toast.LENGTH_SHORT).show();

                showFabMenu();
            } else {
                closeFabMenu();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void showFabMenu(){
        isFabOpen = true;
        fab1.animate().translationY(0).setDuration(300).alpha(1);
        fab1.setEnabled(true);
        fab2.animate().translationY(0).setDuration(300).alpha(1);
        fab2.setEnabled(true);
        fab3.animate().translationY(0).setDuration(300).alpha(1);
        fab3.setEnabled(true);
        fab.animate().alpha(0.5f);
    }

    private void closeFabMenu(){
        isFabOpen=false;
        fab1.animate().translationY(230).setDuration(300).alpha(0);
        fab1.setEnabled(false);
        fab2.animate().translationY(160).setDuration(300).alpha(0);
        fab2.setEnabled(false);
        fab3.animate().translationY(90).setDuration(300).alpha(0);
        fab3.setEnabled(false);
        fab.animate().alpha(1f);
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

        ApiClient client = ApiHelper.getApiClient();

        String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Token", null);

        // W przypadku uruchomienia aktywności z dodatkową wartością "LOKALIZACJA"
        if (this.getIntent().getStringExtra(getString(R.string.PLACE_TO_VISIT_LOCALISATION)) != null) {
            String title = this.getIntent().getStringExtra(getString(R.string.PLACE_TO_VISIT_TITLE));
            String gps = this.getIntent().getStringExtra(getString(R.string.PLACE_TO_VISIT_LOCALISATION));

            double[] decodedGps = GpsHelper.decodeStringGps(gps);
            final LatLng GPS =  new LatLng(decodedGps[0], decodedGps[1]);
            mGps = new double[]{decodedGps[0], decodedGps[1]};
            Marker marker = mMap.addMarker(new MarkerOptions().position(GPS).title(title));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GPS, 15F));
            mMap.getUiSettings().setMapToolbarEnabled(false);
            marker.showInfoWindow();


        } else {

            Call<List<Place>> call = client.getActivePlaces(token);

            call.enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(@NonNull Call<List<Place>> call, @NonNull Response<List<Place>> response) {
                    if (response.code() == 200) {

                        placeList = response.body();
                        placeList = new ArrayList<>(placeList);

                        for (Place place : placeList) {
                            String[] splitGps = place.getGps().split(",");
                            LatLng latLng = new LatLng(Double.parseDouble(splitGps[0]), Double.parseDouble(splitGps[1]));
                            mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                        }

                    } else {
                        Toast.makeText(ToVisitMapsActivity.this, "No failure but still shitty response :(", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Place>> call, @NonNull Throwable t) {
                    Toast.makeText(ToVisitMapsActivity.this, "Something went wrong! :(", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onDriveClick(View view) {
        onNavigateClick("d");
    }
    public void onBikeClick(View view) {
        onNavigateClick("b");
    }

    public void onWalkClick(View view) {
        onNavigateClick("w");
    }

    public void onNavigateClick(String mode) {
//        String strUri = "geo:0,0?q=";
        String strUri = "google.navigation:q=";
        strUri = strUri.concat(String.valueOf(mGps[0]) + "," + String.valueOf(mGps[1]));
        //String mode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("TRAVELING_METHOD", "d");
        strUri = strUri.concat("&mode=" + mode);

        Uri gmmIntentUri = Uri.parse(strUri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        isOpeningNavigation = true;
        finish();

    }

    public void finish() {
        super.finish();
        if(!isOpeningNavigation) {
            overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
        }
    }
}

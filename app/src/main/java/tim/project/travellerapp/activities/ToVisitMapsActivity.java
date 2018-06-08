package tim.project.travellerapp.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tim.project.travellerapp.Constants;
import tim.project.travellerapp.R;
import tim.project.travellerapp.clients.ApiClient;
import tim.project.travellerapp.models.Place;

public class ToVisitMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private List<Place> placeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_visit_maps);
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

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        ApiClient client = retrofit.create(ApiClient.class);
        String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Token", null);

        Call<List<Place>> call = client.getActivePlaces(token);

        call.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(@NonNull Call<List<Place>> call, @NonNull Response<List<Place>> response) {
                if (response.code() == 200) {

                    placeList = response.body();
                    placeList = new ArrayList<>(placeList);

                    for (Place place: placeList) {
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



        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}

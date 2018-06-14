package tim.project.travellerapp.activities;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tim.project.travellerapp.R;
import tim.project.travellerapp.clients.ApiClient;
import tim.project.travellerapp.helpers.ApiHelper;
import tim.project.travellerapp.helpers.GpsHelper;
import tim.project.travellerapp.models.request_body_models.NewPlace;

public class AddNewPlaceActivity extends AppCompatActivity {

    @BindView(R.id.new_place_name)
    EditText nameEditText;

    @BindView(R.id.new_place_address)
    EditText addressEditText;

    @BindView(R.id.new_place_city)
    EditText cityEditText;

    @BindView(R.id.new_place_description)
    EditText descriptionEditText;

    @BindView(R.id.new_place_latitude)
    EditText latitudeEditText;

    @BindView(R.id.new_place_longitude)
    EditText longitudeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        toolbar.setTitle(R.string.add_new_place_toolbar_title);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();

        Double latitude = extras.getDouble(getString(R.string.ADD_NEW_PLACE_LATITUDE));
        Double longitude = extras.getDouble(getString(R.string.ADD_NEW_PLACE_LONGITUDE));

        latitudeEditText.setText(String.valueOf(latitude));
        longitudeEditText.setText(String.valueOf(longitude));

        Geocoder geoCoder = new Geocoder(getApplicationContext());
        List<Address> matches = null;
        try {
            matches = geoCoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address bestMatch = (matches.isEmpty() ? null : matches.get(0));

        String street = bestMatch.getThoroughfare();
        String city = bestMatch.getLocality();

        if (street == null) street = "";
        //if (postalCode == null) postalCode = "POSTAL_CODE";
        if (city == null) city = "";

        addressEditText.setText(street);
        cityEditText.setText(city);

    }

    public void onAddClick(View view) {
        String name = nameEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String latitude = latitudeEditText.getText().toString();
        String longitude = longitudeEditText.getText().toString();

        int result = checkInputs(name, address, city, latitude, longitude);

        switch (result) {
            case 1: // Name empty
                Toast.makeText(this, getString(R.string.empty_name_information), Toast.LENGTH_SHORT).show();
                break;
            case 2: // Address empty
                Toast.makeText(this, getString(R.string.empty_adress_information), Toast.LENGTH_SHORT).show();
                break;
            case 3: // Latitude must be between - 90 and 90
                Toast.makeText(this, getString(R.string.latitude_information), Toast.LENGTH_SHORT).show();
                break;
            case 4: // Longitude must be between - 180 and 180
                Toast.makeText(this, getString(R.string.longitude_information), Toast.LENGTH_SHORT).show();
                break;
            case 0: // Inputs correct

                String combinedAdress = "";
                combinedAdress = combinedAdress.concat(address).concat(", ").concat(city);

                String gps = GpsHelper.encodeStringLatLng(latitude, longitude);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Long userId = preferences.getLong("UserId", 0L);
                String token = preferences.getString("Token", null);

                NewPlace newPlace = new NewPlace(name, combinedAdress, gps, description, userId);

                ApiClient client = ApiHelper.getApiClient();
                Call<Void> call = client.addNewPlace(newPlace, token);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(AddNewPlaceActivity.this, getString(R.string.new_place_added_succesfully), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddNewPlaceActivity.this, "Token expired - please restart application!" + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AddNewPlaceActivity.this, getString(R.string.no_server_connection), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private int checkInputs(String name, String address, String city, String latitude, String longitude) {

        double latitudeDouble = Double.parseDouble(latitude);
        double longitudeDouble = Double.parseDouble(longitude);

        if(name.length() == 0) return 1;
        else if(address.length() == 0) return 2;
        else if(city.length() == 0) return 2;
        else if(latitudeDouble > 90 || latitudeDouble < -90) return 3;
        else if(longitudeDouble > 180 || latitudeDouble < -180) return 4;
        else return 0;
    }
}

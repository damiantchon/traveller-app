package tim.project.travellerapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tim.project.travellerapp.Constants;
import tim.project.travellerapp.R;
import tim.project.travellerapp.clients.ApiClient;
import tim.project.travellerapp.models.UserDetails;

import static tim.project.travellerapp.helpers.AuthenticationHelper.clearSharedPreferences;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!preferences.getString("Token", "Empty").equals("Empty")) {
            tokenLogin(preferences.getLong("UserId", 0L), preferences.getString("Token", null));
        } else {
            gotoLogin();
        }

    }

    public void tokenLogin(Long userId, String token) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        ApiClient client = retrofit.create(ApiClient.class);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Call<UserDetails> call =  client.getUserDetails(userId, token);

        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(@NonNull Call<UserDetails> call, @NonNull Response<UserDetails> response) {
                if (response.code() == 200) {
                    Toast.makeText(SplashActivity.this, "Logged in as " + preferences.getString("Username", "noone - that shouldn't be possible!"), Toast.LENGTH_SHORT).show();
                    gotoMain();
                } else {
                    gotoLogin();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDetails> call, @NonNull Throwable t) {
                //TODO Clear preference
                clearSharedPreferences(getApplicationContext());

                gotoLogin();
            }
        });
    }

    public void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAfterTransition();
    }

    public void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finishAfterTransition();
    }
}

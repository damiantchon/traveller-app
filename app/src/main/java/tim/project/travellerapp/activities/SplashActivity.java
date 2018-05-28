package tim.project.travellerapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import static tim.project.travellerapp.activities.LoginActivity.preferences;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferences = getApplicationContext().getSharedPreferences("Token",0);
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

        Call<UserDetails> call =  client.getUserDetails(userId, token);

        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.code() == 200) {
                    Toast.makeText(SplashActivity.this, "Logged in as " + preferences.getString("Username", "noone - that shouldn't be possible!"), Toast.LENGTH_SHORT).show();
                    gotoMain();
                } else {
                    gotoLogin();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Toast.makeText(SplashActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

package tim.project.travellerapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tim.project.travellerapp.R;
import tim.project.travellerapp.clients.ApiClient;
import tim.project.travellerapp.helpers.ApiHelper;
import tim.project.travellerapp.models.UserDetails;

import static tim.project.travellerapp.helpers.AuthenticationHelper.clearSharedPreferences;

public class SplashActivity extends AppCompatActivity {

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            final android.net.NetworkInfo wifi = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            final android.net.NetworkInfo mobile = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi.isAvailable() || mobile.isAvailable()) {
                setContentView(R.layout.activity_splash);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (!preferences.getString("Token", "Empty").equals("Empty")) {
                    tokenLogin(preferences.getLong("UserId", 0L), preferences.getString("Token", null));
                }
            } else {
                Toast.makeText(context, "Not Avaliable", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        this.registerReceiver(mReceiver, filter);

        setContentView(R.layout.activity_splash);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!preferences.getString("Token", "Empty").equals("Empty")) {
            tokenLogin(preferences.getLong("UserId", 0L), preferences.getString("Token", null));
        } else {
            gotoLogin();
        }

    }

    public void tokenLogin(Long userId, String token) {

        ApiClient client = ApiHelper.getApiClient();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Call<UserDetails> call =  client.getUserDetails(userId, token);

        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(@NonNull Call<UserDetails> call, @NonNull Response<UserDetails> response) {
                if (response.code() == 200) {
                    Toast.makeText(SplashActivity.this, "Logged in as " + preferences.getString("Username", "noone - that shouldn't be possible!"), Toast.LENGTH_SHORT).show();
                    gotoMain();
                } else {
                    Toast.makeText(SplashActivity.this, "Token expired!", Toast.LENGTH_SHORT).show();
                    clearSharedPreferences(getApplicationContext());
                    gotoLogin();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDetails> call, @NonNull Throwable t) {
                //TODO Clear preference
                Toast.makeText(SplashActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                gotoLogin();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
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

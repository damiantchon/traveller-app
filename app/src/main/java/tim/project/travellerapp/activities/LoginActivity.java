package tim.project.travellerapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tim.project.travellerapp.Constants;
import tim.project.travellerapp.R;
import tim.project.travellerapp.clients.ApiClient;
import tim.project.travellerapp.models.User;
import tim.project.travellerapp.models.UserDetails;


public class LoginActivity extends AppCompatActivity {

    public static SharedPreferences preferences;

    private TextView login_token;

    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //GET PREFERENCES

        login_token = (TextView) findViewById(R.id.token_login);
        username = (EditText) findViewById(R.id.login_login);
        password = (EditText) findViewById(R.id.login_password);



        String insertMe = preferences.getString("Token", "Empty") + preferences.getLong("UserId", 0L) + preferences.getBoolean("Admin", false);
        login_token.setText(insertMe);
    }

    public void onLoginClick(View view) {

        User user = new User(
                username.getText().toString(),
                password.getText().toString()
        );

        if(user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            Toast.makeText(LoginActivity.this, R.string.login_empty, Toast.LENGTH_SHORT).show();
        } else {
            loginUser(user);
        }

    }

    public void loginUser(User user) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        ApiClient client = retrofit.create(ApiClient.class);

        Call<Void> call = client.loginUser(user);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200) {
                    //Toast.makeText(LoginActivity.this, R.string.login_correct, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(LoginActivity.this,  response.headers().get("Token"), Toast.LENGTH_SHORT).show();

                    //TODO Zapisać użytkownika!!!
                    SharedPreferences.Editor editor = preferences.edit();
                    String token = response.headers().get("Token");
                    Long userId = Long.parseLong(response.headers().get("UserId"));
                    Boolean admin = response.headers().get("Admin").equals("true");

                    editor.putString("Token", token);
                    editor.putLong("UserId", userId);
                    editor.putBoolean("Admin", admin);

                    editor.apply();

                    getAndSaveAdationalData(userId, token);

                    //gotoMain();
                } else if (response.code() == 401) {
                    Toast.makeText(LoginActivity.this, R.string.login_wrong_credentials, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAndSaveAdationalData(Long userId, String token) {

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
                    Toast.makeText(LoginActivity.this,response.body().getUsername(),Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = preferences.edit();

                    String username = response.body().getUsername();
                    Boolean active = response.body().getActive();
                    Long userRoleId = response.body().getUserRoleId();

                    editor.putString("Username", username);
                    editor.putBoolean("Active", active);
                    editor.putLong("UserRoleId", userRoleId);

                    editor.apply();

                    Toast.makeText(LoginActivity.this, preferences.getString("Username", "xD"),Toast.LENGTH_SHORT).show();


                    if (!active) {
                        Toast.makeText(LoginActivity.this, "Account not active FeelsBadMan",Toast.LENGTH_SHORT).show();
                    }
                    editor.apply();

                    gotoMain();


                } else if (response.code() == 403) {
                    Toast.makeText(LoginActivity.this, "FeelsBadMan",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Toast.makeText(LoginActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onGotoSignupClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAfterTransition();
    }

}

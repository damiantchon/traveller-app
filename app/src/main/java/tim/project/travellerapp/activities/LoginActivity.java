package tim.project.travellerapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tim.project.travellerapp.R;
import tim.project.travellerapp.clients.UserClient;
import tim.project.travellerapp.models.User;

import static tim.project.travellerapp.Constants.REST_API_ADDRESS;

public class LoginActivity extends Activity {

    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.login_login);
        password = (EditText) findViewById(R.id.login_password);
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
                .baseUrl(REST_API_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        UserClient client = retrofit.create(UserClient.class);

        Call<Void> call = client.loginUser(user);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200) {
                    Toast.makeText(LoginActivity.this, R.string.login_correct, Toast.LENGTH_SHORT).show();
                    //TODO Zapisać użytkownika!!!
                    gotoMain();
                } else if (response.code() == 403) {
                    Toast.makeText(LoginActivity.this, R.string.login_wrong_credentials, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, response.code(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
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
    }
}

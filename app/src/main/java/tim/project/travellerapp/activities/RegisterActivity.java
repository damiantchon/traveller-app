package tim.project.travellerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import tim.project.travellerapp.helpers.ApiHelper;
import tim.project.travellerapp.models.User;


public class RegisterActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private EditText username;
    private EditText password;
    private EditText re_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final TextView textView = (TextView) findViewById(R.id.textView2);

        username = (EditText) findViewById(R.id.input_login);
        password = (EditText) findViewById(R.id.input_password);
        re_password = (EditText) findViewById(R.id.input_re_password);

        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
    }

    public void onRegisterClick(View view) {

        User user = new User(
                username.getText().toString(),
                password.getText().toString()
        );
        if(!password.getText().toString().equals(re_password.getText().toString())) {
            Toast.makeText(RegisterActivity.this,"Passwords are not matching!",Toast.LENGTH_SHORT).show();
        }
        else{
            registerUser(user);
        }
    }

    public void onBackClick(View view) {
        finish();
    }

    private void registerUser(User user) {

        ApiClient client = ApiHelper.getApiClient();

        Call<Void> call = client.createAccount(user);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.code() == 409){
                    Toast.makeText(RegisterActivity.this, getString(R.string.user_duplicate),Toast.LENGTH_SHORT).show();
                } else if (response.code() == 200){
                    Toast.makeText(RegisterActivity.this, getString(R.string.user_created),Toast.LENGTH_SHORT).show();
                    gotoLogin();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(RegisterActivity.this,getString(R.string.no_server_connection),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
    }
}

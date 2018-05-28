package tim.project.travellerapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import tim.project.travellerapp.clients.UserClient;
import tim.project.travellerapp.models.User;

import static tim.project.travellerapp.Constants.REST_API_ADDRESS;

public class RegisterActivity extends Activity {

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

    private void registerUser(User user) {


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(REST_API_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        UserClient client = retrofit.create(UserClient.class);

        Call<Void> call = client.createAccount(user);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 409){
                    Toast.makeText(RegisterActivity.this, getString(R.string.user_duplicate),Toast.LENGTH_SHORT).show();
                } else if (response.code() == 200){
                    Toast.makeText(RegisterActivity.this, getString(R.string.user_created),Toast.LENGTH_SHORT).show();
                    gotoLogin();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

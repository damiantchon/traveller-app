package tim.project.travellerapp.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tim.project.travellerapp.Constants;
import tim.project.travellerapp.activities.LoginActivity;
import tim.project.travellerapp.clients.ApiClient;

import static tim.project.travellerapp.helpers.AuthenticationHelper.clearSharedPreferences;

public class DeleteAccountPreference extends DialogPreference {
    public DeleteAccountPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(Constants.REST_API_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.build();

            ApiClient client = retrofit.create(ApiClient.class);

            Long userId = LoginActivity.preferences.getLong("UserId", 0);
            String token = LoginActivity.preferences.getString("Token", null);

            Call<Void> call = client.desactivateAccount(userId, token);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.code() == 200) {
                        clearSharedPreferences();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        getContext().startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });

        } else if (which == DialogInterface.BUTTON_NEGATIVE) {

        }
    }
}

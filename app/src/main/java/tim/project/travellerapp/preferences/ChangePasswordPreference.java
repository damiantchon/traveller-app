package tim.project.travellerapp.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Field;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tim.project.travellerapp.Constants;
import tim.project.travellerapp.R;
import tim.project.travellerapp.activities.LoginActivity;
import tim.project.travellerapp.clients.ApiClient;
import tim.project.travellerapp.models.PasswordChange;

import static tim.project.travellerapp.helpers.AuthenticationHelper.passwordValid;
import static tim.project.travellerapp.helpers.AuthenticationHelper.passwordsEqual;

public class ChangePasswordPreference extends DialogPreference {

    String oldPassword;
    String newPassword;
    String newRePassword;

    private EditText editTextOldPassword;
    private EditText editTextNewPassword;
    private EditText editTextNewPassword2;

    public ChangePasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_change_password);
        //Todo Make it more flexible (@String)
        setPositiveButtonText("Ok");
        setNegativeButtonText("Cancel");

    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        editTextOldPassword = (EditText) view.findViewById(R.id.password_old);
        editTextNewPassword = (EditText) view.findViewById(R.id.password_new);
        editTextNewPassword2 = (EditText) view.findViewById(R.id.password_new2);
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {

            //New passwords different
            if(!passwordsEqual(editTextNewPassword.getText().toString(),
                    editTextNewPassword2.getText().toString())) {

                preventFromClosing(dialog, true);
                Toast.makeText(getContext(), R.string.settings_passwords_differ, Toast.LENGTH_SHORT).show();

            } else if (!passwordValid(editTextNewPassword.getText().toString())){

                preventFromClosing(dialog, true);
                Toast.makeText(getContext(), "Passwords too short (minimum 6 characters)", Toast.LENGTH_SHORT).show();
            } else {
                preventFromClosing(dialog, false);

                PasswordChange passwordChange = new PasswordChange(
                        LoginActivity.preferences.getLong("UserId", -1),
                        editTextOldPassword.getText().toString(),
                        editTextNewPassword.getText().toString());

                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl(Constants.REST_API_ADDRESS)
                        .addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit = builder.build();
                ApiClient client = retrofit.create(ApiClient.class);

                String token = LoginActivity.preferences.getString("Token", null);

                Call<Void> call = client.changePassword(passwordChange, token);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(getContext(), R.string.settings_password_changed , Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 409) {
                            Toast.makeText(getContext(), R.string.settings_password_not_changed, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Something went wrong! OMEGALUL", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            Toast.makeText(getContext(), "LULULULU", Toast.LENGTH_SHORT).show();

            preventFromClosing(dialog, false);

        }
    }

    public void preventFromClosing(DialogInterface dialog, Boolean prevent) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, !prevent);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

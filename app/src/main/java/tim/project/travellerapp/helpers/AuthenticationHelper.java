package tim.project.travellerapp.helpers;


import android.content.SharedPreferences;

import static tim.project.travellerapp.activities.LoginActivity.preferences;

public class AuthenticationHelper {
    public static void clearSharedPreferences() {
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove("Token");
        editor.remove("UserId");
        editor.remove("Admin");
        editor.remove("Username");
        editor.remove("Active");
        editor.remove("UserRoleId");

        editor.apply();
    }
}

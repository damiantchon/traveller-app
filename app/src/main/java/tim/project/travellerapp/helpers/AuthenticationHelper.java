package tim.project.travellerapp.helpers;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class AuthenticationHelper {
    public static void clearSharedPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove("Token");
        editor.remove("UserId");
        editor.remove("Admin");
        editor.remove("Username");
        editor.remove("Active");
        editor.remove("UserRoleId");

        editor.apply();
    }

    public static boolean passwordsEqual(String passwordOne, String passwordTwo) {
        return passwordOne.equals(passwordTwo);
    }

    public static boolean passwordValid(String password) {
        return password.length() >= 6;
    }
}

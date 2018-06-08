package tim.project.travellerapp.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import tim.project.travellerapp.R;

public class SettingsActivity extends PreferenceActivity {

    private Preference customPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load Settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MainSettingsFragment()).commit();
    }

    public static class MainSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);


        }
    }
}

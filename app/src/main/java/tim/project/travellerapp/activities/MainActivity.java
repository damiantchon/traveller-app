package tim.project.travellerapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


import tim.project.travellerapp.R;

import static tim.project.travellerapp.activities.LoginActivity.preferences;

public class MainActivity extends Activity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.token_view);

        textView.setText(preferences.getString("Token", "Empty"));

    }
}

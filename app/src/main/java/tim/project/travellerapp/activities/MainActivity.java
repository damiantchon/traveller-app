package tim.project.travellerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import tim.project.travellerapp.R;

import static tim.project.travellerapp.helpers.AuthenticationHelper.clearSharedPreferences;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.settings:
                //help
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.about:

                //Intent settings = new Intent(this, SettingsActivity.class);
                //startActivity(settings);
                break;
            case R.id.logout:
                Toast.makeText(getApplicationContext(), R.string.succesfull_logout, Toast.LENGTH_SHORT).show();

                clearSharedPreferences(getApplicationContext());

                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    public void onToVisitClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ToVisitActivity.class);
        startActivity(intent);
    }

    public void onVisitedClick(View view) {
        Intent intent = new Intent(getApplicationContext(), VisitedActivity.class);
        startActivity(intent);
    }


    public void showMapClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ToVisitMapsActivity.class);
        startActivity(intent);
    }

    public void addNewPlaceClick(View view) {

        Intent intent = new Intent(getApplicationContext(), AddNewPlaceMapsActivity.class);
        startActivity(intent);
    }
}

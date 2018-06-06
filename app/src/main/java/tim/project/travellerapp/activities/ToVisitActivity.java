package tim.project.travellerapp.activities;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tim.project.travellerapp.Constants;
import tim.project.travellerapp.R;
import tim.project.travellerapp.adapters.VisitAdapter;
import tim.project.travellerapp.clients.ApiClient;
import tim.project.travellerapp.models.Place;

public class ToVisitActivity extends AppCompatActivity {

    @BindView(R.id.to_visit_recycler_view)
    RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;

    private List<Place> placeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_visit);

        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager mStaggeredGridManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mStaggeredGridManager);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Constants.REST_API_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        ApiClient client = retrofit.create(ApiClient.class);
        String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Token", null);

        Call<List<Place>> call = client.getActivePlaces(token);

        call.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                if (response.code() == 200) {

                    placeList = response.body();

                    ArrayList<Place> arrayList = new ArrayList<>(placeList);

                    Toast.makeText(ToVisitActivity.this, placeList.get(0).getName(), Toast.LENGTH_SHORT).show();

                    VisitAdapter adapter = new VisitAdapter(arrayList);

                    mRecyclerView.setAdapter(adapter);

                } else {
                    Toast.makeText(ToVisitActivity.this, "No failure but still shitty response :(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                Toast.makeText(ToVisitActivity.this, "Something went wrong! :(", Toast.LENGTH_SHORT).show();
            }
        });


        //mAdapter = new MyAdapter

    }
}

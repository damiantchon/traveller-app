package tim.project.travellerapp.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
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
import tim.project.travellerapp.adapters.VisitedAdapter;
import tim.project.travellerapp.clients.ApiClient;
import tim.project.travellerapp.models.Place;
import tim.project.travellerapp.models.Visit;
import tim.project.travellerapp.models.VisitedPlace;

public class VisitedActivity extends AppCompatActivity {

    @BindView(R.id.visited_recycler_view)
    RecyclerView mRecyclerView;

    private List<Place> placeList;
    private List<Visit> visitList;
    private List<VisitedPlace> visitedPlaceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited);

        visitedPlaceList = new ArrayList<>();

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
        long userId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("UserId", 0L);

        Call<List<Place>> call = client.getVisitedPlaces(userId, token);

        call.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(@NonNull Call<List<Place>> call, @NonNull Response<List<Place>> response) {
                if (response.code() == 200) {
                    placeList = response.body();
                    if(placeList != null) {
                        for (Place place : placeList) {
                            visitedPlaceList.add(new VisitedPlace(place));
                            Collections.sort(visitedPlaceList, (o1, o2) -> (int)(o1.getId() - o2.getId()));
                        }
                    }
                    //Callback inside callback :)
                    Call<List<Visit>> secondCall = client.getVisitedVisits(userId, token);
                    secondCall.enqueue(new Callback<List<Visit>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<Visit>> call, @NonNull Response<List<Visit>> response) {
                            if(response.code() == 200) {
                                visitList = response.body();
                                if(visitList != null) {
                                    Collections.sort(visitList, ((o1, o2) -> (int)(o1.getPlaceId() - o2.getPlaceId())));
                                    int len = visitedPlaceList.size();
                                    for (int i = 0; i < len; i++) {
                                        visitedPlaceList.get(i).setTimestamp(visitList.get(i).getDate());
                                        visitedPlaceList.get(i).setVisible(visitList.get(i).isVisible());
                                        visitedPlaceList.get(i).setVisited(visitList.get(i).isVisited());
                                    }
                                    ArrayList<VisitedPlace> arrayList = new ArrayList<>(visitedPlaceList);

                                    VisitedAdapter adapter = new VisitedAdapter(arrayList);

                                    mRecyclerView.setAdapter(adapter);

                                }
                            } else {
                                Toast.makeText(VisitedActivity.this, "No failure but still shitty response :(", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<Visit>> call, @NonNull Throwable t) {
                            Toast.makeText(VisitedActivity.this, "Something went wrong! :(", Toast.LENGTH_SHORT).show();
                        }
                    });
                    // FIN
                } else {
                    Toast.makeText(VisitedActivity.this, "No failure but still shitty response :(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Place>> call, @NonNull Throwable t) {
                Toast.makeText(VisitedActivity.this, "Something went wrong! :(", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

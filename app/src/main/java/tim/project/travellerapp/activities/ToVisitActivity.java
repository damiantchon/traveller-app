package tim.project.travellerapp.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tim.project.travellerapp.R;
import tim.project.travellerapp.adapters.VisitAdapter;
import tim.project.travellerapp.clients.ApiClient;
import tim.project.travellerapp.helpers.ApiHelper;
import tim.project.travellerapp.models.Place;
import tim.project.travellerapp.models.Visit;
import tim.project.travellerapp.models.VisitedPlace;
import tim.project.travellerapp.models.request_body_models.VisitVisited;

public class ToVisitActivity extends AppCompatActivity  {

    @BindView(R.id.to_visit_recycler_view)
    RecyclerView mRecyclerView;

    private List<Place> placeList;
    private List<Visit> visitList;
    private List<VisitedPlace> visitedPlaceList;
    ArrayList<Place> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_visit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        toolbar.setTitle(R.string.places_to_visit_toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        visitedPlaceList = new ArrayList<>();

        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager mStaggeredGridManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mStaggeredGridManager);

        ApiClient client = ApiHelper.getApiClient();

        String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Token", null);
        long userId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("UserId", 0L);

        //Change depending on string.
        Call<List<Place>> call = client.getPlacesToVisit(userId, token);

        call.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(@NonNull Call<List<Place>> call, @NonNull Response<List<Place>> response) {
                if (response.code() == 200) {

                    placeList = response.body();

                    arrayList = new ArrayList<>(placeList);

                    VisitAdapter adapter = new VisitAdapter(arrayList);
                    mRecyclerView.setAdapter(adapter);

                    if(placeList != null) {
                        for (Place place : placeList) {
                            visitedPlaceList.add(new VisitedPlace(place));
                            Collections.sort(visitedPlaceList, (o1, o2) -> (int)(o1.getId() - o2.getId()));
                        }
                    }

                    Call<List<Visit>> secondCall = client.getNotVisitedVisits(userId, token);
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
                                        visitedPlaceList.get(i).setVisitId(visitList.get(i).getId());
                                    }
                                    ArrayList<VisitedPlace> arrayList = new ArrayList<>(visitedPlaceList);
                                }
                            } else {
                                Toast.makeText(ToVisitActivity.this, "No failure but still shitty response :(", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<Visit>> call, @NonNull Throwable t) {
                            Toast.makeText(ToVisitActivity.this, "Something went wrong! :(", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(ToVisitActivity.this, "No failure but still shitty response :(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Place>> call, @NonNull Throwable t) {
                Toast.makeText(ToVisitActivity.this, "Something went wrong! :(", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = item.getGroupId();

        ApiClient client = ApiHelper.getApiClient();

        if(item.getItemId() == getResources().getInteger(R.integer.action_visited_id)) {

            String timestamp = String.valueOf(System.currentTimeMillis() - 10000);

            long userId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("UserId", -1L);
            long visitId = visitedPlaceList.get(item.getGroupId()).getVisitId();

            VisitVisited visitVisited = new VisitVisited(timestamp, visitId, userId);

            Toast.makeText(this, String.valueOf(userId) + ", " + String.valueOf(visitId) + ", " + String.valueOf(timestamp), Toast.LENGTH_SHORT).show();

            String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Token", null);
            Call<Void> call = client.setVisitAsVisited(visitVisited, token);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if(response.code() == 200) {

                        Toast.makeText(ToVisitActivity.this, arrayList.get(item.getGroupId()).getName() + " visited!", Toast.LENGTH_SHORT).show();

                        arrayList.remove(item.getGroupId());
                        visitedPlaceList.remove(item.getGroupId());

                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    } else {
                        Toast.makeText(ToVisitActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(ToVisitActivity.this, "Something went really wrong :(", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (item.getItemId() == getResources().getInteger(R.integer.action_delete_id)) {

            long userId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("UserId", -1L);
            long visitId = visitedPlaceList.get(item.getGroupId()).getVisitId();
            String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Token", null);

            Call<Void> call = client.deleteNotVisitedVisit(visitId, userId, token);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if(response.code() == 200) {
                        Toast.makeText(ToVisitActivity.this, "Visit deleted!", Toast.LENGTH_SHORT).show();

                        arrayList.remove(item.getGroupId());
                        visitedPlaceList.remove(item.getGroupId());

                        mRecyclerView.getAdapter().notifyDataSetChanged();

                    } else {
                        Toast.makeText(ToVisitActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(ToVisitActivity.this, "Something went really wrong :(", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
    }
}

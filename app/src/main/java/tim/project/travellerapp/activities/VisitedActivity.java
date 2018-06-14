package tim.project.travellerapp.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tim.project.travellerapp.Constants;
import tim.project.travellerapp.R;
import tim.project.travellerapp.adapters.VisitedAdapter;
import tim.project.travellerapp.clients.ApiClient;
import tim.project.travellerapp.helpers.ApiHelper;
import tim.project.travellerapp.models.Place;
import tim.project.travellerapp.models.Visit;
import tim.project.travellerapp.models.VisitedPlace;
import tim.project.travellerapp.models.request_body_models.NewPhoto;


@RuntimePermissions
public class VisitedActivity extends AppCompatActivity {

    @BindView(R.id.visited_recycler_view)
    RecyclerView mRecyclerView;

    private int CAMERA;
    private int GALLERY;

    String mCurrentPhotoPath;
    Bitmap mBitmap;
    long mChoosenPlace;

    private List<Place> placeList;
    private List<Visit> visitList;
    private List<VisitedPlace> visitedPlaceList;
    ArrayList<VisitedPlace> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited);
        visitedPlaceList = new ArrayList<>();

        CAMERA = getResources().getInteger(R.integer.camera_request_code);
        GALLERY = getResources().getInteger(R.integer.gallery_request_code);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        toolbar.setTitle(R.string.visited_places_toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager mStaggeredGridManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mStaggeredGridManager);

        ApiClient client = ApiHelper.getApiClient();

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
                    //Callback inside callback - not proud of this :)
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
                                    arrayList = new ArrayList<>(visitedPlaceList);

                                    VisitedAdapter adapter = new VisitedAdapter(arrayList);

                                    mRecyclerView.setAdapter(adapter);

                                }
                            } else {
                                Toast.makeText(VisitedActivity.this, "No failure but still shitty response :(", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<Visit>> call, @NonNull Throwable t) {
                            Toast.makeText(VisitedActivity.this, getString(R.string.no_server_connection), Toast.LENGTH_SHORT).show();
                        }
                    });
                    // FIN
                } else {
                    Toast.makeText(VisitedActivity.this, "No failure but still shitty response :(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Place>> call, @NonNull Throwable t) {
                Toast.makeText(VisitedActivity.this, getString(R.string.no_server_connection), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,1, getString(R.string.action_clear_history_visited));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            if (arrayList.size() > 0) {
                ApiClient client = ApiHelper.getApiClient();

                long userId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong("UserId", -1L);
                String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Token", null);

                Call<Void> call = client.clearVisitHistory(userId, token);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(VisitedActivity.this, getString(R.string.history_cleared_successfully), Toast.LENGTH_SHORT).show();
                            visitedPlaceList.clear();
                            arrayList.clear();
                            mRecyclerView.getAdapter().notifyDataSetChanged();

                        } else {
                            Toast.makeText(VisitedActivity.this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(VisitedActivity.this, getString(R.string.no_server_connection), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(VisitedActivity.this, getString(R.string.no_history_to_clear), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = item.getGroupId();
        mChoosenPlace = visitedPlaceList.get(position).getId();
        ApiClient client = ApiHelper.getApiClient();

        if(item.getItemId() == getResources().getInteger(R.integer.action_add_photo_visited_id)) {

            showPictureDialog();
        }

        return super.onContextItemSelected(item);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getString(R.string.select_action));
        String[] pictureDialogItems = {
                getString(R.string.select_from_gallery),
                getString(R.string.select_from_camera)};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                VisitedActivityPermissionsDispatcher.takePhotoFromCameraWithPermissionCheck(VisitedActivity.this);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    void choosePhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "tim.project.travellerapp.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, CAMERA);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        VisitedActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setTitle("Permission needed")
                .setMessage("This permision is needed to access camera.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                }).show();
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void onCameraDenied() {
        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void onNeverAskAgain() {
        Toast.makeText(this, "Permission denied - change permissions in settings to access this feature.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                displayAlertDialog(data);
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    displayCameraAlertDialog();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void displayAlertDialog(Intent data) {
        ImageView imageView = new ImageView(this);
        Uri contentUri = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
            mBitmap = bitmap.copy(bitmap.getConfig(), true);
            imageView.setImageBitmap(bitmap);

            LayoutInflater inflater = getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.dialog_display_photo_to_add, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.accept_photo_title))
                    .setView(dialogView)
                    //.setView(inflater.inflate(R.layout.dialog_display_photo_to_add, true))
                    .setPositiveButton(getString(R.string.ok_string), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SendBitmapToBackendTask sendBitmapToBackendTask = new SendBitmapToBackendTask(mChoosenPlace);
                            sendBitmapToBackendTask.execute(bitmap);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Cancel
                        }
                    })
                    .setCancelable(true);

            AlertDialog dialog = builder.create();
            ImageView imageV = (ImageView) dialogView.findViewById(R.id.photo_to_add);
            imageV.setImageBitmap(bitmap);
            dialog.show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayCameraAlertDialog() throws IOException {
        ImageView imageView = new ImageView(this);
        File file = new File(mCurrentPhotoPath);
        
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            mBitmap = bitmap.copy(bitmap.getConfig(), true);
            LayoutInflater inflater = getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.dialog_display_photo_to_add, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("Accept image")
                    .setView(dialogView)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        SendBitmapToBackendTask sendBitmapToBackendTask = new SendBitmapToBackendTask(mChoosenPlace);
                        sendBitmapToBackendTask.execute(bitmap);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Cancel
                        }
                    })
                    .setCancelable(true);

            AlertDialog dialog = builder.create();
            ImageView imageV = (ImageView) dialogView.findViewById(R.id.photo_to_add);
            imageV.setImageBitmap(bitmap);
            dialog.show();
        } else {
            Toast.makeText(this, "Bitmap null :(", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
    }

    private class SendBitmapToBackendTask extends AsyncTask<Bitmap, Void, Boolean> {

        long mPlaceId;

        int mWorked = -1;

        public SendBitmapToBackendTask() {
            super();
        }

        public SendBitmapToBackendTask(long placeId) {
            this.mPlaceId = placeId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Boolean doInBackground(Bitmap... bitmaps) {
            ApiClient client = ApiHelper.getApiClient();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = bitmaps[0];
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
            //mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] byteArray = stream.toByteArray();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            long userId = preferences.getLong("UserId", 0L);
            String token = preferences.getString("Token", null);

            String timestamp = String.valueOf(System.currentTimeMillis() - 10000);
            NewPhoto photo = new NewPhoto(byteArray, timestamp, userId, mChoosenPlace);
            Call<Void> call = client.addNewPhoto(photo, token);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.code() == 200) {
                        Toast.makeText(VisitedActivity.this, getString(R.string.photo_uploaded_successfully), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VisitedActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(VisitedActivity.this, getString(R.string.no_server_connection), Toast.LENGTH_SHORT).show();
                }
            });

            return false;
        }
    }

}

package tim.project.travellerapp.clients;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import tim.project.travellerapp.models.PasswordChange;
import tim.project.travellerapp.models.Place;
import tim.project.travellerapp.models.User;
import tim.project.travellerapp.models.UserDetails;

public interface ApiClient {

    @Headers("Content-Type: application/json")
    @POST("user/register/en")
    Call<Void> createAccount(@Body User user);

    @POST("/user/login")
    Call<Void> loginUser(@Body User user);

    @GET("/user/details/{userId}")
    Call<UserDetails> getUserDetails(@Path("userId") Long userId, @Header("Token") String token);

    @PUT("/user/changePassword/en")
    Call<Void> changePassword(@Body PasswordChange passwordChange, @Header("Token") String token);

    @PUT("/user/deactivate/{userId}/en")
    Call<Void> desactivateAccount(@Path("userId") Long userId, @Header("Token") String token);

    @GET("/place/all")
    Call<List<Place>> getActivePlaces(@Header("Token") String token);
}

package tim.project.travellerapp.clients;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import tim.project.travellerapp.models.Place;
import tim.project.travellerapp.models.User;
import tim.project.travellerapp.models.UserDetails;
import tim.project.travellerapp.models.Visit;
import tim.project.travellerapp.models.request_body_models.NewPlace;
import tim.project.travellerapp.models.request_body_models.NewVisit;
import tim.project.travellerapp.models.request_body_models.PasswordChange;
import tim.project.travellerapp.models.request_body_models.VisitVisited;

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

    @POST("/visit/add/en")
    Call<List<Visit>> addNewVisit(@Body NewVisit newVisit, @Header("Token") String token);

    @GET("/place/allVisitedPlaces/{userId}")
    Call<List<Place>> getVisitedPlaces(@Path("userId") Long userId, @Header("Token") String token);

    @GET("/place/allNotVisitedPlaces/{userId}")
    Call<List<Place>> getPlacesToVisit(@Path("userId") Long userId, @Header("Token") String token);

    @GET("/visit/myVisitedPlaces/{userId}")
    Call<List<Visit>> getVisitedVisits(@Path("userId") Long userId, @Header("Token") String token);

    @GET("/visit/myNotVisitedPlaces/{userId}")
    Call<List<Visit>> getNotVisitedVisits(@Path("userId") Long userId, @Header("Token") String token);

    @PUT("/visit/selectPlaceAsVisited/en")
    Call<Void> setVisitAsVisited(@Body VisitVisited visitVisited, @Header("Token") String token);

    @DELETE("/visit/deleteNotVisitedPlace/{visitId}/{userId}/en")
    Call<Void> deleteNotVisitedVisit(@Path("visitId") long visitId, @Path("userId") long userId, @Header("Token") String token);

    @POST("/place/new/en")
    Call<Void> addNewPlace(@Body NewPlace newPlace, @Header("Token") String token);
}

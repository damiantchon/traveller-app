package tim.project.travellerapp.clients;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import tim.project.travellerapp.models.User;

public interface UserClient {

    @Headers("Content-Type: application/json")
    @POST("user/register/en")
    Call<Void> createAccount(@Body User user);

    @POST("/user/login")
    Call<Void> loginUser(@Body User user);
}

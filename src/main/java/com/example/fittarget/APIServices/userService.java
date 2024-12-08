package com.example.fittarget.APIServices;


import com.example.fittarget.APIRequests.SignInRequest;
import com.example.fittarget.APIResponses.SignInResponse;
import com.example.fittarget.objects.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface userService {

    @POST("/user/signin")
    Call<SignInResponse> signIn(@Body SignInRequest signInRequest);

    @POST("/user/signup")
    Call<User> createUser(@Body User user);

    @GET("/users/{user_email}")
    Call<User> getUser(@Path("user_email") String userEmail);

    @PUT("/users/{user_id}")
    Call<User> updateUser(@Path("user_id") int userId, @Body User user);

    @DELETE("/users/{user_id}")
    Call<User> deleteUser(@Path("user_id") int userId);
}



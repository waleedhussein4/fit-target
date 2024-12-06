package com.example.fittarget;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface userService {
        @GET("/users")
        Call<List<User>> getUsers();

        @POST("/user/signup")
        Call<User> createUser(@Body User user);

        @GET("/users/{user_email}")
        Call<User> getUser(@Path("user_email") String userEmail);

        @PUT("/users/{user_id}")
        Call<User> updateUser(@Path("user_id") int userId, @Body User user);

        @DELETE("/users/{user_id}")
        Call<User> deleteUser(@Path("user_id") int userId);
    }



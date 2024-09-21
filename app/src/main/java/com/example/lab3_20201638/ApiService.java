package com.example.lab3_20201638;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("auth/login")
    Call<Login_Resp> login(@Body Login_pedido loginRequest);

    @GET("todos/user/{userId}")
    Call<Tarea_Resp> getUserTasks(@Path("userId") int userId);

}

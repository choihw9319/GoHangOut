package com.example.gohangout;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @PATCH("/account/update/")
    Call<PostData> createPost(@Body PostData postData);

    @GET("/account/find/") // GET 요청 예
    Call<ServerIdResponse> getServerId();

    @DELETE("/account/delete/{id}")
    Call<Void> deleteAccount(@Path("id") String id);
}

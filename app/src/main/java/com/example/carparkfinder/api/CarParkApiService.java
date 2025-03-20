package com.example.carparkfinder.api;

import com.example.carparkfinder.model.CarParkApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CarParkApiService {
    @GET("v1/transport/carpark-availability")
    Call<CarParkApiResponse> getCarParkAvailability();
}

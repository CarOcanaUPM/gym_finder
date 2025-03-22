package com.example.gymfindermadrid;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GimnasiosApiService {

    @GET("place/nearbysearch/json")
    Observable<GimnasiosResponse> getGimnasios(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("key") String apiKey
    );


    @GET("place/details/json")
    Observable<GimnasiosDetailsResponse> getGimnasioDetalles(
            @Query("placeid") String placeId,
            @Query("key") String apiKey
    );
}

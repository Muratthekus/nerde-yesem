package com.thekusch.nerdeyesem.data

import com.thekusch.nerdeyesem.data.model.detailed.DetailedResult
import com.thekusch.nerdeyesem.data.model.nearby.NearbyResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface EndPoints {

    @GET("geocode")
    fun nearbyRestaurants(@Header("user-key")token:String,
                          @Query("lat") lat :Double,
                          @Query("lon") lon :Double):Single<NearbyResult>

    @GET("restaurant")
    fun getRestaurant(@Header("user-key")token:String,
                      @Query("res_id") res_id:Int?):Single<DetailedResult>
}
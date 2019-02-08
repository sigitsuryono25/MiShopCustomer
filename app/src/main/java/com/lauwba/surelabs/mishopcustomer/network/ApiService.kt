package com.lauwba.ojollauwba.network

import com.lauwba.surelabs.mishopcustomer.Model.ResponseRoute
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("json")
    fun actionRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") api: String
    ): Observable<ResponseRoute>

}
package com.lauwba.surelabs.mishopcustomer.network

import com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model.ResponseRoute
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*


interface ApiService {

    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAJCgLy7k:APA91bEdZaRdM5lgN5_f6alKP0fgsFHUd5OpNMG9D6XIi4b_P6S76W_W9u_i3Ee_U98WYPMNnxOugCoMwqp1uPF5G_nYcdhjXZZ0UHnHX20Hi8aVN5zSI4rCiK2-z5W0V5GX_uIvrghf"
    )
    @POST("fcm/send")
    fun actionSendBook(@Body notification: Any): Observable<ResponseBody>


    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAJCgLy7k:APA91bEdZaRdM5lgN5_f6alKP0fgsFHUd5OpNMG9D6XIi4b_P6S76W_W9u_i3Ee_U98WYPMNnxOugCoMwqp1uPF5G_nYcdhjXZZ0UHnHX20Hi8aVN5zSI4rCiK2-z5W0V5GX_uIvrghf"
    )
    @POST("fcm/send")
    fun actionSendMessage(@Body message: Any): Observable<ResponseBody>

    @GET("json")
    fun actionRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") api: String
    ): Observable<ResponseRoute>

}
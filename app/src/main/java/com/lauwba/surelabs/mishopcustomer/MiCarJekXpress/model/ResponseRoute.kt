package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model

import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class ResponseRoute(

    @field:SerializedName("routes")
    val routes: List<RoutesItem?>? = null,

    @field:SerializedName("geocoded_waypoints")
    val geocodedWaypoints: List<GeocodedWaypointsItem?>? = null,

    @field:SerializedName("status")
    val status: String? = null
)
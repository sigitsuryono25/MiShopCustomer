package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class ResponseRoute(

    @field:SerializedName("routes")
	val routes: List<RoutesItem?>? = null,

    @field:SerializedName("geocoded_waypoints")
	val geocodedWaypoints: List<GeocodedWaypointsItem?>? = null,

    @field:SerializedName("status")
	val status: String? = null
)
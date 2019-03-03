package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model

import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class RoutesItem(

    @field:SerializedName("summary")
    val summary: String? = null,

    @field:SerializedName("copyrights")
    val copyrights: String? = null,

    @field:SerializedName("legs")
    val legs: List<LegsItem?>? = null,

    @field:SerializedName("warnings")
    val warnings: List<Any?>? = null,

    @field:SerializedName("bounds")
    val bounds: Bounds? = null,

    @field:SerializedName("overview_polyline")
    val overviewPolyline: OverviewPolyline? = null,

    @field:SerializedName("waypoint_order")
    val waypointOrder: List<Any?>? = null
)
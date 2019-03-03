package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model

import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class StepsItem(

    @field:SerializedName("duration")
    val duration: Duration? = null,

    @field:SerializedName("start_location")
    val startLocation: StartLocation? = null,

    @field:SerializedName("distance")
    val distance: Distance? = null,

    @field:SerializedName("travel_mode")
    val travelMode: String? = null,

    @field:SerializedName("html_instructions")
    val htmlInstructions: String? = null,

    @field:SerializedName("end_location")
    val endLocation: EndLocation? = null,

    @field:SerializedName("maneuver")
    val maneuver: String? = null,

    @field:SerializedName("polyline")
    val polyline: Polyline? = null
)
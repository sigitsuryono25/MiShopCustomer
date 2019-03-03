package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model

import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class GeocodedWaypointsItem(

    @field:SerializedName("types")
    val types: List<String?>? = null,

    @field:SerializedName("geocoder_status")
    val geocoderStatus: String? = null,

    @field:SerializedName("place_id")
    val placeId: String? = null
)
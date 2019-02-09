package com.lauwba.surelabs.mishopcustomer.MiCarJekXpress.model

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class GeocodedWaypointsItem(

	@field:SerializedName("types")
	val types: List<String?>? = null,

	@field:SerializedName("geocoder_status")
	val geocoderStatus: String? = null,

	@field:SerializedName("place_id")
	val placeId: String? = null
)
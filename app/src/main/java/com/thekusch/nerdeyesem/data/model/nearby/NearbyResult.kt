package com.thekusch.nerdeyesem.data.model.nearby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NearbyResult{

    @SerializedName("location")
    @Expose
    var location: UserLocation? = null

    @SerializedName("popularity")
    @Expose
    var popularity:Popularity? = null

    @SerializedName("nearby_restaurants")
    @Expose
    var nearbyRestaurants: List<NearbyRestaurant>? = null
}
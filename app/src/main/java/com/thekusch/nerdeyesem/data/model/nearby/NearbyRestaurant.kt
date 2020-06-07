package com.thekusch.nerdeyesem.data.model.nearby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NearbyRestaurant {

    @SerializedName("restaurant")
    @Expose
    var restaurant: Restaurant? = null

}
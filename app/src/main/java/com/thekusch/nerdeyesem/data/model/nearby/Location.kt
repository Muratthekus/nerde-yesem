package com.thekusch.nerdeyesem.data.model.nearby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Location {
    @SerializedName("address")
    @Expose
    var address:String? = null

    @SerializedName("locality")
    @Expose
    var locality:String? = null

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("zipcode")
    @Expose
    var zipcode: String? = null

    @SerializedName("country_id")
    @Expose
    var countryId: String? = null
}
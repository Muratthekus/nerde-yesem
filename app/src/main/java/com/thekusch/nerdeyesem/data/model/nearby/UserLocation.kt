package com.thekusch.nerdeyesem.data.model.nearby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserLocation {
    @SerializedName("entity_type")
    @Expose
    var entityType: String? = null

    @SerializedName("entity_id")
    @Expose
    var entityId: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("city_id")
    @Expose
    var cityId: String? = null

    @SerializedName("city_name")
    @Expose
    var cityName: String? = null

    @SerializedName("country_id")
    @Expose
    var countryId: String? = null

    @SerializedName("country_name")
    @Expose
    var countryName: String? = null

}
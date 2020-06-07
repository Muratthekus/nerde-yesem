package com.thekusch.nerdeyesem.data.model.nearby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.thekusch.nerdeyesem.data.model.UserRating

class Restaurant {
    @SerializedName("id")
    @Expose
    var id:String? = null

    @SerializedName("name")
    @Expose
    var name:String? = null

    @SerializedName("location")
    @Expose
    var location: Location? = null

    @SerializedName("cuisines")
    @Expose
    var cuisines:String? = null

    @SerializedName("currency")
    @Expose
    var currency:String? = null

    @SerializedName("phone_numbers")
    @Expose
    var phone_numbers:String? = null

    @SerializedName("user_rating")
    @Expose
    var userRating: UserRating? = null
}
package com.thekusch.nerdeyesem.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserRating {

    @SerializedName("aggregate_rating")
    @Expose
    var aggregate_rating:String? = null

    @SerializedName("rating_text")
    @Expose
    var rating_text:String? = null

    @SerializedName("rating_color")
    @Expose
    var rating_color:String? = null

    @SerializedName("votes")
    @Expose
    var votes: String? = null
}
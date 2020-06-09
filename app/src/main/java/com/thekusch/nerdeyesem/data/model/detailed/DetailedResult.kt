package com.thekusch.nerdeyesem.data.model.detailed

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thekusch.nerdeyesem.data.model.nearby.Location
import com.thekusch.nerdeyesem.data.model.UserRating

class DetailedResult {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("location")
    @Expose
    var location: Location? = null

    @SerializedName("average_cost_for_two")
    @Expose
    var averageCostForTwo: String? = null

    @SerializedName("timings")
    @Expose
    var timings:String? = null

    @SerializedName("user_rating")
    @Expose
    var userRating: UserRating? = null

    @SerializedName("highlights")
    @Expose
    var highlights:List<String>? = null

    @SerializedName("cuisines")
    @Expose
    var cuisines: String? = null

    @SerializedName("all_reviews_count")
    @Expose
    var allReviewsCount: String? = null

    @SerializedName("phone_numbers")
    @Expose
    var phoneNumbers: String? = null
}
package com.thekusch.nerdeyesem.data.model.detailed

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thekusch.nerdeyesem.data.model.nearby.Location
import com.thekusch.nerdeyesem.data.model.UserRating

class DetailedResult {
    @SerializedName("id")
    @Expose
    private val id: String? = null

    @SerializedName("name")
    @Expose
    private val name: String? = null

    @SerializedName("location")
    @Expose
    private val location: Location? = null

    @SerializedName("average_cost_for_two")
    @Expose
    private val averageCostForTwo: String? = null

    @SerializedName("price_range")
    @Expose
    private val priceRange: String? = null

    @SerializedName("currency")
    @Expose
    private val currency: String? = null

    @SerializedName("user_rating")
    @Expose
    private val userRating: UserRating? = null

    @SerializedName("highlights")
    @Expose
    var highlights:List<String>? = null

    @SerializedName("cuisines")
    @Expose
    private val cuisines: String? = null

    @SerializedName("all_reviews_count")
    @Expose
    private val allReviewsCount: String? = null

    @SerializedName("phone_numbers")
    @Expose
    private val phoneNumbers: String? = null
}
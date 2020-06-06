package com.thekusch.nerdeyesem.data.model.detailed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("name")
    @Expose
    private val name: String? = null

    @SerializedName("zomato_handle")
    @Expose
    private val zomatoHandle: String? = null

    @SerializedName("foodie_level")
    @Expose
    private val foodieLevel: String? = null

    @SerializedName("foodie_level_num")
    @Expose
    private val foodieLevelNum: String? = null

    @SerializedName("foodie_color")
    @Expose
    private val foodieColor: String? = null

}
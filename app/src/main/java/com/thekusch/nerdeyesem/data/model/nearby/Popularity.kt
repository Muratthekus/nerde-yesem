package com.thekusch.nerdeyesem.data.model.nearby

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Popularity {

    @SerializedName("top_cuisines")
    @Expose
    var top_cuisines:List<String>? = null

}
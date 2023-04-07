package com.project.soboro.data

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class DuplicateUserId (
    @SerializedName("userId")
    val userId : String,
)

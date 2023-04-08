package com.project.soboro.data

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class LoginInfo (
    @SerializedName("id")
    val id : String,
    val password: String
)

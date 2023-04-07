package com.project.soboro.data

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("userId")
    val userId : Int,
    val id : Int,
    val title : String,
    val body : String
)

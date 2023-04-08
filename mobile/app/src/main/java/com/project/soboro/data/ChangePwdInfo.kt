package com.project.soboro.data

import com.google.gson.annotations.SerializedName

data class ChangePwdInfo(
    @SerializedName("id")
    val id:String,
    val password:String
)

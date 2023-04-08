package com.project.soboro.data

import com.google.gson.annotations.SerializedName

data class SoboroUserInfo (
    @SerializedName("userId")
    val userId : String,
    val userName:String,
    val userEmail:String,
    val userPhone:String,
    val userAuthType:String,
    val userCreateTime:String,
    );

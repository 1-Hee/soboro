package com.project.soboro.data

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class RegistUserInfo (

    @SerializedName("userId")
    val userId : String,
    val userPassword: String,
    val userName:String,
    val userEmail:String,
    val userPhone:String,
    val userGender:String,
    val userTerm:Boolean,

    /*
    "userNo":2,
    "userId":"soboro123",

    "userName":"testName",
    "userEmail":"testEmail@domain.com",
    "userPhone":"01012345678",

    "userGender":"M",

    "userTerms":true,
    "userActive":true
     */
)

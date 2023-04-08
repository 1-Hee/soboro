package com.project.soboro.data

import com.google.gson.annotations.SerializedName
/*
{
  "userId": "string",
  "userName": "testName",
  "userEmail": "testEmail@domain.com",
  "userPhone": "01012345678",
  "userActive": true
}
 */
data class ModifyUserInfoData(
    @SerializedName("userId")
    val userId:String,
    val userName:String,
    val userEmail:String,
    val userPhone:String,
)

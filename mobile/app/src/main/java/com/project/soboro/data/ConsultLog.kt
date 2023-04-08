package com.project.soboro.data

import com.google.gson.annotations.SerializedName

data class ConsultLog(
    @SerializedName("consultingNo")
    val consultingNo:Int,
    val consultingVisitPlace:String,
    val consultingVisitDate:String,
    val consultingVisitClass: String
)

package com.project.soboro.data

import com.google.gson.annotations.SerializedName

/*
{
consultingNo=2.0,
consultingVisitPlace=봉명동 농협,
consultingVisitDate=2023. 03. 20.,
consultingVisitClass=은행,
videoLocation=www.video2.com
}
 */
data class ConsultDetailInfo(
    @SerializedName("consultingNo")
    val consultingNo:Int,
    val consultingVisitPlace:String,
    val consultingVisitDate:String,
    val consultingVisitClass:String,
    val videoLocation:String
)

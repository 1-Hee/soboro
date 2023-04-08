package com.project.soboro.data

import android.widget.TextView

data class CheckId(
    val result:Boolean, val okString:String,
    val rejectString:String, val registGuideText:TextView,
    val currectColor:Int, val wrongColor:Int
);
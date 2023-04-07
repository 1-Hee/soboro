package com.project.soboro.activity.regist

import android.R.string
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.project.soboro.R
import com.project.soboro.databinding.ActivitySoboroUseOfTermBinding
import com.project.soboro.util.SoboroConstant
import com.project.soboro.util.SoboroRawReader
import java.io.*

class SoboroUseOfTermActivity : AppCompatActivity() {

    lateinit var b:ActivitySoboroUseOfTermBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soboro_use_of_term)
        b = ActivitySoboroUseOfTermBinding.inflate(layoutInflater);
        setContentView(b.root);
        SoboroConstant.tempContext = this;

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        val soboroUseOfTermText = findViewById<TextView>(R.id.soboroUseOfTermText);
        SoboroRawReader.setText(soboroUseOfTermText, resources.openRawResource(R.raw.use_of_term));

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
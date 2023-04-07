package com.project.soboro.activity.regist

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.project.soboro.R
import com.project.soboro.databinding.ActivityPrivacyStatementBinding
import com.project.soboro.util.SoboroConstant
import com.project.soboro.util.SoboroRawReader

class SoboroPrivacyStatementActivity : AppCompatActivity() {
    lateinit var b:ActivityPrivacyStatementBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_statement)
        b = ActivityPrivacyStatementBinding.inflate(layoutInflater);
        setContentView(b.root);
        SoboroConstant.tempContext = this;

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        // 텍스트 세팅
        val privacyStatementText = findViewById<TextView>(R.id.soboroPrivacyStatementText);
        SoboroRawReader.setText(privacyStatementText, resources.openRawResource(R.raw.privacy_statement));

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}